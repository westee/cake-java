package com.westee.cake.entity;

import com.westee.cake.generate.User;

public class LoginResult extends Result<User> {
    boolean isLogin;
    String token;

    public LoginResult(String status, String msg, boolean isLogin) {
        super(status, msg);
        this.isLogin = isLogin;
    }

    public LoginResult(String status, String msg, User data, boolean isLogin, String token) {
        super(status, msg, data);
        this.isLogin = isLogin;
        this.token = token;
    }

    public static LoginResult fail(String msg) {
        return new LoginResult("fail", msg, false);
    }

    public static LoginResult success(String msg, User data, boolean isLogin, String token) {
        return new LoginResult("ok", msg, data, isLogin, token);
    }

    public boolean isLogin() {
        return isLogin;
    }

    public String getToken() {
        return token;
    }
}
