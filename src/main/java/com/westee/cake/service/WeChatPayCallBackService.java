package com.westee.cake.service;

import com.wechat.pay.java.service.payments.model.Transaction;
import com.westee.cake.controller.WeChatPayCallbackController;
import com.westee.cake.data.ChargeStatus;
import com.westee.cake.entity.OrderStatus;
import com.westee.cake.entity.OrderType;
import com.westee.cake.generate.Charge;
import com.westee.cake.generate.OrderTable;
import com.westee.cake.generate.User;
import com.westee.cake.util.WxPayUtil;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.web.bind.annotation.RequestBody;

import java.math.BigDecimal;
import java.util.HashMap;
import java.util.Map;
import java.util.Objects;

@Service
public class WeChatPayCallBackService {
    private final OrderService orderService;
    private final ChargeService chargeService;
    private final UserService userService;
    private final WxPayUtil wxPayUtil;
    private final WeChatSubscribeService weChatSubscribeService;

    private static final Logger log = LoggerFactory.getLogger(WeChatPayCallbackController.class);

    @Autowired
    public WeChatPayCallBackService(OrderService orderService, ChargeService chargeService, UserService userService,
                                    WxPayUtil wxPayUtil, WeChatSubscribeService weChatSubscribeService) {
        this.orderService = orderService;
        this.chargeService = chargeService;
        this.userService = userService;
        this.wxPayUtil = wxPayUtil;
        this.weChatSubscribeService = weChatSubscribeService;
    }

    public HashMap<String, String> dealWithWeixinPay(HttpServletRequest request, HttpServletResponse response,
                                                     @RequestBody Map<String, Object> body) throws Exception {
        Transaction appCallBackVo = wxPayUtil.getNotificationParser(request, body, Transaction.class);
        log.warn("wx回调参数：{}", appCallBackVo);
        String outTradeNo = appCallBackVo.getOutTradeNo();
        String[] split = appCallBackVo.getAttach().split("@"); // @后面是商品名
        String orderType = split[0];
        String sendSubscribeName = split[1];

        if (Objects.equals(orderType, OrderType.GOODS.getName())) {
            return goodsOrder(response, outTradeNo, sendSubscribeName, appCallBackVo);
        } else {
            return chargeOrder(response, outTradeNo, sendSubscribeName, appCallBackVo);
        }
    }

    public HashMap<String, String> chargeOrder(HttpServletResponse response, String outTradeNo, String sendSubscribeName, Transaction appCallBackVo) {
        Charge chargeByOutTradeNo = chargeService.getChargeByOutTradeNo(outTradeNo);
        //  查询我方订单
        if (Objects.isNull(chargeByOutTradeNo)) {
            response.setStatus(500);
            log.warn("充值订单 订单不存在 outTradeNo: {}", outTradeNo);
            return generateResponseToWxPayCallback("FAIL", "订单不存在");
        }
        // 重复回调
        if (chargeByOutTradeNo.getStatus().equals(ChargeStatus.OK.getName())) {
            log.warn("充值订单 重复确认 outTradeNo：{}", outTradeNo);
            return generateResponseToWxPayCallback("SUCCESS", "成功-重复");
        }

        // 金额验证
        if (new BigDecimal(appCallBackVo.getAmount().getTotal()).compareTo(chargeByOutTradeNo.getAmount().multiply(BigDecimal.valueOf(100))) != 0) {
            response.setStatus(500);
            log.warn("充值订单 金额不正确 总额（元）：{}", chargeByOutTradeNo.getAmount());
            return generateResponseToWxPayCallback("FAIL", "金额不正确");
        }
        if (appCallBackVo.getTradeState().name().equals(WeChatPayCallbackController.WxPayConstant.SUCCESS.name())) { // Trade_state是大写
            // 更新订单状态
            chargeByOutTradeNo.setStatus(ChargeStatus.OK.getName());
            User userById = userService.getUserById(chargeByOutTradeNo.getUserId());
            weChatSubscribeService.getParamsAndSendPlaceOrderSubscribe(userById.getWxOpenId(), sendSubscribeName, chargeByOutTradeNo.getAmount());
            log.warn("充值成功:{}", chargeByOutTradeNo);
        } else {
            chargeByOutTradeNo.setStatus(ChargeStatus.OK.getName());
            log.warn("充值失败:{}", chargeByOutTradeNo);
        }
        chargeService.updateCharge(chargeByOutTradeNo);
        userService.updateUserBalance(chargeByOutTradeNo);
        return generateResponseToWxPayCallback("SUCCESS", "成功");
    }

    public HashMap<String, String> goodsOrder(HttpServletResponse response, String outTradeNo, String subscribe, Transaction appCallBackVo) {
        OrderTable orderByOrderNo = orderService.getOrderByOutTradeNo(outTradeNo);
        //  查询我方订单
        if (Objects.isNull(orderByOrderNo)) {
            response.setStatus(500);
            log.warn("商品订单 订单不存在 outTradeNo：{}", outTradeNo);
            return generateResponseToWxPayCallback("FAIL", "订单不存在");
        }
        // 重复回调
        if (orderByOrderNo.getStatus().equals(OrderStatus.PAID.getName())) {
            log.warn("商品订单 重复确认 outTradeNo：{}", outTradeNo);
            return generateResponseToWxPayCallback("SUCCESS", "成功-重复");
        }

        // 金额验证
        if (new BigDecimal(appCallBackVo.getAmount().getTotal()).compareTo(orderByOrderNo.getTotalPrice().multiply(BigDecimal.valueOf(100))) != 0) {
            response.setStatus(500);
            log.warn("商品订单 金额不正确 总额（元）：{}", orderByOrderNo.getTotalPrice());
            return generateResponseToWxPayCallback("FAIL", "金额不正确");
        }
        OrderTable orderSelective = new OrderTable();
        orderSelective.setId(orderByOrderNo.getId());
        if (appCallBackVo.getTradeState().name().equals(WeChatPayCallbackController.WxPayConstant.SUCCESS.name())) { // Trade_state是大写
            log.error("appCallBackVo.getTradeState().name() {}", appCallBackVo.getTradeState().name().equals(WeChatPayCallbackController.WxPayConstant.SUCCESS.name()));
            orderService.deductStockAfterWxPaySuccessByOrderId(orderByOrderNo, subscribe);
            // 更新订单状态
            orderSelective.setStatus(OrderStatus.PAID.getName());
        } else {
            orderSelective.setStatus(OrderStatus.FAIL.getName());
        }
        if (Objects.nonNull(orderSelective.getStatus())) {
            orderService.updateOrderByPrimaryKeySelective(orderSelective);
        }
        return generateResponseToWxPayCallback("SUCCESS", "成功");
    }

    public HashMap<String, String> generateResponseToWxPayCallback(String code, String message) {
        HashMap<String, String> jsonResponse = new HashMap<>();
        jsonResponse.put("code", code);
        jsonResponse.put("message", message);
        return jsonResponse;
    }
}
