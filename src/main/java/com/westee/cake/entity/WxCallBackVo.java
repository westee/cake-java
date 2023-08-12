package com.westee.cake.entity;

import lombok.Data;

import java.util.Date;
import java.util.Map;

@Data
public class WxCallBackVo {
    private String mchid;
    private String appid;
    //    订单号
    private String out_trade_no;
    //    第三方订单号
    private String transaction_id;
    private String trade_type;
    private String trade_state;
    private String trade_state_desc;
    private String bank_type;
    private String attach;
    private Date success_time;
    /**
     * 对方openid payer={openid=ssssss}
     */
    private Map<String, String> payer;
    /**
     * 支付金额详情  amount={total=1.0, payer_total=1.0, currency=CNY, payer_currency=CNY}
     */
    private Map<String, String> amount;
}