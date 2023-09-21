package com.westee.cake.service;

import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.google.gson.Gson;
import com.rabbitmq.client.Channel;
import com.westee.cake.config.WeChatExpressConfig;
import com.westee.cake.config.WxPayConfig;
import com.westee.cake.controller.WeChatExpressController;
import com.westee.cake.data.GoodsInfo;
import com.westee.cake.data.OrderInfo;
import com.westee.cake.entity.ExpressCargo;
import com.westee.cake.entity.ExpressCargoItem;
import com.westee.cake.entity.ExpressCreate;
import com.westee.cake.generate.Address;
import com.westee.cake.generate.AddressMapper;
import com.westee.cake.generate.ExpressInfo;
import com.westee.cake.generate.ExpressInfoMapper;
import com.westee.cake.generate.Goods;
import com.westee.cake.global.GlobalVariable;
import com.westee.cake.util.RequestUtil;
import org.springframework.amqp.core.Message;
import org.springframework.amqp.rabbit.annotation.RabbitListener;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.io.IOException;
import java.math.BigDecimal;
import java.util.ArrayList;
import java.util.Date;
import java.util.HashMap;
import java.util.Map;
import java.util.stream.Collectors;

@Service
public class WechatExpressService {
    private final AddressMapper addressMapper;
    private final ExpressInfoMapper expressInfoMapper;
    private final GoodsImageService goodsImageService;
    private final WxExpressService wxExpressService;
    private final WxPayConfig wxPayConfig;

    ObjectMapper objectMapper = new ObjectMapper();

    private final String IMAGE_PREFIX = "https://baking.sun-rising.net/";

    @Autowired
    public WechatExpressService(AddressMapper addressMapper, GoodsImageService goodsImageService, WxPayConfig wxPayConfig,
                                ExpressInfoMapper expressInfoMapper, WxExpressService wxExpressService) {
        this.addressMapper = addressMapper;
        this.expressInfoMapper = expressInfoMapper;
        this.goodsImageService = goodsImageService;
        this.wxExpressService = wxExpressService;
        this.wxPayConfig = wxPayConfig;
    }

    @RabbitListener(queues = "orderQueue")
    public void processDeliveryMessage(ExpressInfo expressInfo, Channel channel, Message message) throws IOException {
        try {
            System.out.println("收到配送消息时间：" + new Date());
            System.out.println("收到配送消息：" + expressInfo.getId());
            doCreateExpress(expressInfo.getInfo());
        } catch (Exception e) {
            System.out.println(e.getMessage());
        }
    }

    public void doQueryExpress(String wxOrderId) {
    }

    public void doCancelExpress(String wxOrderId, Integer reason) {
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
        expressCargo.setCargoWeight(1);  // 重量 克
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

    public void doCreateExpress(String expressInfo) throws IOException {
        ExpressCreate expressCreate = objectMapper.readValue(expressInfo, ExpressCreate.class);

        Object result = RequestUtil.doPost(WeChatExpressController.ExpressInterface.CREATE_ORDER.getPath() +
                "?access_token=" + GlobalVariable.INSTANCE.getAccessToken(), expressCreate, getWeChatHeader());
        System.out.println(result);
        wxExpressService.insertWxExpress(result);
    }

    public HashMap<String, String> getWeChatHeader() {
        HashMap<String, String> headers = new HashMap<>();
        headers.put("Wechatmp-Appid", wxPayConfig.getAPPID());
        headers.put("Wechatmp-TimeStamp", "");
        headers.put("Wechatmp-Signature", "");
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

    public enum XX {

    }
}
