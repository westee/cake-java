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

    public String getName() {
        return name().toLowerCase();
    }

    public static OrderPickupStatus fromString(String value) {
        for (OrderPickupStatus type : OrderPickupStatus.values()) {
            if (type.getName().equals(value.toLowerCase())) {
                return type;
            }
        }
        throw new IllegalArgumentException("Invalid PayType: " + value);
    }
}
