package com.westee.cake.entity;

import com.fasterxml.jackson.annotation.JsonProperty;

import java.io.Serializable;

public class ExpressCargoItem implements Serializable {
    @JsonProperty("item_name")
    private String item_name;
    @JsonProperty("count")
    private int count;
    @JsonProperty("item_pic_url")
    private String item_pic_url;

    public ExpressCargoItem(){}

    public ExpressCargoItem(String item_name, int count, String item_pic_url){
        this.item_name = item_name;
        this.count=count;
        this.item_pic_url = item_pic_url;
    }

    public void setItem_name(String item_name) {
        this.item_name = item_name;
    }

    public void setCount(int count) {
        this.count = count;
    }

    public void setItem_pic_url(String item_pic_url) {
        this.item_pic_url = item_pic_url;
    }
}
