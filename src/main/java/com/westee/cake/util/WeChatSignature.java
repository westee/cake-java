package com.westee.cake.util;

import javax.crypto.Cipher;
import javax.crypto.spec.GCMParameterSpec;
import javax.crypto.spec.SecretKeySpec;
import java.nio.charset.StandardCharsets;
import java.security.KeyFactory;
import java.security.SecureRandom;
import java.security.Signature;
import java.security.interfaces.RSAPrivateKey;
import java.security.spec.MGF1ParameterSpec;
import java.security.spec.PKCS8EncodedKeySpec;
import java.security.spec.PSSParameterSpec;
import java.util.Arrays;
import java.util.Base64;

import com.google.gson.Gson;
import com.google.gson.JsonObject;
import com.westee.cake.config.WxPayConfig;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;

@Component
public class WeChatSignature {
    public static WxPayConfig wxPayConfig;

    @Autowired
    public WeChatSignature(WxPayConfig wxPayConfig) {
        WeChatSignature.wxPayConfig = wxPayConfig;
    }

    public static String getSignature(JsonObject ctx, JsonObject req){
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
        try{
            local_private_key = local_private_key.replace("-----BEGIN PRIVATE KEY-----", "");
            local_private_key = local_private_key.replace("-----END PRIVATE KEY-----", "");
            local_private_key = local_private_key.replaceAll("\\s+","");
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
        }catch (Exception e){
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

        req.addProperty("_n",nonce);
        req.addProperty("_appid",local_appid);
        req.addProperty("_timestamp",localTs);
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
            reqData.addProperty("iv",iv);
            reqData.addProperty("data",data);
            reqData.addProperty("authtag",authtag);


            JsonObject reqforsign = new JsonObject();
            reqforsign.addProperty("req_ts",localTs);
            reqforsign.addProperty("req_data",reqData.toString());

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
    private static JsonObject getCtx(){
        JsonObject ctx = new JsonObject();
        // 仅做演示，敏感信息请勿硬编码
        ctx.addProperty("local_sym_key","otUpngOjU+nVQaWJIC3D/yMLV17RKaP6t4Ot9tbnzLY=");
        ctx.addProperty("local_sym_sn","fa05fe1e5bcc79b81ad5ad4b58acf787");
        ctx.addProperty("local_appid", wxPayConfig.getAPPID());
        ctx.addProperty("url_path","https://api.weixin.qq.com/wxa/getuserriskrank");

        return ctx;
    }
    private static JsonObject getRawReq(){
        JsonObject req = new JsonObject();
        req.addProperty("appid","wxba6223c06417af7b");
        req.addProperty("openid","oEWzBfmdLqhFS2mTXCo2E4Y9gJAM");
        req.addProperty("scene",0);
        req.addProperty("client_ip","127.0.0.1");

        return req;
    }
    public static void main(String[] args) {
        JsonObject req = getRawReq();
        JsonObject ctx = getCtx();
        JsonObject reqforsign = getReqForSign(ctx,req);
        if(reqforsign != null){
//            System.out.println(reqforsign.get("req_ts").getAsLong());
//            System.out.println(reqforsign.get("req_data").getAsString());
        }
    }
}
