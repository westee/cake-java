package com.westee.cake.util;

import com.google.gson.JsonObject;
import java.nio.charset.StandardCharsets;
import java.security.KeyFactory;
import java.security.Signature;
import java.security.interfaces.RSAPrivateKey;
import java.security.spec.MGF1ParameterSpec;
import java.security.spec.PKCS8EncodedKeySpec;
import java.security.spec.PSSParameterSpec;
import java.util.Base64;

public class RSA_Sign {
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
    private static JsonObject getReq(){
        JsonObject req = new JsonObject();
        req.addProperty("req_ts",1635927954);
        req.addProperty("req_data","{\"iv\":\"fmW/zNxXlytUZBgj\",\"data\":\"0IDVdrPtSPF/Oe2CTXCV2vVNPbVJdJlP2WaTMQnoYLh5iCrrSNfQFh25EnStDMf0hLlVNBCZQtf9NaV0m4aRA4AAYIO7oR/Ge+4yY4EmZp5EVPB42xjScgMx5X3D4VdLCfynXIUKUtZHZvk1zmLVE3RauzJgiM1BB1CPmwcENo3MTJ0z8Vfkf5tMv54kOXobDLlV5rfqKdAX7gM/rP82DgZdt9vvZX44ipdbHIjJvw83ZXAFtvftdVw2Qd8=\",\"authtag\":\"5qeM/2vZv+6KtScN94IpMg==\"}");
        return req;
    }
    private static JsonObject getCtx(){
        JsonObject ctx = new JsonObject();
        // 仅做演示，敏感信息请勿硬编码
        String localPrivateKey = "-----BEGIN PRIVATE KEY-----\n" +
                "MIIEvQIBADANBgkqhkiG9w0BAQEFAASCBKcwggSjAgEAAoIBAQDcWhA6Y6Xn8IXm\n" +
                "EXu1rgTMLLYtpTcS7aT0MHBDvcnzYjndLH0sCAzUMfcixiiFFHH8ERJ9t0MjamjC\n" +
                "+vpBgpUgL7hhq/CDuDOFlHFZaZPUeu4QAMJkCCRQK770iX/0U+6K09KgikF3Ts2p\n" +
                "2SRF7zY0E5+spN2GB7/nN3jnt+XsnzVDlVMjE7y++rsVLDuTom2/546NHpJk5Ztt\n" +
                "Q7t/2vhS5SRea90a/bWqKq1o7UQzDi1QIQvNjvczs2kIfdGiuWiaZhfc/qrRWuQM\n" +
                "esITT9AyGYewX7joT4hFwnfC0SPen+Y7VWgKkSFrALVTvbxlRPBY0SKLyE8SFw0B\n" +
                "lMppAxpNAgMBAAECggEBAJe/mn9rkpzJgpzvm1+B6SHnBMsohWYOrNPR6+5q7IIl\n" +
                "Oi477jP9k0Aq7MRQ7STA8MKjyzUymRTxuhXVG7UzxZk14+e3rlo355ttoCXwVHM2\n" +
                "+W2h6D1VchSYG9hyPOE5UAThXaNbszwD1BTNSnKzC4QPo54Up84e1iTYifYiKkCq\n" +
                "F9vJzqCfu5lJZNCyg7qEYFFDsSPH6+DCtvFkpVntRds8cPSU4Q+toDc8f9BR5Qqk\n" +
                "3ArY1dCgHFZxHbzf8fEVICONJKwZIEqwKfAM3nk/YVDQc2LlivBQ1P3X3Q0hdJ7K\n" +
                "BpHLVv4P7IF+b7ITv+pfActkIeyM77a0xJLcoaOWVMECgYEA/0U95g0pm67mO31G\n" +
                "lnBcJz0TsjPtuYzvPCzp26fp1AfTFVVYVWaIYi7Tt6rIHwc1qBex4XOhctXkRDH7\n" +
                "cNoHRsAf5kSkcYkmkBZIu6yKCywVQW5O9MXD6sLmHE5+J0xrr73dJTmlZ4lvTMkX\n" +
                "w9ENondSbCXCli5sXXuxPg8Pqn0CgYEA3PtGWFFIW+CAmOEQyl6Tc4hhauuVE07Y\n" +
                "xVw/uL98Y1MeGvDs4dw0ZGh/nhBnZjRtovvW8c2iRsraadP5xBdj+BcyZwT95XjH\n" +
                "JEZiIgv2cF2uQt6tzjcRg+/568O+DKOLr7LienlwqoetLwX5jMBOTzYm30eB0jYy\n" +
                "99slORJuaBECgYAc08v2s9vUCf9UDoSvLocpozsbL8QT5TeLGyNZu++rEysiSJ4O\n" +
                "HuMd+13LawzofB7yGaLr6+v6lO8PSHm1t9mEguPXVD8h1zQ0p1+VWhnIwzSvS+68\n" +
                "s6y5UUM9RRv4AihTE2Tq9ESamampCTiW03/vrfvv3J/J5/oy4GoWsQq63QKBgH0V\n" +
                "+y646/Wd8g0XsTJsMJkAROyJ57ujMQ8eda91LMroXK0xXVVIGZwQjtSBQpZW7QyJ\n" +
                "85SyKr5ZyyKGa+Y0u1DOiKhJ6hI/uLHu6VOOY/QdAyw08js9bru5VkW/ak+rL8HH\n" +
                "x20+Wqkc3co1XpnurSUnaP+QcYr1RQqJ0NsqdLaRAoGAdnRxjPCH1ieZ9fH2aKYX\n" +
                "ntpBDxoVz3Jp4Pn/cbb5r8Vlwe1jNR0dDs89LfluUPXv82eG6ceG6pqRuBqsf8sg\n" +
                "UwchMJW4ZBPjFLfiQVWMVWGJPdwd9bIFsnoZtC7PMXHP9QoMMp/f843jWTxQ/bQZ\n" +
                "2+a//5zqpCMo9rIwTzV6W6w=\n" +
                "-----END PRIVATE KEY-----";
        ctx.addProperty("local_private_key",localPrivateKey);
        ctx.addProperty("local_sym_sn","97845f6ed842ea860df6fdf65941ff56");
        ctx.addProperty("local_appid","wxba6223c06417af7b");
        ctx.addProperty("url_path","https://api.weixin.qq.com/wxa/getuserriskrank");
        return ctx;
    }
    public static void main(String[] args) {
        JsonObject req = getReq();
        JsonObject ctx = getCtx();
        String res = getSignature(ctx,req);
        System.out.println("getSignature");
        System.out.println(res);

    }
}
