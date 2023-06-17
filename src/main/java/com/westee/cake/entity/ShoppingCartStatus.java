package com.westee.cake.entity;

import com.westee.cake.data.DataStatus;

public enum ShoppingCartStatus {
    OK(),
    DELETED(),
    PAID();

    public String getName() {
        return name().toLowerCase();
    }

    public static DataStatus fromStatus(String name) {
        try {
            if (name == null) {
                return null;
            }
            return DataStatus.valueOf(name.toUpperCase());
        } catch (IllegalArgumentException e) {
            return null;
        }
    }
}
