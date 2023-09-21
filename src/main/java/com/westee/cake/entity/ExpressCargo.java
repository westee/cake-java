package com.westee.cake.entity;

import com.fasterxml.jackson.annotation.JsonProperty;

import java.util.List;

public class ExpressCargo {
    @JsonProperty("cargo_name")
    private String cargoName;
    @JsonProperty("cargo_type")
    private int cargoType;
    @JsonProperty("cargo_num")
    private int cargoNum;
    @JsonProperty("cargo_price")
    private int cargoPrice;
    @JsonProperty("cargo_weight")
    private int cargoWeight;
    @JsonProperty("item_list")
    private List<ExpressCargoItem> itemList;

    public ExpressCargo() {}

    public ExpressCargo(String cargoName, int cargoType, int cargoNum, int cargoPrice,
                 int cargoWeight, List<ExpressCargoItem> itemList) {
        this.cargoName = cargoName;
        this.cargoType = cargoType;
        this.cargoNum = cargoNum;
        this.cargoPrice = cargoPrice;
        this.cargoWeight = cargoWeight;
        this.itemList = itemList;
    }

    public void setCargoName(String cargoName) {
        this.cargoName = cargoName;
    }

    public void setCargoType(int cargoType) {
        this.cargoType = cargoType;
    }

    public void setCargoNum(int cargoNum) {
        this.cargoNum = cargoNum;
    }

    public void setCargoPrice(int cargoPrice) {
        this.cargoPrice = cargoPrice;
    }

    public void setCargoWeight(int cargoWeight) {
        this.cargoWeight = cargoWeight;
    }

    public void setItemList(List<ExpressCargoItem> itemList) {
        this.itemList = itemList;
    }
}
