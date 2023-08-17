package com.westee.cake.service;

import com.wechat.pay.java.core.exception.MalformedMessageException;
import com.wechat.pay.java.core.exception.ServiceException;
import com.wechat.pay.java.service.payments.jsapi.model.PrepayWithRequestPaymentResponse;
import com.wechat.pay.java.service.refund.model.Refund;
import com.wechat.pay.java.service.refund.model.Status;
import com.westee.cake.dao.GoodsStockMapper;
import com.westee.cake.dao.MyShoppingCartMapper;
import com.westee.cake.data.GoodsInfo;
import com.westee.cake.data.OrderGoodsVO;
import com.westee.cake.data.OrderInfo;
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
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.math.BigDecimal;
import java.util.ArrayList;
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

    private static final Logger log = LoggerFactory.getLogger(OrderService.class);

    @Autowired
    public OrderService(OrderTableMapper orderMapper, MyOrderMapper myOrderMapper, OrderGoodsMapper orderGoodsMapper,
                        ShopMapper shopMapper, GoodsService goodsService, GoodsStockMapper goodsStockMapper,
                         AddressMapper addressMapper,
                        UserMapper userMapper, UserCouponMapper userCouponMapper, CouponMapper couponMapper,
                        WechatPayService wechatPayService, MyShoppingCartMapper myShoppingCartMapper,
                        UserService userService) {
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
    }

    /**
     * 减库存 使用优惠券 扣款
     * 插入order order_goods
     *
     * @param orderInfo { orderId, addressId, List<GoodsInfo> goods }
     * @param userId    用户id
     * @param couponId  优惠券id
     * @return OrderResponse
     * @throws RuntimeException     支付错误
     */
    @Transactional
    public OrderResponseWithPayInfo createOrder(OrderInfo orderInfo, Long userId, Long couponId, PayType payType,
                                                OrderPickupStatus pickupStatus) throws RuntimeException {

        Map<Long, Goods> idToGoodsMap = getIdTOGoodsMap(orderInfo.getGoods());
        BigDecimal totalPriceBeforeCoupon = calculateTotalPrice(orderInfo, idToGoodsMap);
        User user = userMapper.selectByPrimaryKey(userId);

        String orderNo = generateOrderNo();

        // 使用优惠券
        BigDecimal totalPrice = Objects.isNull(couponId) ? totalPriceBeforeCoupon : useCoupon(userId, couponId, totalPriceBeforeCoupon);

        // 扣款
        if (Objects.equals(payType.getName(), PayType.BALANCE.getName())) {  // 余额支付
            deductUserMoney(userId, totalPrice);
        } else {    //  微信支付
            String detail = idToGoodsMap.values().stream().map(Goods::getName).collect(Collectors.joining(","));
            try {
                // ... 调用接口
                PrepayWithRequestPaymentResponse response = wechatPayService.
                        prepayWithRequestPayment(detail, orderNo, totalPrice, user.getWxOpenId(),
                                OrderType.GOODS);
                OrderResponseWithPayInfo orderResponseWithPayInfo = new OrderResponseWithPayInfo();
                orderResponseWithPayInfo.setPrepay(response);
                return orderResponseWithPayInfo;
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
        }

        // 扣减库存
        deductStock(orderInfo);

        OrderTable createdOrder = createdOrderViaRpc(orderInfo, idToGoodsMap, userId, orderNo, payType, pickupStatus);
        return new OrderResponseWithPayInfo(generateResponse(createdOrder, idToGoodsMap, orderInfo.getGoods()));
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
     * @param userId        用户id
     * @param couponId      优惠券id
     * @param totalFee      总费用
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
     * @param orderInfo     { orderId, addressId, List<GoodsInfo> goods }
     * @param idToGoodsMap  goodsId: goods map
     * @param userId        用户id
     * @return              OrderTable
     */
    private OrderTable createdOrderViaRpc(OrderInfo orderInfo, Map<Long, Goods> idToGoodsMap, long userId,
                                          String orderNo, PayType payType, OrderPickupStatus pickupStatus) {
        OrderTable order = new OrderTable();

        order.setPayType(payType.getName());
        order.setPickupType(pickupStatus.getValue());
        order.setWxOrderNo(orderNo);
        order.setUserId(userId);
        order.setStatus(OrderStatus.PENDING.getName());


        if(PayType.MINIAPP.getName().equals(payType.getName())) { // 微信支付先设置成等待确认付款状态 在回调中设置为已支付
            order.setStatus(OrderStatus.UNPAID.getName());
        } else {
            order.setStatus(OrderStatus.PAID.getName());
        }

        // 外送需要地址
        if (Objects.equals(pickupStatus.getName(), OrderPickupStatus.EXPRESS.getName())) {
            if (Objects.isNull(addressMapper.selectByPrimaryKey(orderInfo.getAddressId()))) {
                throw HttpException.badRequest("地址不存在");
            }
            order.setAddressId(orderInfo.getAddressId());
        }
        order.setTotalPrice(calculateTotalPrice(orderInfo, idToGoodsMap));

        ShoppingCart shoppingCart = new ShoppingCart();
        shoppingCart.setStatus(ShoppingCartStatus.PAID.getName());

        HashMap<String, Object> params = new HashMap<>();
        params.put("goodsList", orderInfo.getGoods());
        params.put("userId", userId);
        params.put("status", ShoppingCartStatus.PAID.getName());
        params.put("updatedAt", new Date());
        myShoppingCartMapper.updateByExampleSelectiveWithStatus(params);

//        orderInfo.getGoods().forEach(goodsInfo -> {
//            long goodsId = goodsInfo.getId();
//            ShoppingCartExample shoppingCartExample = new ShoppingCartExample();
//            shoppingCartExample.createCriteria().andUserIdEqualTo(userId).andGoodsIdEqualTo(goodsId)
//                    .andStatusEqualTo(ShoppingCartStatus.OK.getName()).andIdEqualTo(1L);
//
//            shoppingCartMapper.updateByExampleSelective(shoppingCart, shoppingCartExample);
//        });

        insertOrder(order);
        orderInfo.setOrderId(order.getId());
        myOrderMapper.insertMultipleOrderGoods(orderInfo);
        return order;
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
        return toOrderResponse(updateOrder(copy));
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

        if (orderInDatabase.getUserId() != userId) {
            throw HttpException.forbidden("无权访问");
        }

        // 取消已支付订单有两种情况
        if (Objects.equals(orderInDatabase.getStatus(), OrderStatus.PAID.getName()) &&
                Objects.equals(order.getStatus(), OrderStatus.CANCEL.getName())) {
            // 微信支付
            cancelOrder(orderInDatabase);
        } else {
            OrderTable copy = new OrderTable();
            copy.setStatus(order.getStatus());
            copy.setId(order.getId());
            copy.setUpdatedAt(new Date());
            return toOrderResponse(updateOrder(order));
        }
        return null;
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

    public PageResponse<OrderResponse> getOrder(long userId, Integer pageNum, Integer pageSize, OrderStatus status) {
        OrderTableExample countByStatus = new OrderTableExample();
        setStatus(countByStatus, status).andUserIdEqualTo(userId);
        int count = (int) orderMapper.countByExample(countByStatus);

        OrderTableExample pagedOrder = new OrderTableExample();
        setStatus(pagedOrder, status).andUserIdEqualTo(userId);

        Role role = userService.getUserRole(userId);

        List<OrderTable> orders = myOrderMapper.getOrderList(status != null ? status.getName() : null,
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

    public OrderGoodsVO updateOrder(OrderTable order) {
        orderMapper.updateByPrimaryKeySelective(order);

        List<GoodsInfo> goodsInfo = myOrderMapper.getGoodsInfoOfOrder(order.getId());
        OrderGoodsVO result = new OrderGoodsVO();
        result.setGoods(goodsInfo);
        result.setOrder(orderMapper.selectByPrimaryKey(order.getId()));
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

    private OrderTableExample.Criteria setStatus(OrderTableExample orderExample, OrderStatus status) {
        if (status == null) {
            return orderExample.createCriteria().andStatusNotEqualTo(DELETED.getName());
        } else {
            return orderExample.createCriteria().andStatusEqualTo(status.getName());
        }
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

    public OrderTable getOrderByOutTradeNo(String outTradeNo) throws Exception {
        OrderTableExample orderTableExample = new OrderTableExample();
        orderTableExample.createCriteria().andWxOrderNoEqualTo(outTradeNo);
        List<OrderTable> orderTables = orderMapper.selectByExample(orderTableExample);
        if(orderTables.isEmpty()) {
            throw new Exception("未找到订单");
        }
        return orderTables.get(0);
    }

    private String generateOrderNo() {
        return new Date().getTime() + "_" + generateRandomCode(13);
    }

    /**
     * @param order OrderTable
     */
    public void cancelOrder(OrderTable order) {
        String payType = order.getPayType();
        // 处理退款
        if (Objects.equals(payType, PayType.MINIAPP.getName())) {
            Refund refund = WechatPayService.createRefund(order.getWxOrderNo());
            Status refundStatus = refund.getStatus();
            if (Status.CLOSED.equals(refundStatus)) {
                throw HttpException.forbidden("退款已关闭，无法退款");
            } else if (Status.ABNORMAL.equals(refundStatus)) {
                throw HttpException.badRequest("退款异常");
            } else {
                //SUCCESS：退款成功（退款申请成功） || PROCESSING：退款处理中
                //记录支退款日志
            }
        } else {
            // 更新user表  balance
            User user = userMapper.selectByPrimaryKey(order.getUserId());
            BigDecimal add = user.getBalance().add(order.getTotalPrice());
            User userSelective = new User();
            userSelective.setId(user.getId());
            userSelective.setBalance(add);
            userSelective.setUpdatedAt(new Date());
            userMapper.updateByPrimaryKeySelective(userSelective);

            // 更新order表 status
            OrderTable copy = new OrderTable();
            copy.setStatus(order.getStatus());
            copy.setId(order.getId());
            copy.setUpdatedAt(new Date());
            orderMapper.updateByPrimaryKeySelective(copy);
        }
    }

    /**
     * 在取消订单 接收到微信退款成功的通知后调用
     *
     * @param orderTradeNo 订单的 no
     */
    public void setOrderCancel(String orderTradeNo) {
        OrderTableExample orderTableExample = new OrderTableExample();
        orderTableExample.createCriteria().andWxOrderNoEqualTo(orderTradeNo);
        List<OrderTable> orderTables = orderMapper.selectByExample(orderTableExample);
        OrderTable orderTable = orderTables.get(0);

        OrderTable order = new OrderTable();
        order.setId(orderTable.getId());
        order.setStatus(OrderStatus.CANCEL.name());
        order.setUpdatedAt(new Date());
        orderMapper.updateByPrimaryKeySelective(order);
    }

    public enum PayType {
        BALANCE,
        MINIAPP;

        public Boolean isInMyEnum(String value) {
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
