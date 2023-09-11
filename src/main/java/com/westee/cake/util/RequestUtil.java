package com.westee.cake.util;

import com.alibaba.fastjson2.JSON;
import okhttp3.FormBody;
import okhttp3.MediaType;
import okhttp3.OkHttpClient;
import okhttp3.Request;
import okhttp3.RequestBody;
import okhttp3.Response;
import okhttp3.ResponseBody;

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

    public String post(String url, Map<String, String> params, boolean isJsonPost, String token) throws IOException {
        RequestBody requestBody;
        if (isJsonPost) {
            String json = "";
            if (params != null) {
                json = JSON.toJSONString(params);
            }
            requestBody = RequestBody.create(json, MediaType.parse("application/json; charset=utf-8"));
        } else {
            FormBody.Builder formBody = new FormBody.Builder();
            if (params != null) {
                params.forEach(formBody::add);
            }
            requestBody = formBody.build();
        }
        Request request = new Request.Builder().url(url + "?access_token=" + token).post(requestBody).build();
        try( Response response = client.newCall(request).execute()) {
            ResponseBody body = response.body();
            if(body == null) {
                return "";
            }
            return body.string();
        }
    }
}
