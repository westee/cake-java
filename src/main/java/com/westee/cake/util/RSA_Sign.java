package com.westee.cake.util;

import com.google.gson.JsonObject;
import com.westee.cake.config.ApiSecurityConfig;

import java.nio.charset.StandardCharsets;
import java.security.KeyFactory;
import java.security.Signature;
import java.security.interfaces.RSAPrivateKey;
import java.security.spec.MGF1ParameterSpec;
import java.security.spec.PKCS8EncodedKeySpec;
import java.security.spec.PSSParameterSpec;
import java.util.Base64;

public class RSA_Sign {
    public static String getSignature(JsonObject ctx, JsonObject req) {
        String signatureBase64 = null;
        // 开发者本地信息
        String local_appid = ctx.get("local_appid").getAsString();
        String url_path = ctx.get("url_path").getAsString();
        String local_sym_sn = ctx.get("local_sym_sn").getAsString();
        String local_private_key = ctx.get("local_private_key").getAsString();
        // 待请求API数据
        long reqTs = req.get("req_ts").getAsLong();
        String reqData = req.get("req_data").getAsString();

        String payload = url_path + "\n" + local_appid + "\n" + reqTs + "\n" + reqData;
        byte[] dataBuffer = payload.getBytes(StandardCharsets.UTF_8);
        try {
            local_private_key = local_private_key.replace("-----BEGIN PRIVATE KEY-----", "");
            local_private_key = local_private_key.replace("-----END PRIVATE KEY-----", "");
            local_private_key = local_private_key.replaceAll("\\s+", "");
            byte[] decoded = Base64.getDecoder().decode(local_private_key);
            PKCS8EncodedKeySpec keySpec = new PKCS8EncodedKeySpec(decoded);
            RSAPrivateKey priKey = (RSAPrivateKey) KeyFactory.getInstance("RSA").generatePrivate(keySpec);
            Signature signature = Signature.getInstance("RSASSA-PSS");
            // salt长度，需与SHA256结果长度(32)一致
            PSSParameterSpec pssParameterSpec = new PSSParameterSpec("SHA-256", "MGF1", MGF1ParameterSpec.SHA256, 32, 1);
            signature.setParameter(pssParameterSpec);
            signature.initSign(priKey);
            signature.update(dataBuffer);
            byte[] sigBuffer = signature.sign();
            signatureBase64 = Base64.getEncoder().encodeToString(sigBuffer);
        } catch (Exception e) {
            e.printStackTrace();
        }
        return signatureBase64;
        /*
        最终请求头字段
        {
            "Wechatmp-Appid": local_appid,
            "Wechatmp-TimeStamp": req_ts,
            "Wechatmp-Signature": sig,
        }
        */
    }

    private static JsonObject getCtx(String url) {
        JsonObject ctx = new JsonObject();
        String s;
        try {
            s = WxPayUtil.loadKeyByResource("wxApiSecurity/private_key_pkcs8.pem");
        } catch (Exception e) {
            throw new RuntimeException("读取api私钥失败");
        }

        ctx.addProperty("local_private_key", s);
        ctx.addProperty("local_sym_sn", ApiSecurityConfig.getAsym_sn());
        ctx.addProperty("local_appid", ApiSecurityConfig.getAppid());
        ctx.addProperty("url_path", url);
        return ctx;
    }

    public static String getSign(JsonObject data, String url) {
        JsonObject ctx = getCtx(url);
        return getSignature(ctx, data);
    }
}
