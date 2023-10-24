package com.westee.cake.entity;

import com.fasterxml.jackson.annotation.JsonProperty;

import java.io.Serializable;
import java.util.List;

public class ExpressCargo implements Serializable {
    @JsonProperty("cargo_name")
    private String cargo_name;
    @JsonProperty("cargo_type")
    private int cargo_type;
    @JsonProperty("cargo_num")
    private int cargo_num;
    @JsonProperty("cargo_price")
    private int cargo_price;
    @JsonProperty("cargo_weight")
    private int cargo_weight;
    @JsonProperty("item_list")
    private List<ExpressCargoItem> item_list;

    public ExpressCargo() {}

    public ExpressCargo(String cargo_name, int cargo_type, int cargo_num, int cargo_price,
                        int cargo_weight, List<ExpressCargoItem> item_list) {
        this.cargo_name = cargo_name;
        this.cargo_type = cargo_type;
        this.cargo_num = cargo_num;
        this.cargo_price = cargo_price;
        this.cargo_weight = cargo_weight;
        this.item_list = item_list;
    }

    public void setCargo_name(String cargo_name) {
        this.cargo_name = cargo_name;
    }

    public void setCargo_type(int cargo_type) {
        this.cargo_type = cargo_type;
    }

    public void setCargo_num(int cargo_num) {
        this.cargo_num = cargo_num;
    }

    public void setCargo_price(int cargo_price) {
        this.cargo_price = cargo_price;
    }

    public void setCargo_weight(int cargo_weight) {
        this.cargo_weight = cargo_weight;
    }

    public void setItem_list(List<ExpressCargoItem> item_list) {
        this.item_list = item_list;
    }
}
