package com.westee.cake.data;

import java.util.List;

public class OrderInfo {
    private long orderId;
    private long addressId;
    private List<GoodsInfo> goods;

    public long getAddressId() { return addressId; }

    public void setAddressId(long address) {
        this.addressId = address;
    }

    public long getOrderId() {
        return orderId;
    }

    public void setOrderId(long orderId) {
        this.orderId = orderId;
    }

    public List<GoodsInfo> getGoods() {
        return goods;
    }

    public void setGoods(List<GoodsInfo> goods) {
        this.goods = goods;
    }
}