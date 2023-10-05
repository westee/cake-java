package com.westee.cake.service;

import com.westee.cake.global.GlobalVariable;
import com.westee.cake.util.RequestUtil;
import org.springframework.stereotype.Service;

import java.io.IOException;
import java.util.HashMap;
import java.util.Map;
import java.util.Objects;

@Service
public class WeChatSubscribeService {

    public Object sendSubscribe(String userOpenId, String templateId, HashMap<String, Object> data) throws IOException {
        String url = "https://api.weixin.qq.com/cgi-bin/message/subscribe/send?access_token=" + GlobalVariable.INSTANCE.getAccessToken();
        HashMap<String, Object> params = new HashMap<>();
        params.put("touser", userOpenId); // openid
        params.put("template_id", templateId); // TEMPLATE_ID
        params.put("page", "page/order/list"); // TEMPLATE_ID
        if(Objects.nonNull(data)) {
            HashMap<String, Object> map = new HashMap<>();
            for (Map.Entry<String, Object> entry : data.entrySet()) {
                map.put(entry.getKey(), getParam(entry.getValue()));
            }
            params.put("data", map);
        }
        return RequestUtil.doNormalPost(url, params, null);
    }

    public HashMap<String, Object> getParam(Object p) {
        HashMap<String, Object> stringObjectHashMap = new HashMap<>();
        stringObjectHashMap.put("value", p);
        return stringObjectHashMap;
    }
}
