package com.westee.cake.config;


import org.springframework.boot.context.properties.ConfigurationProperties;
import org.springframework.context.annotation.Configuration;
import org.springframework.context.annotation.PropertySource;

@Configuration
@PropertySource("classpath:config.properties") //读取配置文件
@ConfigurationProperties(prefix="qiniu")
public class QiniuConfig {
    private String ACCESS_KEY;
    private String SECRET_KEY;
    private String BUCKET_NAME;
    private String IMAGE_PREFIX;

    public String getACCESS_KEY() {
        return ACCESS_KEY;
    }

    public void setACCESS_KEY(String ACCESS_KEY) {
        this.ACCESS_KEY = ACCESS_KEY;
    }

    public String getSECRET_KEY() {
        return SECRET_KEY;
    }

    public void setSECRET_KEY(String SECRET_KEY) {
        this.SECRET_KEY = SECRET_KEY;
    }

    public String getBUCKET_NAME() {
        return BUCKET_NAME;
    }

    public void setBUCKET_NAME(String BUCKET_NAME) {
        this.BUCKET_NAME = BUCKET_NAME;
    }

    public String getIMAGE_PREFIX() {
        return IMAGE_PREFIX;
    }

    public void setIMAGE_PREFIX(String IMAGE_PREFIX) {
        this.IMAGE_PREFIX = IMAGE_PREFIX;
    }
}
