package com.westee.cake.entity;

import com.westee.cake.exceptions.HttpException;

public enum OrderStatus {
    PENDING, // 待取货
    PAID,   // 已支付
    UNPAID, // 未支付 微信支付时尚未通过回调修改为PENDING
    CANCEL, // 已取消
    FAIL, // 微信扣款失败
    DELETED, // 已删除
    CHECK_REFUND, // 等待
    REFUNDED,  // 已退款
    RECEIVED; // 已收货

    public String getName() {
        return name().toLowerCase();
    }

    public static OrderStatus fromStatus(String name) {
        try {
            if (name == null) {
                return null;
            }
            return OrderStatus.valueOf(name.toUpperCase());
        } catch (IllegalArgumentException e) {
           throw HttpException.badRequest("参数不正确");
        }
    }

}
