package com.westee.cake.service;

import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.google.gson.JsonObject;
import com.westee.cake.config.ApiSecurityConfig;
import com.westee.cake.config.QiniuConfig;
import com.westee.cake.config.WeChatExpressConfig;
import com.westee.cake.data.GoodsInfo;
import com.westee.cake.data.OrderInfo;
import com.westee.cake.entity.ExpressCargo;
import com.westee.cake.entity.ExpressCargoItem;
import com.westee.cake.entity.ExpressCreate;
import com.westee.cake.exceptions.HttpException;
import com.westee.cake.generate.Address;
import com.westee.cake.generate.AddressMapper;
import com.westee.cake.generate.ExpressInfo;
import com.westee.cake.generate.ExpressInfoExample;
import com.westee.cake.generate.ExpressInfoMapper;
import com.westee.cake.generate.Goods;
import com.westee.cake.global.GlobalVariable;
import com.westee.cake.util.AES_Enc;
import com.westee.cake.util.RSA_Sign;
import com.westee.cake.util.RequestUtil;
import com.westee.cake.validator.ExpressSendValidator;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.math.BigDecimal;
import java.util.ArrayList;
import java.util.Date;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.Objects;
import java.util.concurrent.atomic.AtomicInteger;
import java.util.stream.Collectors;

@Service
public class WeChatExpressService {
    private final AddressMapper addressMapper;
    private final ExpressInfoMapper expressInfoMapper;
    private final GoodsImageService goodsImageService;
    private final WxExpressService wxExpressService;
    private final OrderDeliveryScheduler orderDeliveryScheduler;
    private final QiniuConfig qiniuConfig;

    ObjectMapper objectMapper = new ObjectMapper();

    private static final Logger log = LoggerFactory.getLogger(WeChatExpressService.class);

    @Autowired
    public WeChatExpressService(AddressMapper addressMapper, GoodsImageService goodsImageService,
                                ExpressInfoMapper expressInfoMapper, WxExpressService wxExpressService,
                                OrderDeliveryScheduler orderDeliveryScheduler, QiniuConfig qiniuConfig) {
        this.addressMapper = addressMapper;
        this.expressInfoMapper = expressInfoMapper;
        this.goodsImageService = goodsImageService;
        this.wxExpressService = wxExpressService;
        this.orderDeliveryScheduler = orderDeliveryScheduler;
        this.qiniuConfig = qiniuConfig;
    }

    public HashMap<String, Object> doQueryExpress(String wxOrderId) {
        String url = getConcatUrl(ExpressInterface.QUERY_ORDER.getPath());
        HashMap<String, String> param = new HashMap<>();
        param.put("wx_order_id", wxOrderId);
        JsonObject data = AES_Enc.getData(param, ExpressInterface.QUERY_ORDER.getPath());
        HashMap<String, Object> weChatHeader = getWeChatHeader(data, ExpressInterface.QUERY_ORDER.getPath());
        String reqData = data.get("req_data").getAsString();
        HashMap<String, Object> o = RequestUtil.doSecurityPost(url, reqData, weChatHeader);
        if (o.get("errcode").equals(0)) {
            return o;
        }
        throw HttpException.badRequest((String) o.get("errmsg"));
    }

    /**
     * 取消订单
     * 未创建订单
     * 已创建订单
     *
     * @param wxOrderNo         微信out_trade_no
     * @param reason            取消订单理由
     * @return                  微信响应
     */
    public HashMap<String, Object> doCancelExpress(String wxOrderNo, Integer reason) {
        // 从quartz的scheduler删除job
        orderDeliveryScheduler.cancelOrderDelivery(wxOrderNo);

        // 发送请求 派送中订单会扣款
        String path = ExpressInterface.CANCEL_ORDER.getPath();
        String url = getConcatUrl(path);
        HashMap<String, Object> param = new HashMap<>();
        param.put("wx_order_id", wxOrderNo);
        param.put("cancel_reason_id", reason);
        JsonObject data = AES_Enc.getData(param, path);
        HashMap<String, Object> weChatHeader = getWeChatHeader(data, path);
        String reqData = data.get("req_data").getAsString();
        HashMap<String, Object> o = RequestUtil.doSecurityPost(url, reqData, weChatHeader);
        if (o.get("errcode").equals(0)) {
            return o;
        }
        throw HttpException.badRequest((String) o.get("errmsg"));
    }

    public void modifyDeliveryTime(String wxOrderNo, Date datetime) {
        orderDeliveryScheduler.rescheduleOrderDelivery(wxOrderNo, datetime);
    }

    public HashMap<String, Object> estimateExpressFee(ExpressSendValidator expressInfo) {
        return getExpressFeeResponse(expressInfo, ExpressInterface.CALCULATE_PRICE.getPath());
    }

    /**
     * 预估价格
     * 将以预估价格进行扣费
     */
    public HashMap<String, Object> getExpressFeeResponse(ExpressSendValidator expressInfo, String path) {
        if (Objects.nonNull(expressInfo) && expressInfo.isValid()) {
            String url = getConcatUrl(path);
            JsonObject data = AES_Enc.getData(expressInfo, path);
            String reqData = data.get("req_data").getAsString();
            return RequestUtil.doSecurityPost(url, reqData, getWeChatHeader(data, path));
        }
        log.error("获取配送信息参数不正确: {}", expressInfo);
        throw HttpException.badRequest("获取配送信息参数不正确");
    }

    /**
     * 创建快递单
     */
    public void doCreateExpress(ExpressCreate expressCreate) {
        String path = ExpressInterface.CREATE_ORDER.getPath();
        String url = getConcatUrl(path);
        JsonObject data = AES_Enc.getData(expressCreate, path);
        String reqData = data.get("req_data").getAsString();
        HashMap<String, Object> result = RequestUtil.doSecurityPost(url, reqData, getWeChatHeader(data, path));
        Double code = (Double) result.get("errcode");
        if (code == (0.0)) {
            wxExpressService.insertWxExpress(result);
        }
    }

    public HashMap<String, Object> getWeChatHeader(JsonObject data, String url) {
        HashMap<String, Object> headers = new HashMap<>();
        headers.put("Wechatmp-Appid", ApiSecurityConfig.getAppid());
        String signature = RSA_Sign.getSign(data, url);
        headers.put("Wechatmp-Signature", signature);
        long localTs = data.get("req_ts").getAsLong();
        headers.put("Wechatmp-TimeStamp", Long.toString(localTs));
        return headers;
    }

    public int getBuyGoodsNum(Goods goods, OrderInfo orderInfo) {
        for (GoodsInfo goodsInfo :
                orderInfo.getGoods()) {
            if (goodsInfo.getId() == goods.getId()) {
                return goodsInfo.getNumber();
            }
        }
        return 0;
    }

    public ExpressInfo insertExpressInfo(ExpressCreate expressCreate, String orderNo) {
        try {
            String s = objectMapper.writeValueAsString(expressCreate);
            ExpressInfo expressInfo = new ExpressInfo();
            expressInfo.setWxOrderNo(orderNo);
            expressInfo.setInfo(s);
            expressInfo.setCreatedAt(new Date());
            expressInfo.setUpdatedAt(new Date());
            expressInfoMapper.insert(expressInfo);
            return expressInfo;
        } catch (JsonProcessingException e) {
            throw new RuntimeException(e);
        }
    }

    public ExpressCreate collectExpressInfo(Map<Long, Goods> idToGoodsMap, BigDecimal finalPrice,
                                            OrderInfo orderInfo, String openid, String orderId) {
        System.out.println("下单时间：" + new Date());

        ExpressCreate expressCreate = new ExpressCreate();
        Address address = addressMapper.selectByPrimaryKey(orderInfo.getAddressId());
        expressCreate.setWx_store_id(WeChatExpressConfig.getWxStoreId());
        expressCreate.setUser_lat(address.getLatitude().toString());
        expressCreate.setUser_lng(address.getLongitude().toString());
        expressCreate.setUser_address(address.getAddress() + address.getName());
        expressCreate.setUser_phone(address.getPhoneNumber());
        expressCreate.setUser_name(address.getContact());
        expressCreate.setUser_openid(openid);
        expressCreate.setStore_order_id(orderId);
        expressCreate.setCallback_url(WeChatExpressConfig.getNotify());
        expressCreate.setUse_sandbox(1); // 1:使用沙箱环境; 使用测试沙箱环境，不需要充值运费就可以生成测试订单
        expressCreate.setOrder_detail_path("pages/order/list");
//        expressCreate.setPath(openid);

        ExpressCargo expressCargo = new ExpressCargo();
        String goodsNames = idToGoodsMap.values()
                .stream()
                .map(Goods::getName)
                .collect(Collectors.joining(","));
        expressCargo.setCargo_name(goodsNames);
        expressCargo.setCargo_price(finalPrice.intValue());
        expressCargo.setCargo_num(finalPrice.intValue());
        expressCargo.setCargo_type(1);  // 1 快餐
        expressCargo.setCargo_weight(getGoodsWeight(idToGoodsMap));  // 重量 克
        ArrayList<ExpressCargoItem> objects = new ArrayList<>();
        idToGoodsMap.values()
                .forEach(goods -> {
                    ExpressCargoItem expressCargoItem = new ExpressCargoItem();
                    expressCargoItem.setItem_name(goods.getName());
                    expressCargoItem.setItem_pic_url(qiniuConfig.getIMAGE_PREFIX() + goodsImageService.getGoodsImage(goods.getId()).get(0).getUrl());
                    expressCargoItem.setCount(getBuyGoodsNum(goods, orderInfo));
                    objects.add(expressCargoItem);
                });
        expressCargo.setItem_list(objects);

        expressCreate.setCargo(expressCargo);
        return expressCreate;
    }

    public ExpressInfo getCreateExpressInfoByOutTradeNo(String wxOrderNo) {
        ExpressInfoExample expressInfoExample = new ExpressInfoExample();
        expressInfoExample.createCriteria().andWxOrderNoEqualTo(wxOrderNo);
        List<ExpressInfo> expressInfos = expressInfoMapper.selectByExample(expressInfoExample);
        if (!expressInfos.isEmpty()) {
            try {
                return readStringAsExpressInfo(expressInfos.get(0).getInfo());
            } catch (JsonProcessingException e) {
                throw new RuntimeException(e);
            }
        }
        log.error("未找到对应创建快递单信息 outTradeNo：{}", wxOrderNo);
        throw HttpException.badRequest("未找到对应创建快递单信息 outTradeNo：{}" + wxOrderNo);
    }

    public ExpressInfo readStringAsExpressInfo(String expressInfo) throws JsonProcessingException {
        return objectMapper.readValue(expressInfo, ExpressInfo.class);
    }

    public String getConcatUrl(String basePath) {
        return basePath + "?access_token=" + GlobalVariable.INSTANCE.getAccessToken();
    }

    private int getGoodsWeight(Map<Long, Goods> idToGoodsMap) {
        AtomicInteger totalWeight = new AtomicInteger();
        idToGoodsMap.values().forEach(goods -> totalWeight.addAndGet(goods.getWeight()));
        return totalWeight.get();
    }

    public enum ExpressInterface {
        CALCULATE_PRICE("preaddorder"),
        CREATE_ORDER("addorder"),
        CANCEL_ORDER("cancelorder"),
        QUERY_ORDER("queryorder");

        private final String path;

        ExpressInterface(String path) {
            this.path = path;
        }

        public String getPath() {
            String BASE_URL = "https://api.weixin.qq.com/cgi-bin/express/intracity/";
//            String BASE_URL = "http://open.s.bingex.com";
            return BASE_URL + path;
        }
    }

    public enum ExpressCancelReason {
        NO_NEED("1"),
        INFO_ERR("2"),
        NO_EXPRESSMAN("3"),
        OTHER("99");

        private final String path;

        ExpressCancelReason(String path) {
            this.path = path;
        }

        public String getPath() {
            String BASE_URL = "https://api.weixin.qq.com/cgi-bin/express/intracity/";
//            String BASE_URL = "http://open.s.bingex.com";
            return BASE_URL + path;
        }
    }
}
