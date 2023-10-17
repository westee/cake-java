package com.westee.cake.service;

import com.wechat.pay.java.core.Config;
import com.wechat.pay.java.core.RSAAutoCertificateConfig;
import com.wechat.pay.java.service.payments.jsapi.JsapiServiceExtension;
import com.wechat.pay.java.service.payments.jsapi.model.Amount;
import com.wechat.pay.java.service.payments.jsapi.model.CloseOrderRequest;
import com.wechat.pay.java.service.payments.jsapi.model.Payer;
import com.wechat.pay.java.service.payments.jsapi.model.PrepayRequest;
import com.wechat.pay.java.service.payments.jsapi.model.PrepayWithRequestPaymentResponse;
import com.wechat.pay.java.service.payments.jsapi.model.QueryOrderByIdRequest;
import com.wechat.pay.java.service.payments.jsapi.model.QueryOrderByOutTradeNoRequest;
import com.wechat.pay.java.service.payments.model.Transaction;
import com.wechat.pay.java.service.refund.RefundService;
import com.wechat.pay.java.service.refund.model.AmountReq;
import com.wechat.pay.java.service.refund.model.CreateRequest;
import com.wechat.pay.java.service.refund.model.QueryByOutRefundNoRequest;
import com.wechat.pay.java.service.refund.model.Refund;
import com.westee.cake.config.WxPayConfig;
import com.westee.cake.entity.OrderType;
import com.westee.cake.util.WxPayUtil;
import jakarta.annotation.PostConstruct;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.math.BigDecimal;

@Service
public class WechatPayService {
    public static JsapiServiceExtension jsapiServiceExtension;
    private static final Logger log = LoggerFactory.getLogger(WechatPayService.class);
    public static RefundService refundService;

    private final WxPayConfig wxPayConfig;

    /**
     * 人民币
     */
    public static final String CNY = "CNY";

    @Autowired
    public WechatPayService(WxPayConfig wxPayConfig) throws Exception {
        this.wxPayConfig = wxPayConfig;

        this.init();
    }

    @PostConstruct
    public void init() throws Exception {
        String privateKey = WxPayUtil.loadKeyByResource("wxpay/apiclient_key.pem");
        // 初始化商户配置
        Config config =
                new RSAAutoCertificateConfig.Builder()
                        .merchantId(wxPayConfig.getMCHID())
                        // 使用 com.wechat.pay.java.core.util 中的函数从本地文件中加载商户私钥，商户私钥会用来生成请求的签名
                        .privateKey(privateKey)
                        .merchantSerialNumber(wxPayConfig.getSERIAL_NO())
                        .apiV3Key(wxPayConfig.getApiV3key())
                        .build();
        // 初始化服务
        jsapiServiceExtension =
                new JsapiServiceExtension.Builder()
                        .config(config)
                        .signType("RSA") // 不填默认为RSA
                        .build();
        refundService = new RefundService.Builder().config(config).build();
    }

    /**
     * 关闭订单
     */
    public void closeOrder(String outTradeNo) {
        CloseOrderRequest request = new CloseOrderRequest();
        // 调用request.setXxx(val)设置所需参数，具体参数可见Request定义
        request.setMchid(wxPayConfig.getMCHID());
        request.setOutTradeNo(outTradeNo);
        // 调用接口
        jsapiServiceExtension.closeOrder(request);
    }

    /**
     * JSAPI支付下单，并返回JSAPI调起支付数据
     *
     * @param stringGoodsNamesOrChargeInfo 订单描述
     * @param outTradeNo                   id
     * @param money                        金额
     * @param openId                       用户openid
     * @param type                         购买商品 goods、充值 charge
     * @return PrepayWithRequestPaymentResponse 支付信息
     */
    public PrepayWithRequestPaymentResponse prepayWithRequestPayment(String stringGoodsNamesOrChargeInfo, String outTradeNo,
                                                                     BigDecimal money, String openId, OrderType type) {
        PrepayRequest request = new PrepayRequest();

        Amount amount = new Amount();
        amount.setTotal(decimalToInt(money));
        amount.setCurrency(CNY);

        // 调用request.setXxx(val)设置所需参数，具体参数可见Request定义
        log.warn("request outTradeNo: {}", outTradeNo);
        log.warn("request NotifyUrl: {}", wxPayConfig.getPayNotifyUrl());
        request.setNotifyUrl(wxPayConfig.getPayNotifyUrl());
        request.setAmount(amount);
        request.setAttach(type.getName() + "@" + stringGoodsNamesOrChargeInfo);  //  type@goodsNamesString  回调成功后使用,@分隔
        request.setAppid(wxPayConfig.getAPPID());
        request.setMchid(wxPayConfig.getMCHID());
        request.setOutTradeNo(outTradeNo);
        request.setDescription(stringGoodsNamesOrChargeInfo);

        Payer payer = new Payer();
        payer.setOpenid(openId);
        request.setPayer(payer);
        // 调用接口
        return jsapiServiceExtension.prepayWithRequestPayment(request);
    }

    /**
     * 微信支付订单号查询订单
     */
    public Transaction queryOrderById(String transactionId) {

        QueryOrderByIdRequest request = new QueryOrderByIdRequest();
        // 调用request.setXxx(val)设置所需参数，具体参数可见Request定义
        request.setMchid(wxPayConfig.getMCHID());
        request.setTransactionId(transactionId);
        // 调用接口
        return jsapiServiceExtension.queryOrderById(request);
    }

    /**
     * 商户订单号查询订单
     */
    public Transaction queryOrderByOutTradeNo(String tradeNo) {

        QueryOrderByOutTradeNoRequest request = new QueryOrderByOutTradeNoRequest();
        // 调用request.setXxx(val)设置所需参数，具体参数可见Request定义
        request.setMchid(wxPayConfig.getMCHID());
        request.setOutTradeNo(tradeNo);
        // 调用接口
        return jsapiServiceExtension.queryOrderByOutTradeNo(request);
    }

    /**
     * 退款申请
     **/
    public Refund createRefund(String outTradeNo, BigDecimal finalAmount) {
        CreateRequest request = new CreateRequest();
        // 调用request.setXxx(val)设置所需参数，具体参数可见Request定义
        request.setOutTradeNo(outTradeNo);
        request.setOutRefundNo("REFUND_" + outTradeNo);
        log.warn("request outTradeNo refund: {}", outTradeNo);
        log.warn("request NotifyUrl refund: {}", wxPayConfig.getRefundNotifyUrl());

        AmountReq amount = new AmountReq();
        amount.setTotal(decimalToLong(finalAmount));
        amount.setRefund(decimalToLong(finalAmount));
        amount.setCurrency(CNY);

        request.setAmount(amount);
        request.setNotifyUrl(wxPayConfig.getRefundNotifyUrl());
        // 调用接口
        return refundService.create(request);
    }

    /**
     * 查询单笔退款（通过商户退款单号）
     */
    public static Refund queryByOutRefundNo(String r) {

        QueryByOutRefundNoRequest request = new QueryByOutRefundNoRequest();
        // 调用request.setXxx(val)设置所需参数，具体参数可见Request定义
        request.setOutRefundNo(r);
        // 调用接口
        return refundService.queryByOutRefundNo(request);
    }

    private static int decimalToInt(BigDecimal money) {
        return money.multiply(BigDecimal.valueOf(100)).intValue();
    }

    private static long decimalToLong(BigDecimal money) {
        return money.multiply(BigDecimal.valueOf(100)).longValue();
    }

}
