package com.westee.cake.config;

import com.westee.cake.service.ShiroRealm;
import com.westee.cake.service.VerificationCodeCheckService;
import org.apache.shiro.SecurityUtils;
import org.apache.shiro.cache.MemoryConstrainedCacheManager;
import org.apache.shiro.codec.Base64;
import org.apache.shiro.spring.web.ShiroFilterFactoryBean;
import org.apache.shiro.web.mgt.CookieRememberMeManager;
import org.apache.shiro.web.mgt.DefaultWebSecurityManager;
import org.apache.shiro.web.servlet.SimpleCookie;
import org.apache.shiro.web.session.mgt.DefaultWebSessionManager;
import org.springframework.context.annotation.Bean;
import org.apache.shiro.mgt.SecurityManager;
import org.springframework.context.annotation.Configuration;

import javax.servlet.Filter;
import java.util.HashMap;
import java.util.LinkedHashMap;
import java.util.Map;

@Configuration
public class ShiroConfig {
    @Bean
    public ShiroFilterFactoryBean shiroFilterFactoryBean(SecurityManager securityManager, ShiroLoginFilter shiroLoginFilter) {
        ShiroFilterFactoryBean shiroFilterFactoryBean = new ShiroFilterFactoryBean();
        shiroFilterFactoryBean.setSecurityManager(securityManager);

        Map<String, String> pattern = new HashMap<>();
        pattern.put("/api/v1/code", "anon");
        pattern.put("/api/v1/login", "anon");
        pattern.put("/api/v1/status", "anon");
        pattern.put("/api/v1/logout", "anon");
        pattern.put("/api/v1/testRpc", "anon");
        pattern.put("/**", "authc");

        Map<String, Filter> filtersMap = new LinkedHashMap<>();
        filtersMap.put("shiroLoginFilter", shiroLoginFilter);
        shiroFilterFactoryBean.setFilters(filtersMap);

        shiroFilterFactoryBean.setFilterChainDefinitionMap(pattern);
        return shiroFilterFactoryBean;
    }

    @Bean
    public DefaultWebSecurityManager mySecurityManager(ShiroRealm shiroRealm) {
        DefaultWebSecurityManager securityManager = new DefaultWebSecurityManager();

        securityManager.setRealm(shiroRealm);
//        securityManager.setCacheManager(cacheManager);
        securityManager.setCacheManager(new MemoryConstrainedCacheManager());
        securityManager.setSessionManager(new DefaultWebSessionManager());
        securityManager.setRememberMeManager(rememberMeManager());
        SecurityUtils.setSecurityManager(securityManager);
        return securityManager;
    }

    public CookieRememberMeManager rememberMeManager() {
        SimpleCookie cookie = new SimpleCookie("rememberMe");
        cookie.setMaxAge(86400);
        CookieRememberMeManager cookieRememberMeManager = new CookieRememberMeManager();
        cookieRememberMeManager.setCookie(cookie);
        cookieRememberMeManager.setCipherKey(Base64.decode("3AvVhmFLUs0KTA3KaT.GFg=="));  // RememberMe cookie encryption key default AES algorithm of key length (128, 256, 512)
        return cookieRememberMeManager;
    }

    @Bean
    public ShiroRealm myShiroRealm(VerificationCodeCheckService verificationCodeCheckService) {
        return new ShiroRealm(verificationCodeCheckService);
    }
}
