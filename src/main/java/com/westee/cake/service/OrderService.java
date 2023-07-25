package com.westee.cake.service;

import com.westee.cake.dao.GoodsStockMapper;
import com.westee.cake.data.DataStatus;
import com.westee.cake.data.GoodsInfo;
import com.westee.cake.data.OrderGoodsVO;
import com.westee.cake.data.OrderInfo;
import com.westee.cake.entity.GoodsWithNumber;
import com.westee.cake.entity.OrderPickupStatus;
import com.westee.cake.entity.OrderResponse;
import com.westee.cake.entity.PageResponse;
import com.westee.cake.entity.ShoppingCartStatus;
import com.westee.cake.exceptions.HttpException;
import com.westee.cake.generate.*;
import com.westee.cake.mapper.MyOrderMapper;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.math.BigDecimal;
import java.util.ArrayList;
import java.util.Collections;
import java.util.Date;
import java.util.List;
import java.util.Map;
import java.util.Objects;
import java.util.Random;
import java.util.function.BooleanSupplier;
import java.util.stream.Collectors;

import static com.westee.cake.data.DataStatus.PAID;
import static com.westee.cake.data.DataStatus.PENDING;
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

    private final ShoppingCartMapper shoppingCartMapper;

    private final AddressMapper addressMapper;

    private final UserMapper userMapper;

    private final UserCouponMapper userCouponMapper;

    private final CouponMapper couponMapper;

    @Autowired
    public OrderService(OrderTableMapper orderMapper, MyOrderMapper myOrderMapper, OrderGoodsMapper orderGoodsMapper,
                        ShopMapper shopMapper, GoodsService goodsService, GoodsStockMapper goodsStockMapper,
                        ShoppingCartMapper shoppingCartMapper, AddressMapper addressMapper,
                        UserMapper userMapper, UserCouponMapper userCouponMapper, CouponMapper couponMapper) {
        this.shopMapper = shopMapper;
        this.shoppingCartMapper = shoppingCartMapper;
        this.orderMapper = orderMapper;
        this.goodsService = goodsService;
        this.myOrderMapper = myOrderMapper;
        this.goodsStockMapper = goodsStockMapper;
        this.orderGoodsMapper = orderGoodsMapper;
        this.addressMapper = addressMapper;
        this.userMapper = userMapper;
        this.userCouponMapper = userCouponMapper;
        this.couponMapper = couponMapper;
    }

    /**
     * 减库存 使用优惠券 扣款
     * 插入order order_goods
     *
     * @param orderInfo
     * @param userId
     * @param couponId
     * @return
     * @throws RuntimeException
     */
    @Transactional
    public OrderResponse createOrder(OrderInfo orderInfo, Long userId, long couponId) throws RuntimeException {
        Map<Long, Goods> idToGoodsMap = getIdTOGoodsMap(orderInfo.getGoods());
        BigDecimal totalPriceBeforeCoupon = calculateTotalPrice(orderInfo, idToGoodsMap);
        // 使用优惠券
        BigDecimal totalPrice = useCoupon(userId, couponId, totalPriceBeforeCoupon);

        // 扣款
        deductUserMoney(userId, totalPrice);

        // 扣减库存
        deductStock(orderInfo);

        OrderTable createOrder = createdOrderViaRpc(orderInfo, idToGoodsMap, userId);

        return generateResponse(createOrder, idToGoodsMap, orderInfo.getGoods());
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
     * @param userId
     * @param couponId
     * @param totalFee
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

        if(Objects.equals(coupon.getDiscountType(), "AMOUNT")) { // "AMOUNT" "PERCENTAGE"
            return totalFee.subtract(coupon.getDiscountAmount());
        } else {
            return totalFee.multiply(coupon.getDiscountPercentage());
        }
    }

    /**
     * 修改购物车中对应商品的状态
     *
     * @param orderInfo
     * @param idToGoodsMap
     * @param userId
     * @return
     */
    private OrderTable createdOrderViaRpc(OrderInfo orderInfo, Map<Long, Goods> idToGoodsMap, long userId) {
        OrderTable order = new OrderTable();

        order.setUserId(userId);
        order.setStatus(DataStatus.PENDING.getName());
        if (Objects.isNull(addressMapper.selectByPrimaryKey(orderInfo.getAddressId()))) {
            throw HttpException.badRequest("地址不存在");
        }
        order.setAddressId(orderInfo.getAddressId());
        order.setTotalPrice(calculateTotalPrice(orderInfo, idToGoodsMap));

        ShoppingCartExample shoppingCartExample = new ShoppingCartExample();
        ShoppingCart shoppingCart = new ShoppingCart();
        shoppingCart.setStatus(ShoppingCartStatus.PAID.getName());
        orderInfo.getGoods().forEach(goodsInfo -> {
            long goodsId = goodsInfo.getId();
            shoppingCartExample.createCriteria().andUserIdEqualTo(userId).andGoodsIdEqualTo(goodsId);
            shoppingCartMapper.updateByExampleSelective(shoppingCart, shoppingCartExample);
        });
        return rpcCreateOrder(orderInfo, order);
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

    public OrderTable getOrderById(Long orderId) {
        return orderMapper.selectByPrimaryKey(orderId);
    }

    /**
     * 创建订单
     *
     * @param orderInfo
     * @param order
     * @return
     */
    public OrderTable rpcCreateOrder(OrderInfo orderInfo, OrderTable order) {
        insertOrder(order);
        orderInfo.setOrderId(order.getId());
        myOrderMapper.insertMultipleOrderGoods(orderInfo);
        return order;
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
     * @param orderInfo
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

        OrderTable copy = new OrderTable();
        copy.setStatus(order.getStatus());
        copy.setId(order.getId());
        copy.setUpdatedAt(new Date());
        return toOrderResponse(updateOrder(order));
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

        order.setStatus(DELETED.getName());
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

    public PageResponse<OrderResponse> getOrder(long userId, Integer pageNum, Integer pageSize, DataStatus status) {
        OrderTableExample countByStatus = new OrderTableExample();
        setStatus(countByStatus, status).andUserIdEqualTo(userId);
        int count = (int) orderMapper.countByExample(countByStatus);

        OrderTableExample pagedOrder = new OrderTableExample();
        setStatus(pagedOrder, status).andUserIdEqualTo(userId);

        List<OrderTable> orders = myOrderMapper.getOrderList(status != null ? status.getName() : null, userId, (pageNum - 1) * pageSize, pageSize);
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

    private OrderTableExample.Criteria setStatus(OrderTableExample orderExample, DataStatus status) {
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
        order.setStatus(PENDING.getName());

        verify(() -> order.getUserId() == null, "userId不能为空！");
        verify(() -> order.getTotalPrice() == null || order.getTotalPrice().doubleValue() < 0, "totalPrice非法！");
        verify(() -> order.getAddressId() == null, "address不能为空！");

        order.setExpressCompany(null);
        order.setExpressId(null);
        order.setCreatedAt(new Date());
        order.setUpdatedAt(new Date());
        String s = generateRandomCode();

        while (!isCodesEmpty(s)) {
            s = generateRandomCode();
        }
        order.setPickupCode(s);
        order.setPickupType(OrderPickupStatus.STORE.getValue());

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
                .andPickupTypeEqualTo(OrderPickupStatus.STORE.getValue()).andStatusEqualTo(PAID.getName());
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

    private static String generateRandomCode() {
        StringBuilder sb = new StringBuilder();
        String letters = "0123456789ABCDEFGHIJKLMNOPQRSTUVWXYZ";
        Random random = new Random();
        for (int i = 0; i < 4; i++) {
            int index = random.nextInt(letters.length());
            sb.append(letters.charAt(index));
        }
        return sb.toString();
    }

    public OrderTable getOrderByOrderNo(String outTradeNo) {
        return null;
    }
}
