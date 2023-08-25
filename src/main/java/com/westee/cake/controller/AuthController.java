package com.westee.cake.controller;

import com.fasterxml.jackson.core.JsonProcessingException;
import com.westee.cake.entity.LoginResponse;
import com.westee.cake.entity.LoginResult;
import com.westee.cake.entity.TelAndCode;
import com.westee.cake.entity.UsernameAndPassword;
import com.westee.cake.entity.WXParams;
import com.westee.cake.entity.WeChatSession;
import com.westee.cake.generate.User;
import com.westee.cake.realm.JWTUtil;
import com.westee.cake.realm.LoginType;
import com.westee.cake.realm.UserToken;
import com.westee.cake.service.AuthService;
import com.westee.cake.service.CheckTelService;
import com.westee.cake.service.UserService;
import org.apache.shiro.SecurityUtils;
import org.apache.shiro.authc.AuthenticationException;
import org.apache.shiro.authc.IncorrectCredentialsException;
import org.apache.shiro.authc.LockedAccountException;
import org.apache.shiro.authc.UnknownAccountException;
import org.apache.shiro.authc.UsernamePasswordToken;
import org.apache.shiro.session.Session;
import org.apache.shiro.subject.Subject;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.web.bind.annotation.*;

import javax.servlet.http.HttpServletResponse;
import java.util.Map;

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

    /**
     *
     * @param params {wxcode, avatar, name}
     * @return
     * @throws JsonProcessingException
     */
    @GetMapping("/send-wxcode")
    public LoginResult sendWXCode(@RequestParam Map<String, String> params) throws JsonProcessingException {
        WeChatSession session = userService.getWeChatSession(params);
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

    @GetMapping("/token")
    public LoginResult testToken() {
        UserToken userToken = new UserToken(LoginType.USER_PHONE, "111111", "111111");
        return shiroLogin(userToken, LoginType.USER_PHONE);
    }

    @GetMapping("/test")
    public String test() {
        return "123";
    }

    @GetMapping("/status")
    public LoginResponse getStatus() {
        // 获取当前 Subject 对象
        Subject subject = SecurityUtils.getSubject();
        // 判断当前用户是否已经登录
        if (subject.isAuthenticated()) {
            // 获取当前用户的 principal（身份/凭证）
            Object principal = subject.getPrincipal();
            // 判断 principal 是否为 null，如果为 null 则表示当前用户没有登录或者登录已经过期
            if (principal != null) {
                // 对 principal 进行类型转换，通常需要根据具体的情况进行转换
                User user = (User) principal;
                return LoginResponse.alreadyLogin(user);
            }
        }
        return LoginResponse.notLogin();
    }

    public LoginResult shiroLogin(UserToken token, LoginType type) {
        try {
            //登录不在该处处理，交由shiro处理
            Subject subject = SecurityUtils.getSubject();
            subject.login(token);

            if (subject.isAuthenticated()) {
                String jwtToken = JWTUtil.sign(token.getUsername(), type); //使用JWTRealm生成token
                User userByName;
                if (type == LoginType.WECHAT_LOGIN) {
                    userByName = userService.getByOpenid(token.getUsername());
                } else {
                    userByName = userService.getUserByName(token.getUsername());
                }
                Session session = subject.getSession();
                session.setAttribute("user", userByName);
                return LoginResult.success("登录成功", userByName, false, jwtToken);
            } else {
                return LoginResult.fail("用户名密码不匹配");
            }
        } catch (IncorrectCredentialsException | UnknownAccountException e) {
            e.printStackTrace();
            return LoginResult.fail("用户名密码不匹配");
        } catch (LockedAccountException e) {
            return LoginResult.fail("账号被冻结");
        } catch (AuthenticationException e) {
            return LoginResult.fail("用户名或密码不正确");
        }catch (Exception e) {
            return LoginResult.fail(e.getMessage());
        }
    }
}
