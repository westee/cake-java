package com.westee.cake.config;

import org.springframework.boot.context.properties.ConfigurationProperties;
import org.springframework.context.annotation.Configuration;
import org.springframework.context.annotation.PropertySource;

@Configuration
@PropertySource("classpath:config.properties") //读取配置文件
@ConfigurationProperties(prefix="api")
public class ApiSecurityConfig {
    static String sym_key;
    static String sym_sn;
    static String asym_sn;
    static String appid;

    public static String getSym_key() {
        return ApiSecurityConfig.sym_key;
    }

    public void setSym_key(String sym_key) {
        ApiSecurityConfig.sym_key = sym_key;
    }

    public static String getSym_sn() {
        return ApiSecurityConfig.sym_sn;
    }

    public void setSym_sn(String sym_sn) {
        ApiSecurityConfig.sym_sn = sym_sn;
    }

    public static String getAppid() {
        return ApiSecurityConfig.appid;
    }

    public void setAppid(String appid) {
        ApiSecurityConfig.appid = appid;
    }

    public static String getAsym_sn() {
        return asym_sn;
    }

    public void setAsym_sn(String asym_sn) {
        ApiSecurityConfig.asym_sn = asym_sn;
    }
}
