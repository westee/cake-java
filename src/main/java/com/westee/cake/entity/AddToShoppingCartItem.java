package com.westee.cake.entity;

public class AddToShoppingCartItem {
    private long id;
    private int number;

    public AddToShoppingCartItem() {
    }

    public long getId() {
        return id;
    }

    public void setId(int id) {
        this.id = id;
    }

    public int getNumber() {
        return number;
    }

    public void setNumber(int number) {
        this.number = number;
    }
}
