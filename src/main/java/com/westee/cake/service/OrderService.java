package com.westee.cake.service;

import com.wechat.pay.java.core.exception.MalformedMessageException;
import com.wechat.pay.java.core.exception.ServiceException;
import com.wechat.pay.java.service.payments.jsapi.model.PrepayWithRequestPaymentResponse;
import com.wechat.pay.java.service.refund.model.Amount;
import com.wechat.pay.java.service.refund.model.Refund;
import com.wechat.pay.java.service.refund.model.Status;
import com.westee.cake.dao.GoodsStockMapper;
import com.westee.cake.dao.MyShoppingCartMapper;
import com.westee.cake.data.GoodsInfo;
import com.westee.cake.data.OrderGoodsVO;
import com.westee.cake.data.OrderInfo;
import com.westee.cake.entity.ExpressCreate;
import com.westee.cake.entity.GoodsWithNumber;
import com.westee.cake.entity.OrderPickupStatus;
import com.westee.cake.entity.OrderResponse;
import com.westee.cake.entity.OrderStatus;
import com.westee.cake.entity.OrderType;
import com.westee.cake.entity.PageResponse;
import com.westee.cake.entity.ShoppingCartStatus;
import com.westee.cake.exceptions.HttpException;
import com.westee.cake.generate.*;
import com.westee.cake.mapper.MyOrderMapper;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.amqp.rabbit.core.RabbitTemplate;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.math.BigDecimal;
import java.util.ArrayList;
import java.util.Calendar;
import java.util.Collections;
import java.util.Date;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.Objects;
import java.util.Random;
import java.util.function.BooleanSupplier;
import java.util.stream.Collectors;

import static com.westee.cake.entity.GoodsStatus.DELETED;
import static java.util.stream.Collectors.toList;

@Service
public class OrderService {
    private final OrderTableMapper orderMapper;

    private final MyOrderMapper myOrderMapper;

    private final OrderGoodsMapper orderGoodsMapper;

    private final ShopMapper shopMapper;

    private final GoodsService goodsService;

    private final GoodsStockMapper goodsStockMapper;

    private final AddressMapper addressMapper;

    private final UserMapper userMapper;

    private final UserCouponMapper userCouponMapper;

    private final CouponMapper couponMapper;

    private final WechatPayService wechatPayService;

    private final MyShoppingCartMapper myShoppingCartMapper;

    private final UserService userService;

    private final WechatExpressService wechatExpressService;

    private final RabbitTemplate rabbitTemplate;

    private static final Logger log = LoggerFactory.getLogger(OrderService.class);

    @Autowired
    public OrderService(OrderTableMapper orderMapper, MyOrderMapper myOrderMapper, OrderGoodsMapper orderGoodsMapper,
                        ShopMapper shopMapper, GoodsService goodsService, GoodsStockMapper goodsStockMapper,
                        AddressMapper addressMapper,
                        UserMapper userMapper, UserCouponMapper userCouponMapper, CouponMapper couponMapper,
                        WechatPayService wechatPayService, MyShoppingCartMapper myShoppingCartMapper,
                        UserService userService, WechatExpressService wechatExpressService, RabbitTemplate rabbitTemplate) {
        this.shopMapper = shopMapper;
        this.orderMapper = orderMapper;
        this.goodsService = goodsService;
        this.myOrderMapper = myOrderMapper;
        this.goodsStockMapper = goodsStockMapper;
        this.orderGoodsMapper = orderGoodsMapper;
        this.addressMapper = addressMapper;
        this.userMapper = userMapper;
        this.userCouponMapper = userCouponMapper;
        this.couponMapper = couponMapper;
        this.wechatPayService = wechatPayService;
        this.myShoppingCartMapper = myShoppingCartMapper;
        this.userService = userService;
        this.wechatExpressService = wechatExpressService;
        this.rabbitTemplate = rabbitTemplate;
    }

    /**
     * 减库存 使用优惠券 扣款
     * 插入order order_goods
     * 微信支付时需要在回调成功后创建快递订单
     *
     * @param orderInfo { orderId, addressId, List<GoodsInfo> goods }
     * @param userId    用户id
     * @param couponId  优惠券id
     * @return OrderResponse
     * @throws RuntimeException 支付错误
     */
    @Transactional
    public OrderResponseWithPayInfo createOrder(OrderInfo orderInfo, OrderTable requestOrder, Long userId, Long couponId) throws RuntimeException {

        Map<Long, Goods> idToGoodsMap = getIdTOGoodsMap(orderInfo.getGoods());
        BigDecimal totalPrice = calculateTotalPrice(orderInfo, idToGoodsMap);
        User user = userMapper.selectByPrimaryKey(userId);

        String orderNo = generateOrderNo();

        // 使用优惠券
        BigDecimal finalPrice = Objects.isNull(couponId) ? totalPrice : useCoupon(userId, couponId, totalPrice);

        // 创建订单
        OrderTable createdOrder = createdOrderViaRpc(orderInfo, requestOrder, userId, orderNo, totalPrice, finalPrice, couponId);

        // 扣款
        if (Objects.equals(requestOrder.getPayType(), PayType.BALANCE.getName())) {  // 余额支付
            deductUserMoney(userId, finalPrice);
            deductStock(orderInfo);
            addDeliveryToMQ(requestOrder, idToGoodsMap, finalPrice, orderInfo, user.getWxOpenId(), orderNo);
            return new OrderResponseWithPayInfo(generateResponse(createdOrder, idToGoodsMap, orderInfo.getGoods()));

        } else {    //  微信支付 回调成功后扣减库存
            String detail = idToGoodsMap.values().stream().map(Goods::getName).collect(Collectors.joining(","));
            OrderResponseWithPayInfo orderResponseWithPayInfo = new OrderResponseWithPayInfo();

            try {
                // ... 调用接口
                PrepayWithRequestPaymentResponse response = wechatPayService.
                        prepayWithRequestPayment(detail, orderNo, finalPrice, user.getWxOpenId(),
                                OrderType.GOODS);
                orderResponseWithPayInfo.setPrepay(response);

            } catch (com.wechat.pay.java.core.exception.HttpException e) { // 发送HTTP请求失败
                // 调用e.getHttpRequest()获取请求打印日志或上报监控，更多方法见HttpException定义
                log.error("发送HTTP请求失败 {}", e.getHttpRequest());
            } catch (ServiceException e) { // 服务返回状态小于200或大于等于300，例如500
                // 调用e.getResponseBody()获取返回体打印日志或上报监控，更多方法见ServiceException定义
                log.error("状态码异常 {}", e.getResponseBody());
            } catch (MalformedMessageException e) { // 服务返回成功，返回体类型不合法，或者解析返回体失败
                // 调用e.getMessage()获取信息打印日志或上报监控，更多方法见MalformedMessageException定义
                log.error("服务返回成功，返回体类型不合法，或者解析返回体失败 {}", e.getMessage());
            }
            return orderResponseWithPayInfo;
        }
    }

    /**
     * 到店
     * 立即取货
     * 预约取货
     * 到店不需要特殊操作，只需要显示取货时间和取货码
     * <p>
     * 外送
     * 立即送货  直接调用外卖接口开始送货
     * 预约取货  到时间取货
     *
     * @param order        取货时间，如果为空则立即送货; 取货方式 0 到店； 1外送
     * @param idToGoodsMap
     */
    public void addDeliveryToMQ(OrderTable order, Map<Long, Goods> idToGoodsMap, BigDecimal finalPrice,
                                OrderInfo orderInfo, String openid, String orderNo) {
        Date appointmentTime = order.getPickUpTime();

        if (Objects.equals(order.getPickupType(), OrderPickupStatus.EXPRESS.getValue())) {
            ExpressCreate expressCreate = wechatExpressService.collectExpressInfo(idToGoodsMap, finalPrice, orderInfo, openid, orderNo);
            ExpressInfo expressInfo = wechatExpressService.insertExpressInfo(expressCreate, orderNo);
            if (Objects.isNull(appointmentTime)) {
                System.out.println("添加到立即队列中");
                sendOrder(expressInfo);
            } else {
                System.out.println("添加到延迟队列中");
                System.out.println(appointmentTime);
                // 预约配送时间
                if (appointmentTime.getTime() < new Date().getTime()) {
                    throw HttpException.badRequest("派送时间应该晚于当前时间");
                }
                sendDelayedOrder(appointmentTime, expressInfo);
            }
        }
    }

    public void sendOrder(ExpressInfo expressInfo) {
        rabbitTemplate.convertAndSend("orderExchange", "orderRoutingKey", expressInfo, message -> {
            message.getMessageProperties().setExpiration("60000");// 设置消息过期时间，单位毫秒
            message.getMessageProperties().setMessageId(expressInfo.getWxOrderNo());// 设置消息过期时间，单位毫秒
            return message;
        });
    }

    public void sendDelayedOrder(Date pickUpTime, ExpressInfo expressInfo) {
        rabbitTemplate.convertAndSend("orderExchange", "orderDelayRoutingKey", expressInfo, message -> {
            message.getMessageProperties().setExpiration(String.valueOf(getMilSecond(pickUpTime)));// 设置消息过期时间，单位毫秒
            message.getMessageProperties().setMessageId(expressInfo.getWxOrderNo());// 设置消息过期时间，单位毫秒
//            message.getMessageProperties().setDelay(orderNo);// 设置消息过期时间，单位毫秒
            return message;
        });
    }

    /**
     * @param userId   用户id
     * @param totalFee 商品总价格/花费
     */
    public void deductUserMoney(long userId, BigDecimal totalFee) {
        User user = userMapper.selectByPrimaryKey(userId);
        // 如果用户余额为空 或者 余额小于totalFee 提示余额不足 否则对用户的balance进行操作
        if (Objects.isNull(user.getBalance()) || user.getBalance().compareTo(totalFee) < 0) { // 如果用户的余额小于总费用，返回一个负数；
            throw HttpException.badRequest("余额不足");
        } else {
            BigDecimal newBalance = user.getBalance().subtract(totalFee);
            user.setBalance(newBalance);
            userMapper.updateByPrimaryKey(user);
        }
    }

    /**
     * 同一个couponId只该查到一个未使用 未到期的优惠券
     *
     * @param userId   用户id
     * @param couponId 优惠券id
     * @param totalFee 总费用
     */
    public BigDecimal useCoupon(long userId, long couponId, BigDecimal totalFee) {
        Coupon coupon = couponMapper.selectByPrimaryKey(couponId);
        UserCouponExample userCouponExample = new UserCouponExample();
        userCouponExample.createCriteria().andUsedEqualTo(false).andUserIdEqualTo(userId).andCouponIdEqualTo(couponId);
        List<UserCoupon> userCoupons = userCouponMapper.selectByExample(userCouponExample);
        if (Objects.isNull(coupon) || userCoupons.isEmpty()) {
            throw HttpException.badRequest("优惠券不合法");
        }

        userCoupons.forEach(item -> {
            item.setUsed(true);
            item.setUsedTime(new Date());
            item.setUpdatedAt(new Date());
            userCouponMapper.updateByPrimaryKey(item);
        });

        if (Objects.equals(coupon.getDiscountType(), "AMOUNT")) { // "AMOUNT" "PERCENTAGE"
            return totalFee.subtract(coupon.getDiscountAmount());
        } else {
            return totalFee.multiply(coupon.getDiscountPercentage());
        }
    }

    /**
     * 修改购物车中对应商品的状态
     *
     * @param orderInfo { orderId, addressId, List<GoodsInfo> goods }
     * @param userId    用户id
     * @return OrderTable
     */
    private OrderTable createdOrderViaRpc(OrderInfo orderInfo, OrderTable requestOrder, long userId, String orderNo,
                                          BigDecimal totalPrice, BigDecimal finalPrice, Long couponId) {
        OrderTable order = new OrderTable();
        String payType = requestOrder.getPayType();
        PayType.isInMyEnum(payType);
        order.setPayType(payType);
        order.setPickupType(requestOrder.getPickupType());
        order.setWxOrderNo(orderNo);
        order.setUserId(userId);
        order.setPhone(requestOrder.getPhone());
        order.setDiscountId(couponId);
        order.setPickUpTime(Objects.isNull(requestOrder.getPickUpTime()) ? new Date() : requestOrder.getPickUpTime());


        if (PayType.MINIAPP.getName().equals(payType)) { // 微信支付先设置成等待确认付款状态 在回调中设置为已支付
            order.setStatus(OrderStatus.UNPAID.getName());
        } else {
            order.setStatus(OrderStatus.PAID.getName());
        }

        // 外送需要地址
        if (Objects.equals(requestOrder.getPickupType(), OrderPickupStatus.EXPRESS.getValue())) {
            if (Objects.isNull(addressMapper.selectByPrimaryKey(orderInfo.getAddressId()))) {
                throw HttpException.badRequest("地址不存在");
            }
            order.setAddressId(orderInfo.getAddressId());
        }
        //
        order.setTotalPrice(totalPrice);
        order.setFinalAmount(finalPrice);

        ShoppingCart shoppingCart = new ShoppingCart();
        shoppingCart.setStatus(ShoppingCartStatus.PAID.getName());

        HashMap<String, Object> params = new HashMap<>();
        params.put("goodsList", orderInfo.getGoods());
        params.put("userId", userId);
        params.put("status", ShoppingCartStatus.PAID.getName());
        params.put("updatedAt", new Date());
        myShoppingCartMapper.updateByExampleSelectiveWithStatus(params);

        insertOrder(order);
        orderInfo.setOrderId(order.getId());
        myOrderMapper.insertMultipleOrderGoods(orderInfo);
        return order;
    }

    public void deductStockAfterWxPaySuccessByOrderId(Long orderId) {
        log.warn("微信成功付款后减库存 orderId： {}", orderId);
        OrderGoodsExample orderGoodsExample = new OrderGoodsExample();
        orderGoodsExample.createCriteria().andOrderIdEqualTo(orderId);
        List<OrderGoods> orderGoodsList = orderGoodsMapper.selectByExample(orderGoodsExample);
        orderGoodsList.forEach(orderGoods -> {
            GoodsInfo goodsInfo = new GoodsInfo();
            goodsInfo.setId(orderGoods.getGoodsId());
            goodsInfo.setNumber(orderGoods.getNumber().intValue());
            goodsStockMapper.deductStock(goodsInfo); //.deductStock(goodsInfo);
        });
    }

    public OrderResponse updateExpressInformation(OrderTable order, Long userId) {
        OrderTable orderInDatabase = getOrderById(order.getId());
        if (orderInDatabase == null) {
            throw HttpException.notFound("订单未找到，id=" + order.getId());
        }

        Shop shop = shopMapper.selectByPrimaryKey(orderInDatabase.getShopId());
        if (shop == null) {
            throw HttpException.notFound(("店铺未找到"));
        }

        if (!shop.getOwnerUserId().equals(userId)) {
            throw HttpException.forbidden("禁止访问");
        }

        OrderTable copy = new OrderTable();
        copy.setId(order.getId());
        copy.setExpressId(order.getExpressId());
        copy.setExpressCompany(order.getExpressCompany());
        updateOrderByPrimaryKeySelective(copy);
        OrderGoodsVO orderGoodsVO = generateOrderGoodsVO(order.getId());
        return toOrderResponse(orderGoodsVO);
    }

    // 订单没插入 购物车状态不对
    public OrderTable getOrderById(Long orderId) {
        return orderMapper.selectByPrimaryKey(orderId);
    }

    private OrderResponse toOrderResponse(OrderGoodsVO OrderGoodsVO) {
        Map<Long, Goods> idToGoodsMap = getIdToGoodsMap(OrderGoodsVO.getGoods());
        return generateResponse(OrderGoodsVO.getOrder(), idToGoodsMap, OrderGoodsVO.getGoods());
    }

    private Map<Long, Goods> getIdToGoodsMap(List<GoodsInfo> goodsInfo) {
        if (goodsInfo.isEmpty()) {
            return Collections.emptyMap();
        }
        List<Long> goodsId = goodsInfo
                .stream()
                .map(GoodsInfo::getId)
                .collect(toList());
        return goodsService.getGoodsToMapByGoodsIds(goodsId);
    }


    public OrderResponse getOrderById(Long userId, long orderId) {
        return toOrderResponse(doGetOrderById(userId, orderId));
    }

    public OrderGoodsVO doGetOrderById(long userId, long orderId) {
        OrderGoodsVO orderInDatabase = rpcGetOrderById(orderId);
        if (orderInDatabase == null) {
            throw HttpException.notFound("订单未找到: " + orderId);
        }

        Shop shop = shopMapper.selectByPrimaryKey(orderInDatabase.getOrder().getShopId());
        if (shop == null) {
            throw HttpException.notFound("店铺未找到: " + orderInDatabase.getOrder().getShopId());
        }

        if (shop.getOwnerUserId() != userId && orderInDatabase.getOrder().getUserId() != userId) {
            throw HttpException.forbidden("无权访问！");
        }
        return orderInDatabase;
    }

    public OrderGoodsVO rpcGetOrderById(long orderId) {
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

    private OrderResponse generateResponse(OrderTable createOrder, Map<Long, Goods> idToGoodsMap, List<GoodsInfo> goodsInfos) {
        OrderResponse response = new OrderResponse(createOrder);
        Long shopId = new ArrayList<>(idToGoodsMap.values()).get(0).getShopId();
        response.setShop(shopMapper.selectByPrimaryKey(shopId));

        response.setGoods(
                goodsInfos
                        .stream()
                        .map(goods -> toGoodsWithNumber(goods, idToGoodsMap))
                        .collect(Collectors.toList()));
        return response;

    }

    /**
     * 扣减库存
     * 全部扣减成功，返回true，否则返回false。
     *
     * @param orderInfo long orderId; long addressId; List<GoodsInfo> goods;
     */
    @Transactional
    public void deductStock(OrderInfo orderInfo) throws RuntimeException {
        for (GoodsInfo goodsInfo : orderInfo.getGoods()) {
            if (goodsStockMapper.deductStock(goodsInfo) <= 0) {
//                LOGGER.error("扣减库存失败，商品id:" + goodsInfo.getId());
                throw HttpException.gone("扣减库存失败");
            }
        }
    }

    public OrderResponse updateOrderStatus(OrderTable order, long userId) {
        OrderTable orderInDatabase = getOrderById(order.getId());
        if (orderInDatabase == null) {
            throw HttpException.notFound("订单未找到，id=" + order.getId());
        }

        if (userService.checkAdmin(userId) || orderInDatabase.getUserId() == userId) {
            if (Objects.equals(order.getStatus(), OrderStatus.RECEIVED.getName())) {
                OrderTable copy = new OrderTable();
                copy.setId(order.getId());
                copy.setStatus(order.getStatus());
                copy.setUpdatedAt(new Date());
                updateOrderByPrimaryKeySelective(order);
                return toOrderResponse(generateOrderGoodsVO(order.getId()));
            }

            // 取消/退款已支付订单
            if (Objects.equals(order.getStatus(), OrderStatus.CHECK_REFUND.getName())) {
                OrderTable copy = new OrderTable();
                copy.setStatus(OrderStatus.CHECK_REFUND.getName());
                copy.setId(order.getId());
                copy.setUpdatedAt(new Date());
                updateOrderByPrimaryKeySelective(copy);
                return toOrderResponse(generateOrderGoodsVO(order.getId()));
            }

            // 申请退款后取消申请
            if (Objects.equals(order.getStatus(), OrderStatus.PAID.getName()) &&
                    Objects.equals(orderInDatabase.getStatus(), OrderStatus.CHECK_REFUND.getName())) {
                order.setPickUpTime(new Date());
                OrderTable copy = new OrderTable();
                copy.setId(order.getId());
                copy.setStatus(OrderStatus.PAID.getName());
                copy.setUpdatedAt(new Date());
                orderMapper.updateByPrimaryKeySelective(copy);

                return toOrderResponse(generateOrderGoodsVO(order.getId()));
            }
        } else {
            throw HttpException.forbidden("无权访问");
        }
        return toOrderResponse(generateOrderGoodsVO(order.getId()));
    }

    /**
     * @param userId
     * @param orderTradeNo
     * @param orderId
     */
    public void adminConfirmRefund(Long userId, String orderTradeNo, Long orderId, Boolean isWxCallback) {
        OrderTableExample orderTableExample = new OrderTableExample();
        if (Objects.nonNull(orderTradeNo) && !Objects.equals("", orderTradeNo)) {
            orderTableExample.createCriteria().andWxOrderNoEqualTo(orderTradeNo);
        } else {
            orderTableExample.createCriteria().andIdEqualTo(orderId);
        }

        List<OrderTable> orderTables = orderMapper.selectByExample(orderTableExample);
        OrderTable orderInDB;
        if (orderTables.isEmpty()) {
            throw HttpException.badRequest("参数错误");
        } else {
            orderInDB = orderTables.get(0);
        }

        if ((isWxCallback && Objects.isNull(userId)) || userService.checkAdmin(userId)) {
            if (Objects.equals(orderInDB.getPayType(), PayType.MINIAPP.getName())) {
                cancelWXPayOrder(orderInDB);
            } else {
                setOrderRefunded(orderInDB);
            }
        }
    }

    public OrderResponse deleteOrder(long orderId, long userId) {
        OrderTable order = orderMapper.selectByPrimaryKey(orderId);
        if (order == null) {
            throw HttpException.notFound("订单未找到");
        }

        if (order.getUserId() != userId) {
            throw HttpException.forbidden("无权访问");
        }

        List<GoodsInfo> goodsInfo = myOrderMapper.getGoodsInfoOfOrder(orderId);

        order.setStatus(OrderStatus.DELETED.getName());
        order.setUpdatedAt(new Date());
        orderMapper.updateByPrimaryKey(order);

        OrderGoodsVO result = new OrderGoodsVO();
        result.setGoods(goodsInfo);
        result.setOrder(order);
        return toOrderResponse(result);
    }

    private GoodsWithNumber toGoodsWithNumber(GoodsInfo goodsInfo, Map<Long, Goods> idToGoodsMap) {
        GoodsWithNumber result = new GoodsWithNumber(idToGoodsMap.get(goodsInfo.getId()));
        result.setNumber(goodsInfo.getNumber());
        return result;
    }

    private Map<Long, Goods> getIdTOGoodsMap(List<GoodsInfo> goodsInfo) {
        List<Long> goodsId = goodsInfo
                .stream()
                .map(GoodsInfo::getId)
                .collect(Collectors.toList());
        return goodsService.getGoodsToMapByGoodsIds(goodsId);
    }

    public PageResponse<OrderResponse> getOrder(long userId, Integer pageNum, Integer pageSize, OrderStatus status,
                                                Byte pickupType) {
        OrderTableExample countByStatus = new OrderTableExample();
        setStatus(countByStatus, status, pickupType).andUserIdEqualTo(userId);
        int count = (int) orderMapper.countByExample(countByStatus);

//        OrderTableExample pagedOrder = new OrderTableExample();
//        setStatus(pagedOrder, status, pickupType).andUserIdEqualTo(userId);

        Role role = userService.getUserRole(userId);

        List<OrderTable> orders = myOrderMapper.getOrderList(status != null ? status.getName() : null,
                pickupType,
                userId, (pageNum - 1) * pageSize, pageSize, role.getName());
        List<OrderGoods> orderGoods = getOrderGoods(orders);

        int totalPage = count % pageSize == 0 ? count / pageSize : count / pageSize + 1;

        Map<Long, List<OrderGoods>> orderIdToGoodsMap = orderGoods
                .stream()
                .collect(Collectors.groupingBy(OrderGoods::getOrderId, toList()));

        List<OrderGoodsVO> orderGoodsVO = orders.stream()
                .map(order -> toOrderGoodsVO(order, orderIdToGoodsMap))
                .collect(toList());

        List<GoodsInfo> goodsInfo = orderGoodsVO.stream()
                .map(OrderGoodsVO::getGoods)
                .flatMap(List::stream)
                .collect(toList());

        List<OrderResponse> orderResponseList;
        if (goodsInfo.isEmpty()) {
            orderResponseList = new ArrayList<>();
        } else {
            Map<Long, Goods> idToGoodsMap = getIdTOGoodsMap(goodsInfo);
            orderResponseList = orderGoodsVO
                    .stream()
                    .map(order -> generateResponse(order.getOrder(), idToGoodsMap, order.getGoods()))
                    .collect(Collectors.toList());
        }

        return PageResponse.pageData(
                pageNum,
                pageSize,
                totalPage,
                orderResponseList
        );
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

    public OrderTable updateOrderByPrimaryKeySelective(OrderTable order) {
        orderMapper.updateByPrimaryKeySelective(order);
        return order;
    }

    public OrderGoodsVO generateOrderGoodsVO(Long orderId) {
        List<GoodsInfo> goodsInfo = myOrderMapper.getGoodsInfoOfOrder(orderId);
        OrderGoodsVO result = new OrderGoodsVO();
        result.setGoods(goodsInfo);
        result.setOrder(orderMapper.selectByPrimaryKey(orderId));
        return result;
    }

    private OrderGoodsVO toOrderGoodsVO(OrderTable order, Map<Long, List<OrderGoods>> orderIdToGoodsMap) {
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

    private OrderTableExample.Criteria setStatus(OrderTableExample orderExample, OrderStatus status, Byte pickupType) {
        OrderTableExample.Criteria criteria = orderExample.createCriteria();
        if (status == null) {
            criteria.andStatusNotEqualTo(DELETED.getName());
        } else {
            criteria.andStatusEqualTo(status.getName());
        }
        if (pickupType != null) {
            criteria.andPickupTypeEqualTo(pickupType);
        }
        return criteria;
    }

    /**
     * TODO 以后添加外卖方式
     * 暂时只支持到店自取
     *
     * @param order 订单
     */
    private void insertOrder(OrderTable order) {
        verify(() -> order.getUserId() == null, "userId不能为空！");
        verify(() -> order.getTotalPrice() == null || order.getTotalPrice().doubleValue() < 0, "totalPrice非法！");
//        verify(() -> order.getAddressId() == null, "address不能为空！");

        order.setExpressCompany(null);
        order.setExpressId(null);
        order.setCreatedAt(new Date());
        order.setUpdatedAt(new Date());
        String s = generateRandomCode(4);

        while (!isCodesEmpty(s)) {
            s = generateRandomCode(4);
        }
        order.setPickupCode(s);

        orderMapper.insert(order);
    }

    /**
     * 到底自取时需要生成
     *
     * @param code 取货码
     * @return 最近一周是否有未使用的到店自取的验证码
     */
    private boolean isCodesEmpty(String code) {
        OrderTableExample orderTableExample = new OrderTableExample();
        orderTableExample.createCriteria().andPickupCodeEqualTo(code)
                .andPickupTypeEqualTo(OrderPickupStatus.STORE.getValue()).andStatusEqualTo(OrderStatus.PAID.getName());
        List<OrderTable> orderTables = orderMapper.selectByExample(orderTableExample);
        return orderTables.isEmpty();
    }

    private void verify(BooleanSupplier supplier, String message) {
        if (supplier.getAsBoolean()) {
            throw new IllegalArgumentException(message);
        }
    }

    private BigDecimal calculateTotalPrice(OrderInfo orderInfo, Map<Long, Goods> idToGoodsMap) {
        BigDecimal result = BigDecimal.ZERO;
        for (GoodsInfo goodsInfo : orderInfo.getGoods()) {
            Goods goods = idToGoodsMap.get(goodsInfo.getId());
            if (goods == null) {
                throw HttpException.badRequest("id非法" + goodsInfo.getId());
            }
            if (goodsInfo.getNumber() <= 0) {
                throw HttpException.badRequest("number非法" + goodsInfo.getNumber());
            }

            result = result.add(goods.getPrice().multiply(new BigDecimal(goodsInfo.getNumber())));
        }
        return result;
    }

    public static String generateRandomCode(int codeLength) {
        StringBuilder sb = new StringBuilder();
        String letters = "0123456789ABCDEFGHIJKLMNOPQRSTUVWXYZ";
        Random random = new Random();
        for (int i = 0; i < codeLength; i++) {
            int index = random.nextInt(letters.length());
            sb.append(letters.charAt(index));
        }
        return sb.toString();
    }

    public OrderTable getOrderByOutTradeNo(String outTradeNo) {
        OrderTableExample orderTableExample = new OrderTableExample();
        orderTableExample.createCriteria().andWxOrderNoEqualTo(outTradeNo);
        List<OrderTable> orderTables = orderMapper.selectByExample(orderTableExample);
        if (orderTables.isEmpty()) {
            throw HttpException.badRequest("未找到订单");
        }
        return orderTables.get(0);
    }

    private String generateOrderNo() {
        return new Date().getTime() + "_" + generateRandomCode(13);
    }

    /**
     * 发起微信退款请求
     * 管理员同意退款 或者主动退款再调用
     *
     * @param order OrderTable
     */
    public void cancelWXPayOrder(OrderTable order) {
        String payType = order.getPayType();
        // 处理退款
        if (Objects.equals(payType, PayType.MINIAPP.getName())) {
            Refund refund = wechatPayService.createRefund(order.getWxOrderNo(), order.getFinalAmount());
            Status refundStatus = refund.getStatus();
            if (Status.CLOSED.equals(refundStatus)) {
                throw HttpException.forbidden("退款已关闭，无法退款");
            } else if (Status.ABNORMAL.equals(refundStatus)) {
                throw HttpException.badRequest("退款异常");
            } else {
                //SUCCESS：退款成功（退款申请成功） || PROCESSING：退款处理中
                Amount amount = refund.getAmount();
                Long refundNum = amount.getRefund();
                //记录支退款日志
            }
        }
    }

    /**
     * 用户取消订单时 都要先经过老板的同意
     * <p>
     * 在取消已付款订单 接收到微信退款成功的通知后调用
     * 1. 增加库存
     * 2. 退回钱款
     * 3. 退回优惠券
     *
     * @param orderInDB 订单的 no
     */
    public void setOrderRefunded(OrderTable orderInDB) {
        log.warn("进行退款 库存 优惠券操作： {}", orderInDB);
        // 修改订单状态 更新order表 status
        OrderTable order = new OrderTable();
        order.setId(orderInDB.getId());
        order.setStatus(OrderStatus.REFUNDED.getName());
        order.setUpdatedAt(new Date());
        orderMapper.updateByPrimaryKeySelective(order);

        // 增加库存
        OrderGoodsExample orderGoodsExample = new OrderGoodsExample();
        orderGoodsExample.createCriteria().andOrderIdEqualTo(orderInDB.getId());
        List<OrderGoods> orderGoodsList = orderGoodsMapper.selectByExample(orderGoodsExample);
        orderGoodsList.forEach(orderGoods -> goodsStockMapper.addStock(orderGoods.getGoodsId(), orderGoods.getNumber()));

        Long userId = orderInDB.getUserId();
        // 余额支付方式才需要退款操作数据库
        if (Objects.equals(orderInDB.getPayType(), PayType.BALANCE.getName())) {
            // 退款
            BigDecimal finalAmount = orderInDB.getFinalAmount();
            User userById = userService.getUserById(userId);

            User user = new User();
            user.setId(orderInDB.getUserId());
            user.setBalance(userById.getBalance().add(finalAmount));
            userMapper.updateByPrimaryKeySelective(user);
        }

        // 退还优惠券
        if (Objects.nonNull(orderInDB.getDiscountId())) {
            UserCouponExample userCouponExample = new UserCouponExample();
            userCouponExample.createCriteria().andCouponIdEqualTo(orderInDB.getDiscountId())
                    .andUserIdEqualTo(userId).andUsedEqualTo(true);
            // 保证未使用的同一个优惠券只有一个
            List<UserCoupon> userCoupons = userCouponMapper.selectByExample(userCouponExample);
            if (!userCoupons.isEmpty()) {
                UserCoupon userCoupon = userCoupons.get(0);
                userCoupon.setUsed(false);
                userCoupon.setUpdatedAt(new Date());
                userCoupon.setUsedTime(null);
                userCouponMapper.updateByPrimaryKey(userCoupon);
            }
        }
    }

    public long getMilSecond(Date pickupTime) {
        long deliveryTime = pickupTime.getTime();
        // 计算过期时间的毫秒数
        return deliveryTime - System.currentTimeMillis();
    }

    public enum PayType {
        BALANCE,
        MINIAPP;

        public static Boolean isInMyEnum(String value) {
            try {
                PayType myEnum = PayType.valueOf(value);
                return true;
            } catch (IllegalArgumentException e) {
                return false;
            }
        }

        public String getName() {
            return name().toLowerCase();
        }

        public static PayType fromString(String value) {
            for (PayType type : PayType.values()) {
                if (type.getName().equals(value.toLowerCase())) {
                    return type;
                }
            }
            throw new IllegalArgumentException("Invalid PayType: " + value);
        }
    }

    static class OrderResponseWithPayInfo extends OrderResponse {
        PrepayWithRequestPaymentResponse response;

        public OrderResponseWithPayInfo(OrderTable order) {
            super(order);
        }

        public OrderResponseWithPayInfo() {

        }

        public PrepayWithRequestPaymentResponse getPrepay() {
            return response;
        }

        public void setPrepay(PrepayWithRequestPaymentResponse response) {
            this.response = response;
        }
    }
}
