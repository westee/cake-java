package com.westee.cake.util;

import com.google.gson.Gson;
import com.google.gson.JsonObject;
import com.google.gson.JsonParser;
import com.westee.cake.config.ApiSecurityConfig;

import javax.crypto.Cipher;
import javax.crypto.spec.GCMParameterSpec;
import javax.crypto.spec.SecretKeySpec;
import java.nio.charset.StandardCharsets;
import java.security.SecureRandom;
import java.util.Arrays;
import java.util.Base64;

public class AES_Enc {
    private static JsonObject getReqForSign(JsonObject ctx, JsonObject req) {
        Gson gson = new Gson();
        // 开发者本地信息
        String local_appid = ctx.get("local_appid").getAsString();
        String url_path = ctx.get("url_path").getAsString();
        String local_sym_sn = ctx.get("local_sym_sn").getAsString();
        String local_sym_key = ctx.get("local_sym_key").getAsString();
        //加密签名使用的统一时间戳
        long localTs = System.currentTimeMillis() / 1000;
        String nonce = generateNonce();

        req.addProperty("_n", nonce);
        req.addProperty("_appid", local_appid);
        req.addProperty("_timestamp", localTs);
        String plaintext = gson.toJson(req);

        String aad = url_path + "|" + local_appid + "|" + localTs + "|" + local_sym_sn;
        byte[] realKey = Base64.getDecoder().decode(local_sym_key);
        byte[] realIv = generateRandomBytes(12);
        byte[] realAad = aad.getBytes(StandardCharsets.UTF_8);
        byte[] realPlaintext = plaintext.getBytes(StandardCharsets.UTF_8);

        try {
            Cipher cipher = Cipher.getInstance("AES/GCM/NoPadding");
            SecretKeySpec keySpec = new SecretKeySpec(realKey, "AES");
            GCMParameterSpec parameterSpec = new GCMParameterSpec(128, realIv);
            cipher.init(Cipher.ENCRYPT_MODE, keySpec, parameterSpec);
            cipher.updateAAD(realAad);

            byte[] ciphertext = cipher.doFinal(realPlaintext);
            byte[] encryptedData = Arrays.copyOfRange(ciphertext, 0, ciphertext.length - 16);
            byte[] authTag = Arrays.copyOfRange(ciphertext, ciphertext.length - 16, ciphertext.length);

            String iv = base64Encode(realIv);
            String data = base64Encode(encryptedData);
            String authtag = base64Encode(authTag);

            JsonObject reqData = new JsonObject();
            reqData.addProperty("iv", iv);
            reqData.addProperty("data", data);
            reqData.addProperty("authtag", authtag);

            JsonObject reqforsign = new JsonObject();
            reqforsign.addProperty("req_ts", localTs);
            reqforsign.addProperty("req_data", reqData.toString());

            return reqforsign;
        } catch (Exception e) {
            e.printStackTrace();
        }

        return null;
    }

    private static String generateNonce() {
        byte[] nonce = generateRandomBytes(16);
        return base64Encode(nonce).replace("=", "");
    }

    private static byte[] generateRandomBytes(int length) {
        byte[] bytes = new byte[length];
        new SecureRandom().nextBytes(bytes);
        return bytes;
    }

    private static String base64Encode(byte[] data) {
        return Base64.getEncoder().encodeToString(data);
    }

    private static JsonObject getCtx(String url) {
        JsonObject ctx = new JsonObject();
        // 仅做演示，敏感信息请勿硬编码
        ctx.addProperty("local_sym_key", ApiSecurityConfig.getSym_key());
        ctx.addProperty("local_sym_sn", ApiSecurityConfig.getSym_sn());
        ctx.addProperty("local_appid", ApiSecurityConfig.getAppid());
        ctx.addProperty("url_path", url);

        return ctx;
    }

    private static JsonObject getRawReq(Object hashMap) {
        Gson gson = new Gson();
        String json = gson.toJson(hashMap);
        JsonParser parser = new JsonParser();
        return parser.parse(json).getAsJsonObject();
    }

    public static JsonObject getData(Object param, String url) {
        JsonObject req = getRawReq(param);
        JsonObject ctx = getCtx(url);
        return getReqForSign(ctx, req);
    }


}
