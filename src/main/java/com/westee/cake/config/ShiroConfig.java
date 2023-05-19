package com.westee.cake.config;

import com.westee.cake.realm.AuthorizationRealm;
import com.westee.cake.realm.JWTAuthFilter;
import com.westee.cake.realm.JWTCredentialsMatcher;
import com.westee.cake.realm.JWTRealm;
import com.westee.cake.realm.LoginType;
import com.westee.cake.realm.MyModularRealmAuthenticator;
import com.westee.cake.realm.UserPasswordRealm;
import com.westee.cake.realm.WechatLoginRealm;
import com.westee.cake.service.UserService;
import org.apache.shiro.SecurityUtils;
import org.apache.shiro.authc.credential.CredentialsMatcher;
import org.apache.shiro.authc.pam.AtLeastOneSuccessfulStrategy;
import org.apache.shiro.cache.MemoryConstrainedCacheManager;
import org.apache.shiro.codec.Base64;
import org.apache.shiro.crypto.hash.Sha256Hash;
import org.apache.shiro.mgt.SecurityManager;
import org.apache.shiro.realm.AuthorizingRealm;
import org.apache.shiro.realm.Realm;
import org.apache.shiro.spring.web.ShiroFilterFactoryBean;
import org.apache.shiro.web.mgt.CookieRememberMeManager;
import org.apache.shiro.web.mgt.DefaultWebSecurityManager;
import org.apache.shiro.web.servlet.SimpleCookie;
import org.apache.shiro.web.session.mgt.DefaultWebSessionManager;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.web.servlet.config.annotation.WebMvcConfigurer;

import javax.servlet.Filter;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.LinkedHashMap;
import java.util.List;
import java.util.Map;

@Configuration
public class ShiroConfig implements WebMvcConfigurer {
    @Bean
    public ShiroFilterFactoryBean shiroFilterFactoryBean(SecurityManager securityManager, ShiroLoginFilter shiroLoginFilter) {
        ShiroFilterFactoryBean shiroFilterFactoryBean = new ShiroFilterFactoryBean();
        shiroFilterFactoryBean.setSecurityManager(securityManager);

        Map<String, String> pattern = new HashMap<>();
        pattern.put("/api/v1/code", "anon");
        pattern.put("/api/v1/login", "anon");
        pattern.put("/api/v1/login**", "anon");
        pattern.put("/api/v1/register**", "anon");
        pattern.put("/api/v1/status", "anon");
        pattern.put("/api/v1/logout", "anon");

        pattern.put("/api/v1/token", "jwt");
        pattern.put("/api/v1/test", "jwt");

        Map<String, Filter> filtersMap = new LinkedHashMap<>();
        filtersMap.put("shiroLoginFilter", shiroLoginFilter);
        filtersMap.put("jwt", new JWTAuthFilter());
        shiroFilterFactoryBean.setFilters(filtersMap);

        shiroFilterFactoryBean.setFilterChainDefinitionMap(pattern);
        return shiroFilterFactoryBean;
    }

    @Bean
    public DefaultWebSecurityManager mySecurityManager() {
        DefaultWebSecurityManager securityManager = new DefaultWebSecurityManager();

//        securityManager.setCacheManager(cacheManager);
        List<Realm> realms = new ArrayList<>();
        // 统一角色权限控制realm
        realms.add(authorizingRealm());
        // 用户密码登录realm
        realms.add(userPasswordRealm());
        // 微信登录realm
        realms.add(wechatLoginRealm());
        realms.add(jwtRealm());
        securityManager.setRealms(realms);

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

    /**
     * 自定义的Realm管理，主要针对多realm
     */
    @Bean("myModularRealmAuthenticator")
    public MyModularRealmAuthenticator modularRealmAuthenticator() {
        MyModularRealmAuthenticator customizedModularRealmAuthenticator = new MyModularRealmAuthenticator();
        // 设置realm判断条件
        customizedModularRealmAuthenticator.setAuthenticationStrategy(new AtLeastOneSuccessfulStrategy());

        return customizedModularRealmAuthenticator;
    }

    @Bean
    public AuthorizingRealm authorizingRealm(){
        AuthorizationRealm authorizationRealm = new AuthorizationRealm();
        authorizationRealm.setName(LoginType.COMMON.getType());

        return authorizationRealm;
    }

    /**
     * 微信授权登录realm
     * @return WechatLoginRealm
     */
    @Bean
    public WechatLoginRealm wechatLoginRealm(){
        WechatLoginRealm wechatLoginRealm = new WechatLoginRealm();
        wechatLoginRealm.setName(LoginType.WECHAT_LOGIN.getType());

        return wechatLoginRealm;
    }

    /**
     * 密码登录realm
     *
     * @return UserPasswordRealm
     */
    @Bean
    public UserPasswordRealm userPasswordRealm() {
        UserPasswordRealm userPasswordRealm = new UserPasswordRealm();
        userPasswordRealm.setName(LoginType.USER_PASSWORD.getType());
        // 自定义的密码校验器
        userPasswordRealm.setCredentialsMatcher(credentialsMatcher());
        return userPasswordRealm;
    }

    @Bean
    public JWTRealm jwtRealm() {
        JWTRealm jwtRealm = new JWTRealm();
        jwtRealm.setName(LoginType.USER_PHONE.getType());
        // 自定义的密码校验器
        jwtRealm.setCredentialsMatcher(new JWTCredentialsMatcher());
        return jwtRealm;
    }

    /**
     * 用于账号密码登录
     * 在SecurityUtils.getSubject.isAuthenticated()时运行
     * 用此方法进行身份验证时，Shiro会将您提供的凭据与在Subject中保存的凭据进行比较。
     * 数据库中的密码通常在SimpleAuthenticationInfo（包含从数据库获取的凭据）中保存，
     * 而CredentialsMatcher会将它们与用户提交的相应凭据进行比较。
     * @return CredentialsMatcher
     */
    @Bean
    public CredentialsMatcher credentialsMatcher() {
        return (authenticationToken, authenticationInfo) -> {
            String submittedPassword = new String((char[])authenticationToken.getCredentials());
            String encryptedPassword = new Sha256Hash(submittedPassword, UserService.salt).toString();
            String storedPassword = (String)authenticationInfo.getCredentials();
            return encryptedPassword.equals(storedPassword);
        };
    }
}
