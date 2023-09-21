package com.westee.cake.entity;

import com.fasterxml.jackson.annotation.JsonProperty;

public class ExpressCreate {
    @JsonProperty("wx_store_id")
    private String wxStoreId;
    @JsonProperty("store_order_id")
    private String storeOrderId;
    @JsonProperty("user_openid")
    private String userOpenid;
    @JsonProperty("user_lng")
    private String userLng;
    @JsonProperty("user_lat")
    private String userLat;
    @JsonProperty("user_address")
    private String userAddress;
    @JsonProperty("user_name")
    private String userName;
    @JsonProperty("user_phone")
    private String userPhone;
    @JsonProperty("callback_url")
    private String callbackUrl;
    @JsonProperty("use_sandbox")
    private Integer useSandbox;
    @JsonProperty("order_detail_path")
    private String orderDetailPath;
    @JsonProperty("cargo")
    private ExpressCargo cargo;

    public ExpressCreate() {}

    public ExpressCreate(String storeOrderId, String userOpenid, String userLng, String userLat,
                 String userAddress, String userName, String userPhone, String callbackUrl, ExpressCargo cargo) {
        this.wxStoreId = storeOrderId;
        this.storeOrderId = storeOrderId;
        this.userOpenid = userOpenid;
        this.userLng = userLng;
        this.userLat = userLat;
        this.userAddress = userAddress;
        this.userName = userName;
        this.userPhone = userPhone;
        this.callbackUrl = callbackUrl;
        this.cargo = cargo;
    }

    public void setWxStoreId(String wxStoreId) {
        this.wxStoreId = wxStoreId;
    }

    public void setStoreOrderId(String storeOrderId) {
        this.storeOrderId = storeOrderId;
    }

    public void setUserOpenid(String userOpenid) {
        this.userOpenid = userOpenid;
    }

    public void setUserLng(String userLng) {
        this.userLng = userLng;
    }

    public void setUserLat(String userLat) {
        this.userLat = userLat;
    }

    public void setUserAddress(String userAddress) {
        this.userAddress = userAddress;
    }

    public void setUserName(String userName) {
        this.userName = userName;
    }

    public void setUserPhone(String userPhone) {
        this.userPhone = userPhone;
    }

    public void setCallbackUrl(String callbackUrl) {
        this.callbackUrl = callbackUrl;
    }

    public void setCargo(ExpressCargo cargo) {
        this.cargo = cargo;
    }

    public void setUseSandbox(Integer useSandbox) {
        this.useSandbox = useSandbox;
    }

    public void setOrderDetailPath(String orderDetailPath) {
        this.orderDetailPath = orderDetailPath;
    }
}
