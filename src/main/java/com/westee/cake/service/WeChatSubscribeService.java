package com.westee.cake.service;

import com.westee.cake.global.GlobalVariable;
import com.westee.cake.util.RequestUtil;

import java.io.IOException;
import java.text.SimpleDateFormat;
import java.util.Date;
import java.util.HashMap;

public class WeChatSubscribeService {

    public void sendSubscribe(String userOpenId, String templateId) throws IOException {
        String url = "https://api.weixin.qq.com/cgi-bin/message/subscribe/send?access_token=" + GlobalVariable.INSTANCE.getAccessToken();
        HashMap<String, Object> params = new HashMap<>();
        params.put("touser", userOpenId); // openid
        params.put("template_id", templateId); // TEMPLATE_ID
        params.put("page", "page/order/list"); // TEMPLATE_ID
        HashMap<String, Object> data = new HashMap<>();
        SimpleDateFormat formatter = new SimpleDateFormat("yyyy年MM月dd日 HH:mm");
        Date date = new Date();
        String formattedDate = formatter.format(date);
        data.put("date4", formattedDate);
        data.put("thing5", "商品名");
        data.put("name8", "x");
        data.put("thing3", "y");
        data.put("amount12", "￥" + 10.01);
        params.put("data", data);
        RequestUtil.doPost(url, params, null);
    }
}
