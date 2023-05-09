package com.westee.cake.entity;

public class TelAndPassword {
    private String tel;
    private String password;

    public TelAndPassword(String tel, String code) {
        this.tel = tel;
        this.password = code;
    }

    public String getTel() {
        return tel;
    }

    public void setTel(String tel) {
        this.tel = tel;
    }

    public String getPassword() {
        return password;
    }

    public void setPassword(String code) {
        this.password = code;
    }
}
