package com.westee.cake.controller;

import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.westee.cake.config.ShanSongConfig;
import com.westee.cake.util.ShanSongUtil;
import org.apache.http.NameValuePair;
import org.apache.http.client.entity.UrlEncodedFormEntity;
import org.apache.http.client.methods.CloseableHttpResponse;
import org.apache.http.client.methods.HttpPost;
import org.apache.http.impl.client.CloseableHttpClient;
import org.apache.http.impl.client.HttpClients;
import org.apache.http.message.BasicNameValuePair;
import org.apache.http.util.EntityUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.Objects;

@RestController
@RequestMapping("/api/v1/")
public class ShanSongController {
    private final ShanSongConfig configProperties;

    @Autowired
    public ShanSongController(ShanSongConfig configProperties) {
        this.configProperties = configProperties;
    }

    @GetMapping("/shansong")
    public String test() throws JsonProcessingException {
        Map<String, Object> map = getBaseMap(null);
        //请求接口获取的结果
        return sendPost(ShanSongInterface.CITIES.getPath(), map);
    }

    @GetMapping("/shansong/stores")
    public String getStore() throws JsonProcessingException {
        HashMap<String, Object> dataMap = new HashMap<>();
        dataMap.put("pageNo", 1);
        dataMap.put("pageSize", 10);

        String stringMap = getStringMap(dataMap);
        Map<String, Object> map = getBaseMap(stringMap);
        map.put("data", stringMap);
        //请求接口获取的结果
        return sendPost(ShanSongInterface.GET_SHOP.getPath(), map);
    }

    @GetMapping("/shansong/calculate")
    public String testPrice() throws JsonProcessingException {

        Map<String, Object> dataMap = new HashMap<>();
        dataMap.put("cityName", "青岛市");
        dataMap.put("appointType", 0);
//        dataMap.put("appointmentDate", 0);
        // 从店铺信息中获取手机号 经纬度等
        Map<String, Object> sender = new HashMap<>();
        sender.put("fromAddress", "xx");
        sender.put("fromSenderName", "xx");
        sender.put("fromMobile", "xx");
        sender.put("fromLatitude", "xx");
        sender.put("fromLongitude", "xx");
        dataMap.put("sender", sender);

        ArrayList<Object> objects = new ArrayList<>();
        Map<String, Object> receiver = new HashMap<>();
        receiver.put("orderNo", "xx");
        receiver.put("toAddress", "xx");
        receiver.put("toReceiverName", "xx");
        receiver.put("toMobile", "x");
        receiver.put("toLatitude", "xx.x");
        receiver.put("toLongitude", "xx.xx");
        receiver.put("goodType", 1);
        receiver.put("weight", 1);
        objects.add(receiver);
        dataMap.put("receiverList", objects);

        String stringMap = getStringMap(dataMap);
        Map<String, Object> map = getBaseMap(stringMap);
        map.put("data", stringMap);

        //请求接口获取的结果
        return sendPost(ShanSongInterface.CALCULATE_PRICE.getPath(), map);
    }

    public String getStringMap(Map<String, Object> data) throws JsonProcessingException {
        ObjectMapper objectMapper = new ObjectMapper();
        return objectMapper.writeValueAsString(data);
    }

    public Map<String, Object> getBaseMap(String stringMap) {
        String appSecret = configProperties.getAppSecret();
        String clientId = configProperties.getClientId();
        String shopId = configProperties.getShopId();
        Long timestamp = System.currentTimeMillis();

        StringBuffer sb;
        if (Objects.nonNull(stringMap) && !stringMap.isEmpty()) {
            sb = new StringBuffer(appSecret)
                    .append("clientId").append(clientId)
                    .append("data").append(stringMap)
                    .append("shopId").append(shopId)
                    .append("timestamp").append(timestamp);
        } else {
            sb = new StringBuffer(appSecret)
                    .append("clientId").append(clientId)
                    .append("shopId").append(shopId)
                    .append("timestamp").append(timestamp);
        }
        System.out.println("result");
        System.out.println(sb);

        //计算签名
        String sign = ShanSongUtil.bytesToMD5(sb.toString().getBytes());
        System.out.println(sign);

        Map<String, Object> map = new HashMap<String, Object>();
        map.put("clientId", clientId);
        map.put("shopId", shopId);
        map.put("timestamp", timestamp);
        map.put("sign", sign);
        return map;
    }

    public static String sendPost(String url, Map<String, Object> params) {
        String response = null;
        try {
            List<NameValuePair> pairs = null;
            if (params != null && !params.isEmpty()) {
                pairs = new ArrayList<NameValuePair>(params.size());
                for (String key : params.keySet()) {
                    pairs.add(new BasicNameValuePair(key, params.get(key).toString()));
                }
            }
            CloseableHttpClient httpClient = null;
            CloseableHttpResponse httpResponse = null;
            try {
                httpClient = HttpClients.createDefault();
                HttpPost httppost = new HttpPost(url);
                if (pairs != null && pairs.size() > 0) {
                    httppost.setEntity(new UrlEncodedFormEntity(pairs, "UTF-8"));
                }
                httpResponse = httpClient.execute(httppost);
                response = EntityUtils
                        .toString(httpResponse.getEntity());
                System.out.println(response);
            } finally {
                if (httpClient != null) {
                    httpClient.close();
                }
                if (httpResponse != null) {
                    httpResponse.close();
                }
            }
        } catch (Exception e) {
            e.printStackTrace();
        }
        return response;
    }

    public enum ShanSongInterface {
        CITIES("/openapi/merchants/v5/openCitiesLists"),
        CALCULATE_PRICE("/openapi/merchants/v5/orderCalculate"),
        GET_SHOP("/openapi/merchants/v5/queryAllStores");

        private final String path;

        ShanSongInterface(String path) {
            this.path = path;
        }

        public String getPath() {
            String BASE_URL = "https://open.ishansong.com";
//            String BASE_URL = "http://open.s.bingex.com";
            return BASE_URL + path;
        }
    }

}
