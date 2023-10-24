package com.westee.cake.entity;

import com.fasterxml.jackson.annotation.JsonProperty;

import java.io.Serializable;

public class ExpressCreate implements Serializable {
    @JsonProperty("wx_store_id")
    private String wx_store_id;
    @JsonProperty("store_order_id")
    private String store_order_id;
    @JsonProperty("user_openid")
    private String user_openid;
    @JsonProperty("user_lng")
    private String user_lng;
    @JsonProperty("user_lat")
    private String user_lat;
    @JsonProperty("user_address")
    private String user_address;
    @JsonProperty("user_name")
    private String user_name;
    @JsonProperty("user_phone")
    private String user_phone;
    @JsonProperty("callback_url")
    private String callback_url;
    @JsonProperty("use_sandbox")
    private Integer use_sandbox;
    @JsonProperty("order_detail_path")
    private String order_detail_path;
    @JsonProperty("cargo")
    private ExpressCargo cargo;

    public ExpressCreate() {}

    public ExpressCreate(String wx_store_id, String store_order_id, String user_openid, String user_lng, String user_lat,
                         String user_address, String user_name, String user_phone, String callback_url, ExpressCargo cargo) {
        this.wx_store_id = wx_store_id;
        this.store_order_id = store_order_id;
        this.user_openid = user_openid;
        this.user_lng = user_lng;
        this.user_lat = user_lat;
        this.user_address = user_address;
        this.user_name = user_name;
        this.user_phone = user_phone;
        this.callback_url = callback_url;
        this.cargo = cargo;
    }

    public ExpressCreate(String wx_store_id, String store_order_id, String user_openid, String user_lng, String user_lat,
                         String user_address, String user_name, String user_phone, String callback_url, Integer use_sandbox,
                         String order_detail_path, ExpressCargo cargo) {
        this.wx_store_id = wx_store_id;
        this.store_order_id = store_order_id;
        this.user_openid = user_openid;
        this.user_lng = user_lng;
        this.user_lat = user_lat;
        this.user_address = user_address;
        this.user_name = user_name;
        this.user_phone = user_phone;
        this.callback_url = callback_url;
        this.use_sandbox = use_sandbox;
        this.order_detail_path = order_detail_path;
        this.cargo = cargo;
    }

    public void setWx_store_id(String wx_store_id) {
        this.wx_store_id = wx_store_id;
    }

    public String getStore_order_id() {
        return store_order_id;
    }

    public void setStore_order_id(String store_order_id) {
        this.store_order_id = store_order_id;
    }

    public void setUser_openid(String user_openid) {
        this.user_openid = user_openid;
    }

    public void setUser_lng(String user_lng) {
        this.user_lng = user_lng;
    }

    public void setUser_lat(String user_lat) {
        this.user_lat = user_lat;
    }

    public void setUser_address(String user_address) {
        this.user_address = user_address;
    }

    public void setUser_name(String user_name) {
        this.user_name = user_name;
    }

    public void setUser_phone(String user_phone) {
        this.user_phone = user_phone;
    }

    public void setCallback_url(String callback_url) {
        this.callback_url = callback_url;
    }

    public void setCargo(ExpressCargo cargo) {
        this.cargo = cargo;
    }

    public void setUse_sandbox(Integer use_sandbox) {
        this.use_sandbox = use_sandbox;
    }

    public void setOrder_detail_path(String order_detail_path) {
        this.order_detail_path = order_detail_path;
    }
}
