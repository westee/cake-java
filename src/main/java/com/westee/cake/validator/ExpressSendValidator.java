package com.westee.cake.validator;

import com.westee.cake.config.WeChatExpressConfig;

import java.math.BigDecimal;

public class ExpressSendValidator {
    private final String wx_store_id;
    private final String user_name;
    private final String user_phone;
    private final String user_lng;
    private final String user_lat;
    private final String user_address;
    private final CargoData cargo;

    public ExpressSendValidator(String user_name, String user_phone, String user_lng, String user_lat, String user_address, CargoData cargo) {
        this.wx_store_id = WeChatExpressConfig.getWxStoreId();
        this.user_name = user_name;
        this.user_phone = user_phone;
        this.user_lng = user_lng;
        this.user_lat = user_lat;
        this.user_address = user_address;
        this.cargo = cargo;
    }

    public boolean isValid() {
        if (wx_store_id == null || wx_store_id.isEmpty() ||
                user_name == null || user_name.isEmpty() ||
                user_phone == null || user_phone.isEmpty() ||
                user_lng == null || user_lng.isEmpty() ||
                user_lat == null || user_lat.isEmpty() ||
                user_address == null || user_address.isEmpty() ||
                cargo == null) {
            return false;
        }
        return cargo.isValid();
    }

    public String getWx_store_id() {
        return wx_store_id;
    }

    public String getUser_name() {
        return user_name;
    }

    public String getUser_phone() {
        return user_phone;
    }

    public String getUser_lng() {
        return user_lng;
    }

    public String getUser_lat() {
        return user_lat;
    }

    public String getUser_address() {
        return user_address;
    }

    public CargoData getCargo() {
        return cargo;
    }

    public static class CargoData {
        private String cargo_name;
        private String cargo_num;
        private String cargo_type;
        private String cargo_weight;
        private BigDecimal cargo_price;

        public CargoData(String cargo_name, String cargo_num, String cargo_type, String cargo_weight, BigDecimal cargo_price) {
            this.cargo_name = cargo_name;
            this.cargo_num = cargo_num;
            this.cargo_type = cargo_type;
            this.cargo_weight = cargo_weight;
            this.cargo_price = cargo_price.multiply(BigDecimal.valueOf(100));
        }

        public boolean isValid() {
            if (cargo_name == null || cargo_name.isEmpty() ||
                    cargo_num == null || cargo_num.isEmpty() ||
                    cargo_type == null || cargo_type.isEmpty() ||
                    cargo_weight == null || cargo_weight.isEmpty() ||
                    cargo_price.compareTo(BigDecimal.ZERO) <= 0)
//                    cargo_num <= 0 ||
//                    cargo_type <= 0 ||
//                    cargo_weight <= 0 ||
//                    cargo_price <= 0)
                {
                return false;
            }
            return true;
        }

        public String getCargo_name() {
            return cargo_name;
        }

        public String getCargo_num() {
            return cargo_num;
        }

        public String getCargo_type() {
            return cargo_type;
        }

        public String getCargo_weight() {
            return cargo_weight;
        }

        public BigDecimal getCargo_price() {
            return cargo_price;
        }
    }
}

