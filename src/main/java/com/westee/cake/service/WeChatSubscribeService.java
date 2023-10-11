package com.westee.cake.service;

import com.westee.cake.generate.Config;
import com.westee.cake.global.GlobalVariable;
import com.westee.cake.global.WeChatSubscribeTemplate;
import com.westee.cake.util.RequestUtil;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.math.BigDecimal;
import java.text.SimpleDateFormat;
import java.util.Date;
import java.util.HashMap;
import java.util.Map;
import java.util.Objects;

@Service
public class WeChatSubscribeService {
    private final ConfigService configService;

    @Autowired
    public WeChatSubscribeService(ConfigService configService) {
        this.configService = configService;
    }

    public Object sendSubscribe(String userOpenId, String templateId, HashMap<String, Object> data) {
        String url = "https://api.weixin.qq.com/cgi-bin/message/subscribe/send?access_token=" + GlobalVariable.INSTANCE.getAccessToken();
        HashMap<String, Object> params = new HashMap<>();
        params.put("touser", userOpenId); // openid
        params.put("template_id", templateId); // TEMPLATE_ID
        params.put("page", "page/order/list"); // 打开的页面
        if (Objects.nonNull(data)) {
            HashMap<String, Object> map = new HashMap<>();
            for (Map.Entry<String, Object> entry : data.entrySet()) {
                map.put(entry.getKey(), getParam(entry.getValue()));
            }
            params.put("data", map);
        }
        return RequestUtil.doNormalPost(url, params, null);
    }

    public void getParamsAndSendPlaceOrderSubscribe(String openId, String goodsName, BigDecimal price) {
        Config config = configService.getConfig();
        HashMap<String, Object> objectObjectHashMap = new HashMap<>();
        SimpleDateFormat formatter = new SimpleDateFormat("yyyy-MM-dd HH:mm:ss");
        Date date = new Date();
        String formattedDate = formatter.format(date);
        objectObjectHashMap.put("date4", formattedDate);
        objectObjectHashMap.put("thing5", goodsName);
        objectObjectHashMap.put("name8", config.getShopName()); // 商户名称
        objectObjectHashMap.put("thing3", config.getShopAddress());  // 商户地址
        objectObjectHashMap.put("amount12", "￥" + price);
        sendSubscribe(openId, WeChatSubscribeTemplate.INSTANCE.getOrder(), objectObjectHashMap);
    }

    public HashMap<String, Object> getParam(Object p) {
        HashMap<String, Object> stringObjectHashMap = new HashMap<>();
        stringObjectHashMap.put("value", p);
        return stringObjectHashMap;
    }
}
