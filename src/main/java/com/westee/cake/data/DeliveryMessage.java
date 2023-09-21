package com.westee.cake.data;

import java.io.Serializable;
import java.util.Date;

public class DeliveryMessage implements Serializable {
    String message;
    Date deliveryTime;
    Long orderNo;

    public DeliveryMessage(String message, Date deliveryTime, Long orderId) {
        this.message = message;
        this.deliveryTime = deliveryTime;
        this.orderNo = orderId;
    }

    public Date getDeliveryTime() {
        return deliveryTime;
    }
}