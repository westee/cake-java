package com.westee.cake.util;

import com.alibaba.fastjson2.JSON;
import com.wechat.pay.java.core.RSAAutoCertificateConfig;
import com.wechat.pay.java.core.notification.NotificationConfig;
import com.wechat.pay.java.core.notification.NotificationParser;
import com.wechat.pay.java.core.notification.RequestParam;
import com.westee.cake.config.WxPayConfig;
import jakarta.servlet.http.HttpServletRequest;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.core.io.ClassPathResource;
import org.springframework.stereotype.Service;
import org.springframework.util.FileCopyUtils;

import java.nio.charset.StandardCharsets;
import java.util.Map;

@Service
public class WxPayUtil {
    private final WxPayConfig wxPayConfig;

    @Autowired
    public WxPayUtil(WxPayConfig wxPayConfig) {
        this.wxPayConfig = wxPayConfig;
    }

    public  <T> T getNotificationParser(HttpServletRequest request, Map<String, Object> body, Class<T> clazz) throws Exception {
        String privateKey = loadKeyByResource("wxpay/apiclient_key.pem");
        RequestParam requestParam = new RequestParam.Builder()
                .serialNumber(request.getHeader("Wechatpay-Serial"))
                .nonce(request.getHeader("Wechatpay-Nonce"))
                .signature(request.getHeader("Wechatpay-Signature"))
                .timestamp(request.getHeader("Wechatpay-Timestamp"))
                .signType(request.getHeader("Wechatpay-Signature-Type"))
                .body(JSON.toJSONString(body))
                .build();
        NotificationConfig config = new RSAAutoCertificateConfig.Builder()
                .merchantId(wxPayConfig.getMCHID())
                .privateKey(privateKey)
                .merchantSerialNumber(wxPayConfig.getSERIAL_NO())
                .apiV3Key(wxPayConfig.getApiV3key())
                .build();

        NotificationParser parser = new NotificationParser(config);
        return parser.parse(requestParam, clazz);
    }

    /**
     * 通过文件路径获取文件内容
     * ClassPathResource可以在jar包中运行,但不能使用其中getFile().getPath()
     * @param path          文件路径
     * @return              文件内容
     * @throws Exception    报错信息
     */
    public static String loadKeyByResource(String path) throws Exception {
        ClassPathResource resource = new ClassPathResource(path);

        byte[] byteArray = FileCopyUtils.copyToByteArray(resource.getInputStream());
        return new String(byteArray, StandardCharsets.UTF_8);
    }

}
