package com.westee.cake.global;

public enum WeChatSubscribeTemplate {
    INSTANCE;

    private String Express;
    private String Order;

    WeChatSubscribeTemplate() {
        Express = "wFJTEkXKAPYv8QON7Im3sd62h4lXd0q72c1XkHyfLjs";
        Order = "dE-GQYObGi8YFJy1gDpby7cBvuis50PPH2Sf_uFkqLw";
    }

    public String getExpress() {
        return Express;
    }

    public void setExpress(String express) {
        Express = express;
    }

    public String getOrder() {
        return Order;
    }

    public void setOrder(String order) {
        Order = order;
    }
}
