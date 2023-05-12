package com.westee.cake.controller;

import com.alibaba.fastjson2.JSONObject;
import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.westee.cake.entity.*;
import com.westee.cake.generate.User;
import com.westee.cake.realm.LoginType;
import com.westee.cake.realm.UserToken;
import com.westee.cake.service.AuthService;
import com.westee.cake.service.CheckTelService;
import com.westee.cake.service.UserContext;
import com.westee.cake.service.UserService;
import org.apache.shiro.SecurityUtils;
import org.apache.shiro.authc.IncorrectCredentialsException;
import org.apache.shiro.authc.LockedAccountException;
import org.apache.shiro.authc.UnknownAccountException;
import org.apache.shiro.authc.UsernamePasswordToken;
import org.apache.shiro.subject.Subject;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpMethod;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.client.RestTemplate;

import javax.servlet.http.HttpServletResponse;

@RestController
@RequestMapping("/api/v1")
public class AuthController {
    private final AuthService authService;
    private final CheckTelService checkTelService;
    private final UserService userService;


    @Autowired
    public AuthController(AuthService authService, CheckTelService checkTelService, UserService userService) {
        this.checkTelService = checkTelService;
        this.authService = authService;
        this.userService = userService;
    }


    @PostMapping("/login")
    public void login(@RequestBody TelAndCode telAndCode) {
        // https://shiro.apache.org/authentication.html
        UsernamePasswordToken token = new UsernamePasswordToken(telAndCode.getTel(),
                telAndCode.getCode());
        token.setRememberMe(true);
        SecurityUtils.getSubject().login(token);
    }

    @PostMapping("/login-password")
    public LoginResult login(@RequestBody UsernameAndPassword usernameAndPassword) {
        UserToken userToken = new UserToken(LoginType.USER_PASSWORD, usernameAndPassword.getUsername(),
                usernameAndPassword.getPassword());
        return shiroLogin(userToken, LoginType.USER_PASSWORD);
    }

    @PostMapping("/login-wechat")
    public LoginResult login(@RequestBody WXParams usernameAndPassword) {
        UserToken token = new UserToken(LoginType.WECHAT_LOGIN, usernameAndPassword.getCode(),
                usernameAndPassword.getCode(), usernameAndPassword.getCode());
        return shiroLogin(token, LoginType.WECHAT_LOGIN);
    }

    @PostMapping("/register-password")
    public void register(@RequestBody UsernameAndPassword usernameAndPassword) {
        userService.createUserIfNotExist(usernameAndPassword);
    }

    @GetMapping("/send-wxcode")
    public LoginResult sendWXCode(String wxcode) throws JsonProcessingException {
        WeChatSession session = userService.getWeChatSession(wxcode);
        String openid = session.getOpenid();
        UserToken token = new UserToken(LoginType.WECHAT_LOGIN, openid,
                openid, openid);
        token.setRememberMe(true);
        return shiroLogin(token, LoginType.WECHAT_LOGIN);
    }


    // application/x-www-form-urlencoded
//    @PostMapping("/send-wxcode")
//    public void sendWXCode(@RequestParam String wxcode) {
//        System.out.println(wxcode);
//    }

    @PostMapping("/code")
    public void sendCode(@RequestBody TelAndCode telAndCode, HttpServletResponse response) {
        if (checkTelService.verifyTelParams(telAndCode)) {
            authService.sendVerificationCode(telAndCode.getTel());
        } else {
            response.setStatus(HttpStatus.BAD_REQUEST.value());
        }
    }

    @GetMapping("/status")
    public LoginResponse getStatus() {
        if (UserContext.getCurrentUser() != null) {
            return LoginResponse.alreadyLogin(UserContext.getCurrentUser());
        } else {
            return LoginResponse.notLogin();
        }
    }

    public LoginResult shiroLogin(UserToken token, LoginType type) {
        try {
            //登录不在该处处理，交由shiro处理
            Subject subject = SecurityUtils.getSubject();
            subject.login(token);

            if (subject.isAuthenticated()) {
                JSONObject json = new JSONObject();
                json.put("token", subject.getSession().getId());
                User userByName;
                if (type == LoginType.WECHAT_LOGIN) {
                    userByName = userService.getByOpenid(token.getUsername());
                } else {
                    userByName = userService.getUserByName(token.getUsername());
                }
                return LoginResult.success("登录成功", userByName, true);
            } else {
                return LoginResult.fail("用户名密码不匹配");
            }
        } catch (IncorrectCredentialsException | UnknownAccountException e) {
            e.printStackTrace();
            return LoginResult.fail("用户名密码不匹配");
        } catch (LockedAccountException e) {
            return LoginResult.fail("账号被冻结");
        } catch (Exception e) {
            return LoginResult.fail(e.getMessage());
        }
    }
}
