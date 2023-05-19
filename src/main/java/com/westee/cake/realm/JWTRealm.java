package com.westee.cake.realm;

import com.westee.cake.generate.User;
import com.westee.cake.service.UserService;
import lombok.extern.slf4j.Slf4j;
import org.apache.shiro.authc.*;
import org.apache.shiro.authz.AuthorizationInfo;
import org.apache.shiro.authz.SimpleAuthorizationInfo;
import org.apache.shiro.realm.AuthorizingRealm;
import org.apache.shiro.subject.PrincipalCollection;
import org.springframework.beans.factory.annotation.Autowired;

@Slf4j
public class JWTRealm extends AuthorizingRealm {
    @Autowired
    private UserService userService;

    @Override
    public String getName() {
        return super.getName();
    }

    @Override
    public boolean supports(AuthenticationToken token) {
        if (token instanceof UserToken) {
            return ((UserToken) token).getLoginType() == LoginType.USER_PASSWORD;
        } else {
            return false;
        }
    }

    @Override
    protected AuthorizationInfo doGetAuthorizationInfo(PrincipalCollection principals) {
        String username = JWTUtil.getUsername(principals.toString());
        //查询数据库，获取用户角色
        SimpleAuthorizationInfo simpleAuthorizationInfo = new SimpleAuthorizationInfo();
        simpleAuthorizationInfo.addRole("role1");
        simpleAuthorizationInfo.addRole("role2");
        return simpleAuthorizationInfo;
    }

    @Override
    protected AuthenticationInfo doGetAuthenticationInfo(AuthenticationToken auth) throws AuthenticationException {
//        log.info("---------------- 用户 jwt ----------------------");
//        String token = (String) auth.getCredentials();
//        String username = JWTUtil.getUsername(token);
//        User user = userService.getUserByName(username);
//        //查询数据库，获取用户密码
//        String password = user.getPassword();
//        if (username == null || !password.equals(token)) {
//            throw new AuthenticationException("token无效");
//        }
//        return new SimpleAuthenticationInfo(token, token, "JWTRealm");
        log.info("---------------- 用户 jwt ----------------------");
        UsernamePasswordToken token = (UsernamePasswordToken) auth;
        String name = token.getUsername();
        // 从数据库获取对应用户名密码的用户
        User user = userService.getUserByName(name);
        if (user != null) {
            // 用户为禁用状态
//            if (!user.getLoginFlag().equals("1")) {
//                throw new DisabledAccountException();
//            }
            SimpleAuthenticationInfo authenticationInfo = new SimpleAuthenticationInfo(
                    user, //用户
                    user.getPassword(), //密码
                    getName()  //realm name
            );
            return authenticationInfo;
        }
        throw new UnknownAccountException();
    }

    @Override
    public void setAuthorizationCacheName(String authorizationCacheName) {
        super.setAuthorizationCacheName(authorizationCacheName);
    }

    @Override
    protected void clearCachedAuthorizationInfo(PrincipalCollection principals) {
        super.clearCachedAuthorizationInfo(principals);
    }
}
