package com.westee.cake.config;


import org.springframework.boot.context.properties.ConfigurationProperties;
import org.springframework.context.annotation.Configuration;
import org.springframework.context.annotation.PropertySource;


@Configuration
@PropertySource("classpath:config.properties") //读取配置文件
@ConfigurationProperties(prefix="miniapp")
public class WxPayConfig {

    private String APPID;
    private String SECRET;
    private String SALT;
    private String MCHID;
    private String SERIAL_NO;
    private String apiV3key;
    private String payNotifyUrl;
    private String refundNotifyUrl;

    public String getSERIAL_NO() {
        return SERIAL_NO;
    }

    public void setSERIAL_NO(String SERIAL_NO) {
        this.SERIAL_NO = SERIAL_NO;
    }

    public String getApiV3key() {
        return apiV3key;
    }

    public void setApiV3key(String apiV3key) {
        this.apiV3key = apiV3key;
    }

    public String getMCHID() {
        return MCHID;
    }

    public void setMCHID(String MCHID) {
        this.MCHID = MCHID;
    }

    public String getAPPID() {
        return APPID;
    }

    public void setAPPID(String APPID) {
        this.APPID = APPID;
    }

    public String getSECRET() {
        return SECRET;
    }

    public void setSECRET(String SECRET) {
        this.SECRET = SECRET;
    }

    public String getSALT() {
        return SALT;
    }

    public void setSALT(String SALT) {
        this.SALT = SALT;
    }

    public String getPayNotifyUrl() {
        return payNotifyUrl;
    }

    public void setPayNotifyUrl(String payNotifyUrl) {
        this.payNotifyUrl = payNotifyUrl;
    }

    public String getRefundNotifyUrl() {
        return refundNotifyUrl;
    }

    public void setRefundNotifyUrl(String refundNotifyUrl) {
        this.refundNotifyUrl = refundNotifyUrl;
    }
}
