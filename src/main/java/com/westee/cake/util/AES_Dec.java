package com.westee.cake.util;

import com.google.gson.Gson;
import com.google.gson.JsonElement;
import com.google.gson.JsonObject;
import com.google.gson.JsonParser;
import com.westee.cake.config.ApiSecurityConfig;

import javax.crypto.Cipher;
import javax.crypto.spec.GCMParameterSpec;
import javax.crypto.spec.SecretKeySpec;
import java.nio.charset.StandardCharsets;
import java.util.Base64;
import java.util.HashMap;

public class AES_Dec {
    public static HashMap<String, Object> getRealResp(JsonObject ctx, JsonObject resp) {
        byte[] decryptedBytes = null;
        // 开发者本地信息
        String local_appid = ctx.get("local_appid").getAsString();
        String url_path = ctx.get("url_path").getAsString();
        String local_sym_sn = ctx.get("local_sym_sn").getAsString();
        String local_sym_key = ctx.get("local_sym_key").getAsString();
        // API响应数据，解密只需要响应头时间戳与响应数据
        long respTs = resp.get("resp_ts").getAsLong();
        String respData = resp.get("resp_data").getAsString();

        JsonParser parser = new JsonParser();
        JsonElement resp_data = parser.parse(respData);
        String iv = resp_data.getAsJsonObject().get("iv").getAsString();
        String data = resp_data.getAsJsonObject().get("data").getAsString();
        String authtag = resp_data.getAsJsonObject().get("authtag").getAsString();
        // 构建AAD
        String aad = url_path + "|" + local_appid + "|" + respTs + "|" + local_sym_sn;
        // 拼接cipher和authtag
        byte[] dataBytes = Base64.getDecoder().decode(data);
        byte[] authtagBytes = Base64.getDecoder().decode(authtag);
        byte[] new_dataBytes = new byte[dataBytes.length + authtagBytes.length];
        System.arraycopy(dataBytes, 0, new_dataBytes, 0, dataBytes.length);
        System.arraycopy(authtagBytes, 0, new_dataBytes, dataBytes.length, authtagBytes.length);
        byte[] aadBytes = aad.getBytes(StandardCharsets.UTF_8);
        byte[] ivBytes = Base64.getDecoder().decode(iv);
        HashMap<String, Object> realResp = null;
        try {
            byte[] keyBytes = Base64.getDecoder().decode(local_sym_key);
            SecretKeySpec secretKey = new SecretKeySpec(keyBytes, "AES");
            Cipher cipher = Cipher.getInstance("AES/GCM/NoPadding");
            GCMParameterSpec gcmParameterSpec = new GCMParameterSpec(128, ivBytes);
            cipher.init(Cipher.DECRYPT_MODE, secretKey, gcmParameterSpec);
            cipher.updateAAD(aadBytes);
            try {
                decryptedBytes = cipher.doFinal(new_dataBytes);
            } catch (Exception e) {
                System.out.println("auth tag验证失败");
                return null;
            }

            // 解密结果
            String decryptedData = new String(decryptedBytes, StandardCharsets.UTF_8);
            JsonElement element = parser.parse(decryptedData);
            Gson gson = new Gson();
            realResp = gson.fromJson(element, HashMap.class);
            long localTs = System.currentTimeMillis() / 1000;
            // 安全检查，根据业务实际需求判断
            if (element.getAsJsonObject().get("_appid").getAsString() == local_appid // appid不匹配
                    || element.getAsJsonObject().get("_timestamp").getAsLong() != respTs // timestamp与Wechatmp-TimeStamp不匹配
                    || localTs - element.getAsJsonObject().get("_timestamp").getAsLong() > 300 // 响应数据的时候与当前时间超过5分钟
            ) {
                System.out.println("安全字段校验失败");
                return null;
            }
        } catch (Exception e) {
            e.printStackTrace();
        }
        return realResp;

    }

    private static JsonObject getCtx(String url) {
        JsonObject ctx = new JsonObject();
        ctx.addProperty("local_sym_key", ApiSecurityConfig.getSym_key());
        ctx.addProperty("local_sym_sn", ApiSecurityConfig.getSym_sn());
        ctx.addProperty("local_appid", ApiSecurityConfig.getAppid());
        ctx.addProperty("url_path", url);
        return ctx;
    }

    private static JsonObject getResp(String respData, Long resp_ts) {
        JsonObject resp = new JsonObject();
        // String respData = "{\"iv\":\"QrZZqNdqxToexKwy\",\"data\":\"CfoqyhQ1oYg2zJ7tvqO4t5\\/KA2zUD33y2D3FIbFShkclYaONiLqROmG8CfHm0EXMADNcvp4EQuxKxrGZbVE97qrymbnpreTT\\/XQtly62A84KrGCLIqi8SF\\/jM75QFH6qEuwy32gSsl7sv2GxDSCdb4JkpFPmskjsZgP3z9vBFUPklTiJtLRDfy\\/MPg9sYT3eVFuD636NO01\\/NkYPAa9WUIIlbDlqk\\/O0xMgBdi84JwqtxW+VuRqSkge30EluRBoiH4j3ngCuK5JGqfPw1MFXL0e55V72iBsSa5fqHDnskl62pKUcR+\\/h4Znw7H5f3U5WnUtsKAzH3hg22+VIgUp5veLOw1PI94c9Ks5A26+4OWIh9wmgUuInZo20g1j50G8ENfNPVcDApSG0P\\/yKJZFAsQrvx2c+pU6sVllo+XTp4eoz2wCNrEedEZ\\/ism02GWmwTtgWALFIedib44fOyNuIfNWwXMg\\/ywzBN37Ercip7vI+Iyd+wPTCjB046wmZYSzVelLY5qc97X7jo6m8+np3VS+NHr09tNGmjG5L\\/FMHO99WuURJqSNdXB3swiOlVbtRCYwA4epzhhnv2b6TzYN9UOLw\",\"authtag\":\"n5OB+4ZdaQU\\/ZE6wE3GO2w==\"}";
        resp.addProperty("resp_ts", resp_ts);
        resp.addProperty("resp_data", respData);

        return resp;
    }

    public static HashMap<String, Object> getRealRespResult(String data, Long ts, String url) {
        JsonObject resp = getResp(data, ts);
        JsonObject ctx = getCtx(url);
        return getRealResp(ctx, resp);
    }
}

