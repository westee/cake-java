package com.westee.cake.entity;

import com.westee.cake.generate.User;

public class LoginResult extends Result<User> {
    boolean loginStatus;
    String token;

    public LoginResult() {
        super(null, null);
    }

    public LoginResult(String status, String msg) {
        super(status, msg);
    }

    public LoginResult(String status, String msg, boolean loginStatus) {
        super(status, msg);
        this.loginStatus = loginStatus;
    }

    public LoginResult(String status, String msg, User data, boolean loginStatus, String token) {
        super(status, msg, data);
        this.loginStatus = loginStatus;
        this.token = token;
    }

    public static LoginResult fail(String msg) {
        return new LoginResult("fail", msg, false);
    }

    public static LoginResult success(String msg, User data, boolean isLogin, String token) {
        return new LoginResult("ok", msg, data, isLogin, token);
    }

    public boolean isLoginStatus() {
        return loginStatus;
    }

    public String getToken() {
        return token;
    }
}
