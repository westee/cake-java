package com.westee.cake.mapper;

import com.westee.cake.data.GoodsInfo;
import com.westee.cake.data.OrderInfo;
import com.westee.cake.generate.OrderTable;
import org.apache.ibatis.annotations.Mapper;

import java.util.List;

@Mapper
public interface MyOrderMapper {
    void insertOrders(OrderInfo orderInfo);

    List<GoodsInfo> getGoodsInfoOfOrder(long orderId);

    List<OrderTable> getOrderList(String status, long userId, int start, int offset);
}
