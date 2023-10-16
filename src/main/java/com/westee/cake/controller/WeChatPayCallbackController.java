package com.westee.cake.controller;

import com.wechat.pay.java.service.refund.model.RefundNotification;
import com.wechat.pay.java.service.refund.model.Status;
import com.westee.cake.entity.OrderStatus;
import com.westee.cake.generate.OrderTable;
import com.westee.cake.service.OrderService;
import com.westee.cake.service.WeChatPayCallBackService;
import com.westee.cake.util.WxPayUtil;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import org.apache.commons.lang3.exception.ExceptionUtils;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import java.io.BufferedReader;
import java.io.IOException;
import java.util.HashMap;
import java.util.Map;
import java.util.Objects;

@RestController
@RequestMapping("/notify")
public class WeChatPayCallbackController {
    private final OrderService orderService;
    private final WeChatPayCallBackService weChatPayCallBackService;
    private final WxPayUtil wxPayUtil;


    private static final Logger log = LoggerFactory.getLogger(WeChatPayCallbackController.class);

    @Autowired
    public WeChatPayCallbackController(OrderService orderService,WeChatPayCallBackService weChatPayCallBackService,
                                       WxPayUtil wxPayUtil) {
        this.orderService = orderService;
        this.weChatPayCallBackService = weChatPayCallBackService;
        this.wxPayUtil = wxPayUtil;
    }

    /**
     * 下单商品 || 充值
     */
    @PostMapping(value = "/pay")
    public HashMap<String, String> callBackWeiXinPay(HttpServletRequest request, HttpServletResponse response,
                                                     @RequestBody Map<String, Object> body) {
        log.warn("进入回调");
        log.warn("回调参数body {}", body);
        log.warn("回调参数request {}", request);
        try {
            return weChatPayCallBackService.dealWithWeixinPay(request, response, body);
        } catch (Exception e) {
            log.error("回调解析错误： {}", e.getMessage());
            response.setStatus(500);
            return generateResponseToWxPayCallback("FAIL", "失败");
        }
    }

    @PostMapping("/refund")
    public HashMap<String, String> callback(HttpServletRequest request, HttpServletResponse response,
                                            @RequestBody Map<String, Object> body) {
        log.warn("退款通知执行");
        try {
            // 验签、解密并转换成 Transaction
            RefundNotification refundNotification = wxPayUtil.getNotificationParser(request, body, RefundNotification.class);

            String orderTradeNo = refundNotification.getOutTradeNo();
            Status refundStatus = refundNotification.getRefundStatus();
            OrderTable order = orderService.getOrderByOutTradeNo(orderTradeNo);

            if ("SUCCESS".equals(refundStatus.toString())) {
                log.warn("更新退款记录：已退款 {}", orderTradeNo);
                //退款状态
                if (Objects.equals(order.getStatus(), OrderStatus.PAID.getName()) ||
                        Objects.equals(order.getStatus(), OrderStatus.CHECK_REFUND.getName())) {
                    orderService.setOrderRefunded(order);
                }
            }

            //成功应答
            response.setStatus(200);
            return generateResponseToWxPayCallback("SUCCESS", "");
        } catch (Exception e) {
            log.error(ExceptionUtils.getStackTrace(e));
            //失败应答
            response.setStatus(500);
            return generateResponseToWxPayCallback("ERROR", "失败");// JSONObject.toJSONString(map);
        }
    }

    public HashMap<String, String> generateResponseToWxPayCallback(String code, String message) {
        HashMap<String, String> jsonResponse = new HashMap<>();
        jsonResponse.put("code", code);
        jsonResponse.put("message", message);
        return jsonResponse;
    }

    public enum WxPayConstant {
        SUCCESS(),   // "支付成功"
        REFUND(),   //   "转入退款"
        NOTPAY(), // "未支付"
        CLOSED(), // "已关闭"
        REVOKED(), // "已撤销（付款码支付）"
        USERPAYING(),  // "用户支付中（付款码支付）"
        PAYERROR(), // "支付失败(其他原因，如银行返回失败)")
    }

    public enum RefundStatusEnum {
        SUCCESS(),   //  退款成功
        CLOSED(),    //  退款关闭
        ABNORMAL(),  //  退款异常
    }

    public String readData(HttpServletRequest request) {
        BufferedReader br = null;
        try {
            StringBuilder result = new StringBuilder();
            br = request.getReader();
            for (String line; (line = br.readLine()) != null; ) {
                if (result.length() > 0) {
                    result.append("\n");
                }
                result.append(line);
            }
            return result.toString();
        } catch (IOException e) {
            throw new RuntimeException(e);
        } finally {
            if (br != null) {
                try {
                    br.close();
                } catch (IOException e) {
                    e.printStackTrace();
                }
            }
        }
    }
}
