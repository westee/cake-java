package com.westee.cake.service;

import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.core.type.TypeReference;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.google.gson.JsonObject;
import com.rabbitmq.client.Channel;
import com.westee.cake.config.ApiSecurityConfig;
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
import org.springframework.amqp.core.Message;
import org.springframework.amqp.rabbit.annotation.RabbitListener;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.io.IOException;
import java.math.BigDecimal;
import java.util.ArrayList;
import java.util.Date;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.concurrent.atomic.AtomicInteger;
import java.util.stream.Collectors;

@Service
public class WeChatExpressService {
    private final AddressMapper addressMapper;
    private final ExpressInfoMapper expressInfoMapper;
    private final GoodsImageService goodsImageService;
    private final WxExpressService wxExpressService;

    ObjectMapper objectMapper = new ObjectMapper();

    private final String IMAGE_PREFIX = "https://x.net/";

    private static final Logger log = LoggerFactory.getLogger(WeChatExpressService.class);

    @Autowired
    public WeChatExpressService(AddressMapper addressMapper, GoodsImageService goodsImageService,
                                ExpressInfoMapper expressInfoMapper, WxExpressService wxExpressService) {
        this.addressMapper = addressMapper;
        this.expressInfoMapper = expressInfoMapper;
        this.goodsImageService = goodsImageService;
        this.wxExpressService = wxExpressService;
    }

    @RabbitListener(queues = "orderQueue")
    public void processDeliveryMessage(ExpressInfo expressInfo, Channel channel, Message message) {
        try {
            System.out.println("收到配送消息时间：" + new Date());
            System.out.println("收到配送消息：" + expressInfo.getId());
            doCreateExpress(expressInfo.getInfo());
        } catch (Exception e) {
            System.out.println(e.getMessage());
        }
    }

    public HashMap<String, Object> doQueryExpress(String wxOrderId) {
        String url = getConcatUrl(ExpressInterface.QUERY_ORDER.getPath());
        try {
            HashMap<String, String> param = new HashMap<>();
            param.put("wx_order_id", wxOrderId);
            JsonObject data = AES_Enc.getData(param, ExpressInterface.QUERY_ORDER.getPath());
            HashMap<String, Object> weChatHeader = getWeChatHeader(data, ExpressInterface.QUERY_ORDER.getPath());
            String reqData = data.get("req_data").getAsString();
            Object o = RequestUtil.doSecurityPost(url, reqData, weChatHeader);
            HashMap<String, Object> res = objectMapper.convertValue(o, new TypeReference<HashMap<String, Object>>() {
            });
            if (res.get("errcode").equals(0)) {
                return res;
            }
            throw HttpException.badRequest((String) res.get("errmsg"));
        } catch (IOException e) {
            throw new RuntimeException(e);
        }
    }

    public void doCancelExpress(String wxOrderId, Integer reason) {
    }

    public HashMap<String, Object> estimateExpressFee(ExpressSendValidator expressInfo) {
        return getExpressFeeResponse(expressInfo, ExpressInterface.CALCULATE_PRICE.getPath());
    }

    /**
     * 预估价格
     * 将以预估价格进行扣费
     */
    public HashMap<String, Object> getExpressFeeResponse(ExpressSendValidator expressInfo, String path) {
        if (expressInfo.isValid()) {
            String url = getConcatUrl(path);
            try {
                JsonObject data = AES_Enc.getData(expressInfo, path);
                String reqData = data.get("req_data").getAsString();
                Object o = RequestUtil.doSecurityPost(url, reqData, getWeChatHeader(data, path));
                HashMap<String, Object> res = objectMapper.convertValue(o, new TypeReference<HashMap<String, Object>>() {
                });
                Double code = (Double) res.get("errcode");
                if (code == (0.0)) {
                    return res;
                }
                throw HttpException.badRequest("获取配送信息失败："+ res.get("errmsg"));
            } catch (IOException e) {
                throw new RuntimeException(e);
            }
        }
        log.error("获取配送信息参数不正确: {}", expressInfo);
        throw HttpException.badRequest("获取配送信息参数不正确");
    }

    /**
     * 创建快递单
     */
    public void doCreateExpress(String expressInfo) throws IOException {
        ExpressCreate expressCreate = objectMapper.readValue(expressInfo, ExpressCreate.class);

        Object result = RequestUtil.doNormalPost(getConcatUrl(ExpressInterface.CREATE_ORDER.getPath()), expressCreate,
                getWeChatHeader(null, ExpressInterface.CREATE_ORDER.getPath()));
        System.out.println(result);
        wxExpressService.insertWxExpress(result);
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
        expressCreate.setWxStoreId(WeChatExpressConfig.getWxStoreId());
        expressCreate.setUserLat(address.getLatitude().toString());
        expressCreate.setUserLng(address.getLongitude().toString());
        expressCreate.setUserAddress(address.getAddress() + address.getName());
        expressCreate.setUserPhone(address.getPhoneNumber());
        expressCreate.setUserName(address.getContact());
        expressCreate.setUserOpenid(openid);
        expressCreate.setStoreOrderId(orderId);
        expressCreate.setCallbackUrl(WeChatExpressConfig.getNotify());
        expressCreate.setUseSandbox(1); // 1:使用沙箱环境; 使用测试沙箱环境，不需要充值运费就可以生成测试订单
        expressCreate.setOrderDetailPath("pages/order/list");
//        expressCreate.setPath(openid);

        ExpressCargo expressCargo = new ExpressCargo();
        String goodsNames = idToGoodsMap.values()
                .stream()
                .map(Goods::getName)
                .collect(Collectors.joining(","));
        expressCargo.setCargoName(goodsNames);
        expressCargo.setCargoPrice(finalPrice.intValue());
        expressCargo.setCargoNum(finalPrice.intValue());
        expressCargo.setCargoType(1);  // 1 快餐
        expressCargo.setCargoWeight(getGoodsWeight(idToGoodsMap));  // 重量 克
        ArrayList<ExpressCargoItem> objects = new ArrayList<>();
        idToGoodsMap.values()
                .forEach(goods -> {
                    ExpressCargoItem expressCargoItem = new ExpressCargoItem();
                    expressCargoItem.setItemName(goods.getName());
                    expressCargoItem.setItemPicUrl(IMAGE_PREFIX + goodsImageService.getGoodsImage(goods.getId()).get(0).getUrl());
                    expressCargoItem.setCount(getBuyGoodsNum(goods, orderInfo));
                    objects.add(expressCargoItem);
                });
        expressCargo.setItemList(objects);

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
}
