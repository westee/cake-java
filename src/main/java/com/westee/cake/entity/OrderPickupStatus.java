package com.westee.cake.entity;

public enum OrderPickupStatus {
    STORE(0),
    EXPRESS(1);

    private final int value;

    OrderPickupStatus(int i) {
        value = i;
    }

    public byte getValue() {
        return (byte) value;
    }
}
