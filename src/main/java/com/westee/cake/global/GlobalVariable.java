package com.westee.cake.global;

public enum GlobalVariable {
    INSTANCE;

    private String accessToken;

    GlobalVariable() {
        accessToken = "";
    }

    public String getAccessToken() {
        return accessToken;
    }

    public void setAccessToken(String accessToken) {
        this.accessToken = accessToken;
    }

}
