package com.westee.cake.entity;

import com.westee.cake.generate.Goods;

import java.util.List;

public class ShoppingCartGoods extends Goods {
    private int number;
    private long shoppingCartId;
    private List<String> images;

    public int getNumber() {
        return number;
    }

    public void setNumber(int number) {
        this.number = number;
    }

    public long getShoppingCartId() {
        return shoppingCartId;
    }

    public void setShoppingCartId(long shoppingCartId) {
        this.shoppingCartId = shoppingCartId;
    }

    public List<String> getImages() {
        return images;
    }

    public void setImages(List<String> images) {
        this.images = images;
    }
}
