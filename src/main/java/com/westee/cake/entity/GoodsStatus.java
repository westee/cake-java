package com.westee.cake.entity;

public enum GoodsStatus {
    OK(),
    DELETED(),
    ZERO(); // 0 售罄

    public String getName() {
        return  name().toLowerCase();
    }
}
