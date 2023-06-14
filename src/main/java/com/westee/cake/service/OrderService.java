package com.westee.cake.service;

import com.github.pagehelper.PageHelper;
import com.westee.cake.data.DataStatus;
import com.westee.cake.data.GoodsInfo;
import com.westee.cake.data.OrderGoodsVO;
import com.westee.cake.data.OrderInfo;
import com.westee.cake.entity.PageResponse;
import com.westee.cake.exceptions.HttpException;
import com.westee.cake.generate.OrderGoodsExample;
import com.westee.cake.generate.OrderGoods;
import com.westee.cake.generate.OrderGoodsMapper;
import com.westee.cake.generate.OrderTable;
import com.westee.cake.generate.OrderTableExample;
import com.westee.cake.generate.OrderTableMapper;
import com.westee.cake.mapper.MyOrderMapper;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.util.Collections;
import java.util.Date;
import java.util.List;
import java.util.Map;
import java.util.function.BooleanSupplier;
import java.util.stream.Collectors;

import static com.westee.cake.data.DataStatus.PENDING;
import static com.westee.cake.entity.GoodsStatus.DELETED;
import static java.util.stream.Collectors.toList;

@Service
public class OrderService {
    private OrderTableMapper orderMapper;

    private MyOrderMapper myOrderMapper;

    private OrderGoodsMapper orderGoodsMapper;

    @Autowired
    public OrderService(OrderTableMapper orderMapper, MyOrderMapper myOrderMapper, OrderGoodsMapper orderGoodsMapper) {
        this.orderMapper = orderMapper;
        this.myOrderMapper = myOrderMapper;
        this.orderGoodsMapper = orderGoodsMapper;
    }

    public OrderTable createOrder(OrderInfo orderInfo, OrderTable order) {
        insertOrder(order);
        orderInfo.setOrderId(order.getId());
        myOrderMapper.insertOrders(orderInfo);
        return order;
    }

    public OrderGoodsVO getOrderById(long orderId) {
        OrderTable order = orderMapper.selectByPrimaryKey(orderId);
        if (order == null) {
            return null;
        }
        List<GoodsInfo> goodsInfo = myOrderMapper.getGoodsInfoOfOrder(orderId);
        OrderGoodsVO result = new OrderGoodsVO();
        result.setGoods(goodsInfo);
        result.setOrder(order);
        return result;
    }

    public OrderGoodsVO deleteOrder(long orderId, long userId) {
        OrderTable order = orderMapper.selectByPrimaryKey(orderId);
        if (order == null) {
            throw HttpException.notFound("订单未找到: " + orderId);
        }
        if (order.getUserId() != userId) {
            throw HttpException.forbidden("无权访问！");
        }

        List<GoodsInfo> goodsInfo = myOrderMapper.getGoodsInfoOfOrder(orderId);

        order.setStatus(DELETED.getName());
        order.setUpdatedAt(new Date());
        orderMapper.updateByPrimaryKey(order);

        OrderGoodsVO result = new OrderGoodsVO();
        result.setGoods(goodsInfo);
        result.setOrder(order);
        return result;
    }

    public PageResponse<OrderGoodsVO> getOrder(long userId,
                                             Integer pageNum,
                                             Integer pageSize,
                                             DataStatus status) {
        OrderTableExample countByStatus = new OrderTableExample();
        setStatus(countByStatus, status).andUserIdEqualTo(userId);
        int count = (int) orderMapper.countByExample(countByStatus);

        OrderTableExample pagedOrder = new OrderTableExample();
//        pagedOrder.setOffset((pageNum - 1) * pageSize);
//        pagedOrder.setLimit(pageNum);
        setStatus(pagedOrder, status).andUserIdEqualTo(userId);

        PageHelper.startPage(pageNum, pageSize);
        List<OrderTable> orders = orderMapper.selectByExample(pagedOrder);
        List<OrderGoods> orderGoods = getOrderGoods(orders);

        int totalPage = count % pageSize == 0 ? count / pageSize : count / pageSize + 1;

        Map<Long, List<OrderGoods>> orderIdToGoodsMap = orderGoods
                .stream()
                .collect(Collectors.groupingBy(OrderGoods::getOrderId, toList()));

        List<OrderGoodsVO> rpcOrderGoods = orders.stream()
                .map(order -> toRpcOrderGoods(order, orderIdToGoodsMap))
                .collect(toList());

        return PageResponse.pageData(pageNum,
                pageSize,
                totalPage,
                rpcOrderGoods);
    }

    private List<OrderGoods> getOrderGoods(List<OrderTable> orders) {
        if (orders.isEmpty()) {
            return Collections.emptyList();
        }

        List<Long> orderIds = orders.stream().map(OrderTable::getId).collect(toList());

        OrderGoodsExample selectByOrderIds = new OrderGoodsExample();
        selectByOrderIds.createCriteria().andOrderIdIn(orderIds);
        return orderGoodsMapper.selectByExample(selectByOrderIds);
    }

    public OrderGoodsVO updateOrder(OrderTable order) {
        orderMapper.updateByPrimaryKey(order);

        List<GoodsInfo> goodsInfo = myOrderMapper.getGoodsInfoOfOrder(order.getId());
        OrderGoodsVO result = new OrderGoodsVO();
        result.setGoods(goodsInfo);
        result.setOrder(orderMapper.selectByPrimaryKey(order.getId()));
        return result;
    }

    private OrderGoodsVO toRpcOrderGoods(OrderTable order, Map<Long, List<OrderGoods>> orderIdToGoodsMap) {
        OrderGoodsVO result = new OrderGoodsVO();
        result.setOrder(order);
        List<GoodsInfo> goodsInfos = orderIdToGoodsMap
                .getOrDefault(order.getId(), Collections.emptyList())
                .stream()
                .map(this::toGoodsInfo)
                .collect(toList());
        result.setGoods(goodsInfos);
        return result;
    }

    private GoodsInfo toGoodsInfo(OrderGoods orderGoods) {
        GoodsInfo result = new GoodsInfo();
        result.setId(orderGoods.getGoodsId());
        result.setNumber(orderGoods.getNumber().intValue());
        return result;
    }

    private OrderTableExample.Criteria setStatus(OrderTableExample orderExample, DataStatus status) {
        if (status == null) {
            return orderExample.createCriteria().andStatusNotEqualTo(DELETED.getName());
        } else {
            return orderExample.createCriteria().andStatusEqualTo(status.getName());
        }
    }

    private void insertOrder(OrderTable order) {
        order.setStatus(PENDING.getName());

        verify(() -> order.getUserId() == null, "userId不能为空！");
        verify(() -> order.getTotalPrice() == null || order.getTotalPrice().doubleValue() < 0, "totalPrice非法！");
        verify(() -> order.getAddress() == null, "address不能为空！");

        order.setExpressCompany(null);
        order.setExpressId(null);
        order.setCreatedAt(new Date());
        order.setUpdatedAt(new Date());

        orderMapper.insert(order);
    }

    private void verify(BooleanSupplier supplier, String message) {
        if (supplier.getAsBoolean()) {
            throw new IllegalArgumentException(message);
        }
    }
}
