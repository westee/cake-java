package com.westee.cake.entity;

public enum OrderType {
    GOODS(),
    CHARGE();

    public String getName() {
        return name().toLowerCase();
    }
}
