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

    public static Object doPost(String url, Object params, HashMap<String, String> headers) throws IOException {
        RequestBody requestBody;

        String json = "";
        if (params != null) {
            ObjectMapper objectMapper = new ObjectMapper();
            json = objectMapper.writeValueAsString(params);
        }
        requestBody = RequestBody.create(ByteString.encodeUtf8(json), MediaType.parse("application/json; charset=utf-8"));

        Request.Builder requestBuilder = new Request.Builder().url(url)
                .post(requestBody);

        if (headers != null) {
            for (Map.Entry<String, String> entry : headers.entrySet()) {
                requestBuilder.addHeader(entry.getKey(), entry.getValue());
            }
        }

        Request request = requestBuilder.build();
        try (Response response = client.newCall(request).execute()) {
            ResponseBody body = response.body();
            if(body == null) {
                return "";
            }
            return JSON.parse(body.string());
        }
    }
}
