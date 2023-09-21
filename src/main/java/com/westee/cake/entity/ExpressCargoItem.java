package com.westee.cake.entity;

import com.fasterxml.jackson.annotation.JsonProperty;

public class ExpressCargoItem {
    @JsonProperty("item_name")
    private String itemName;
    @JsonProperty("count")
    private int count;
    @JsonProperty("item_pic_url")
    private String itemPicUrl;

    public ExpressCargoItem(){}

    public ExpressCargoItem(String itemName, int count, String itemPicUrl){
        this.itemName=itemName;
        this.count=count;
        this.itemPicUrl=itemPicUrl;
    }

    public void setItemName(String itemName) {
        this.itemName = itemName;
    }

    public void setCount(int count) {
        this.count = count;
    }

    public void setItemPicUrl(String itemPicUrl) {
        this.itemPicUrl = itemPicUrl;
    }
}
