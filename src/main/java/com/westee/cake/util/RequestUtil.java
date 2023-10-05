package com.westee.cake.util;

import com.alibaba.fastjson2.JSON;
import com.fasterxml.jackson.databind.ObjectMapper;
import okhttp3.MediaType;
import okhttp3.OkHttpClient;
import okhttp3.Request;
import okhttp3.RequestBody;
import okhttp3.Response;
import okhttp3.ResponseBody;
import okio.ByteString;

import java.io.IOException;
import java.util.HashMap;
import java.util.Map;

public class RequestUtil {
    private static final OkHttpClient client = new OkHttpClient();

    public static String doGetRequest(String url, HashMap<String, String> params) throws IOException {
        Request request = new Request.Builder()
                .url(url + "?" + mapToQueryString(params))
                .build();
        try (Response response = client.newCall(request).execute()) {
            ResponseBody responseBody = response.body();
            if (responseBody == null) {
                return "";
            }
            return responseBody.string();
        }
    }

    public static String mapToQueryString(Map<String, String> map) {
        StringBuilder sb = new StringBuilder();
        for (Map.Entry<String, String> entry : map.entrySet()) {
            if (sb.length() > 0) {
                sb.append("&");
            }
            sb.append(entry.getKey()).append("=").append(entry.getValue());
        }
        return sb.toString();
    }

    public static Object doSecurityPost(String url, Object params, HashMap<String, Object> headers) throws IOException {
        try (Response response = post(url, params, headers)) {

            ResponseBody body = response.body();
            String respSign = response.header("Wechatmp-Signature");
            String respAppId = response.header("Wechatmp-Appid");
            String respTs = response.header("Wechatmp-TimeStamp");
            String respSerial = response.header("Wechatmp-Serial");
            if (body == null) {
                return "";
            }
            if (respTs == null) {
                return body.string();
            }
            String responseBodyString = body.string();
            // {errcode=934016.0, errmsg=Order not exist rid: 65192df5-77a7d1b3-117973ad, _n=270eaa5fe4b9e68213ecbd37f417e10e, _appid=wx1e933945b62aebf8, _timestamp=1.696148981E9}
            return AES_Dec.getRealRespResult(responseBodyString, Long.decode(respTs), url.split("\\?")[0]);
        }
    }

    public static Response post(String url, Object params, HashMap<String, Object> headers) throws IOException {
        RequestBody requestBody;
        String json = "";
        if (params instanceof String) {
            json = params.toString();
        } else if (params != null) {
            ObjectMapper objectMapper = new ObjectMapper();
            json = objectMapper.writeValueAsString(params);
        }
        requestBody = RequestBody.create(ByteString.encodeUtf8(json), MediaType.parse("application/json; charset=utf-8"));

        Request.Builder requestBuilder = new Request.Builder().url(url)
                .post(requestBody);
        if (headers != null) {
            for (Map.Entry<String, Object> entry : headers.entrySet()) {
                requestBuilder.addHeader(entry.getKey(), entry.getValue().toString());
            }
        }

        Request request = requestBuilder.build();
        return client.newCall(request).execute();
    }

    public static Object doNormalPost(String url, Object params, HashMap<String, Object> headers) throws IOException {
        try (Response response = post(url, params, headers)) {
            ResponseBody body = response.body();
            if (body == null) {
                return "";
            }
            String string = body.string();
            return JSON.parse(string);
        }
    }
}
