package com.westee.cake.data;

import com.westee.cake.generate.OrderTable;

import java.util.List;

/**
 * 为避免混淆，建议将用于返回给前端数据的OrderGoods重命名为OrderGoodsVO或者OrderGoodsDTO。通常，VO代表“Value Object”，
 * 将一组相关的值装载到一个对象中，而DTO代表“Data Transfer Object”，
 * 用于在不同层（例如控制器和业务逻辑）之间传输数据。无论选择哪个名称，
 * 都应始终明确表示它是一个用于传输数据或在不同层之间传递数据的对象，而不是一个数据库实体。这有助于减少开发中的混淆和错误。
 */
public class OrderGoodsVO {
    private OrderTable order;
    private List<GoodsInfo> goods;

    public OrderTable getOrder() {
        return order;
    }

    public void setOrder(OrderTable order) {
        this.order = order;
    }

    public List<GoodsInfo> getGoods() {
        return goods;
    }

    public void setGoods(List<GoodsInfo> goods) {
        this.goods = goods;
    }
}
