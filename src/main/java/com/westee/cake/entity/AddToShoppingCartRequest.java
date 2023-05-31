package com.westee.cake.entity;

import java.util.List;

public class AddToShoppingCartRequest {
    List<AddToShoppingCartItem> goods;

    public AddToShoppingCartRequest() {
    }

    public List<AddToShoppingCartItem> getGoods() {
        return goods;
    }

    public void setGoods(List<AddToShoppingCartItem> goods) {
        this.goods = goods;
    }
}
