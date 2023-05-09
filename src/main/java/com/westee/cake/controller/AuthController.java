package com.westee.cake.controller;

import com.westee.cake.entity.LoginResponse;
import com.westee.cake.entity.TelAndCode;
import com.westee.cake.entity.TelAndPassword;
import com.westee.cake.entity.UsernameAndPassword;
import com.westee.cake.service.AuthService;
import com.westee.cake.service.CheckTelService;
import com.westee.cake.service.UserContext;
import org.apache.shiro.SecurityUtils;
import org.apache.shiro.authc.UsernamePasswordToken;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.web.bind.annotation.*;

import javax.servlet.http.HttpServletResponse;

@RestController
@RequestMapping("/api/v1")
public class AuthController {
    private final AuthService authService;
    private final CheckTelService checkTelService;

    @Autowired
    public AuthController(AuthService authService, CheckTelService checkTelService) {
        this.checkTelService = checkTelService;
        this.authService = authService;
    }


    @PostMapping("/login")
    public void login(@RequestBody TelAndCode telAndCode) {
        // https://shiro.apache.org/authentication.html
        UsernamePasswordToken token = new UsernamePasswordToken(telAndCode.getTel(),
                                                                telAndCode.getCode());
        token.setRememberMe(true);
        SecurityUtils.getSubject().login(token);
    }

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
}
