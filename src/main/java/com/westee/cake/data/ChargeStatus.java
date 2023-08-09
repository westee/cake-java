package com.westee.cake.data;

public enum ChargeStatus {
    OK(),
    WAIT(),
    FAIL();

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
