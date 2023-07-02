package com.westee.cake.data;

public enum ChargeType {
    WECHAT(),
    ALIPAY();

    public String getName() {
        return name().toLowerCase();
    }
}
