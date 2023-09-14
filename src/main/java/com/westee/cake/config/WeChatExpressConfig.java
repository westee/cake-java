package com.westee.cake.config;

import org.springframework.boot.context.properties.ConfigurationProperties;
import org.springframework.context.annotation.Configuration;
import org.springframework.context.annotation.PropertySource;

@Configuration
@PropertySource("classpath:config.properties") //读取配置文件
@ConfigurationProperties(prefix="express")
public class WeChatExpressConfig {
    static String wxStoreId;

    public static String getWxStoreId() {
        return WeChatExpressConfig.wxStoreId;
    }

    public void setWxStoreId(String wxStoreId) {
        WeChatExpressConfig.wxStoreId = wxStoreId;
    }
}
