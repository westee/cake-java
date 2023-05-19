package com.westee.cake.realm;

import org.apache.commons.lang3.StringUtils;
import org.apache.shiro.SecurityUtils;
import org.apache.shiro.authc.AuthenticationException;
import org.apache.shiro.authc.AuthenticationToken;
import org.apache.shiro.web.filter.authc.AuthenticatingFilter;

import javax.servlet.ServletRequest;
import javax.servlet.ServletResponse;
import javax.servlet.http.HttpServletRequest;

public class JWTAuthFilter extends AuthenticatingFilter {

    @Override
    protected AuthenticationToken createToken(ServletRequest request, ServletResponse response) throws Exception {
        //从请求头中获取token
        String token = getRequestToken((HttpServletRequest) request);
        if (StringUtils.isEmpty(token)) {
            throw new AuthenticationException("token不能为空");
        }
        return new JWTToken(token);
    }

    @Override
    protected boolean onAccessDenied(ServletRequest request, ServletResponse response) throws Exception {
        //进行token校验
        JWTToken jwtToken = (JWTToken) this.createToken(request, response);
        SecurityUtils.getSubject().login(jwtToken);
        return true;
    }

    //从请求头中获取token
    private String getRequestToken(HttpServletRequest request) {
        //从Header中获取token
        String token = request.getHeader("Authorization");
        if (StringUtils.isNotEmpty(token) && token.startsWith("Bearer ")) {
            token = token.substring(7);
        }
        return token;
    }

    @Override
    protected boolean isAccessAllowed(ServletRequest request, ServletResponse response, Object mappedValue) {
        String token = getRequestToken((HttpServletRequest) request);
        if (StringUtils.isEmpty(token)) {
            return false;
        }
        try {
            //验证token
            JWTUtil.verify(token); //调用JWTUtil工具类验证token
        } catch (Exception e) {
            e.printStackTrace();
            return false;
        }
        return true;
    }
}
