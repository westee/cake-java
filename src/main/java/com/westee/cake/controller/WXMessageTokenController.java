package com.westee.cake.controller;

import com.westee.cake.exceptions.AESException;
import jakarta.servlet.http.HttpServletRequest;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import java.security.MessageDigest;
import java.util.Arrays;

@RestController
@RequestMapping("/api/v1/")
public class WXMessageTokenController {
    @RequestMapping("/customer_message")
    public String verifyWXToken(HttpServletRequest request) throws AESException {
        String msgSignature = request.getParameter("signature");
        String msgTimestamp = request.getParameter("timestamp");
        String msgNonce = request.getParameter("nonce");
        String echostr = request.getParameter("echostr");
        if (verifyUrl(msgSignature, msgTimestamp, msgNonce)) {
            return echostr;
        }
        return null;
    }

    /**
     * 验证Token
     *
     * @param msgSignature 签名串，对应URL参数的signature
     * @param timeStamp    时间戳，对应URL参数的timestamp
     * @param nonce        随机串，对应URL参数的nonce
     * @return 是否为安全签名
     * @throws AESException 执行失败，请查看该异常的错误码和具体的错误信息
     */
    public static boolean verifyUrl(String msgSignature, String timeStamp, String nonce) throws AESException {
        // 这里的 WXPublicConstants.TOKEN 填写你自己设置的Token就可以了
        String signature = SHA1.getSHA1("xxx", timeStamp, nonce);
        if (!signature.equals(msgSignature)) {
            throw new AESException(AESException.ValidateSignatureError);
        }
        return true;
    }

    public static class SHA1 {

        /**
         * 用SHA1算法验证Token
         *
         * @param token     票据
         * @param timestamp 时间戳
         * @param nonce     随机字符串
         * @return 安全签名
         * @throws AESException 错误
         */
        public static String getSHA1(String token, String timestamp, String nonce) throws AESException {
            try {
                String[] array = new String[]{token, timestamp, nonce};
                StringBuilder sb = new StringBuilder();
                // 字符串排序
                Arrays.sort(array);
                for (int i = 0; i < 3; i++) {
                    sb.append(array[i]);
                }
                String str = sb.toString();
                // SHA1签名生成
                MessageDigest md = MessageDigest.getInstance("SHA-1");
                md.update(str.getBytes());
                byte[] digest = md.digest();

                StringBuilder hexstr = new StringBuilder();
                String shaHex;
                for (byte b : digest) {
                    shaHex = Integer.toHexString(b & 0xFF);
                    if (shaHex.length() < 2) {
                        hexstr.append(0);
                    }
                    hexstr.append(shaHex);
                }
                return hexstr.toString();
            } catch (Exception e) {
                e.printStackTrace();
                throw new AESException(AESException.ComputeSignatureError);
            }
        }
    }
}
