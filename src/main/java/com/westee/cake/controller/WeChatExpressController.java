package com.westee.cake.controller;

import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.westee.cake.config.WxPayConfig;
import com.westee.cake.entity.Response;
import com.westee.cake.global.GlobalVariable;
import com.westee.cake.service.WechatExpressService;
import com.westee.cake.util.RequestUtil;
import com.westee.cake.validator.ExpressSendValidator;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;

import java.io.IOException;
import java.util.HashMap;
import java.util.Map;

@RestController
@RequestMapping("/api/v1/")
public class WeChatExpressController {
    private final WxPayConfig wxPayConfig;
    private final WechatExpressService wechatExpressService;

    @Autowired
    public WeChatExpressController(WxPayConfig wxPayConfig, WechatExpressService wechatExpressService) {
        this.wxPayConfig = wxPayConfig;
        this.wechatExpressService = wechatExpressService;
    }

    @PostMapping("/express/estimate")
    public Response<Object> getExpressEstimateFee(@RequestBody ExpressSendValidator expressInfo) throws IOException {
        if (expressInfo.isValid()) {
            HashMap<String, String> headers = new HashMap<>();
            headers.put("Wechatmp-Appid", wxPayConfig.getAPPID());
            headers.put("Wechatmp-TimeStamp", "");
            headers.put("Wechatmp-Signature", "");
            String url = ExpressInterface.CALCULATE_PRICE.getPath() + "?access_token=" + GlobalVariable.INSTANCE.getAccessToken();
            return Response.ok(RequestUtil.doPost(url, expressInfo, headers));
        } else {
            return Response.ok("参数不正确");
        }
    }

    @PostMapping("/express")
    public Response<Object> createExpress(@RequestBody ExpressSendValidator expressInfo) throws IOException {
        System.out.println(expressInfo);
        if (expressInfo.isValid()) {
            String url = ExpressInterface.CALCULATE_PRICE.getPath() + "?access_token=" + GlobalVariable.INSTANCE.getAccessToken();
            HashMap<String, String> headers = new HashMap<>();
            headers.put("Wechatmp-Appid", wxPayConfig.getAPPID());
            headers.put("Wechatmp-TimeStamp", "");
            headers.put("Wechatmp-Signature", "");
            return Response.ok(RequestUtil.doPost(url, expressInfo, headers));
        } else {
            return Response.ok("参数不正确");
        }
    }

    @PostMapping("/express/cancel/{wxOrderId}")
    public Response<Object> cancelExpress(@PathVariable String wxOrderId,
                                          @RequestParam(name = "reason", defaultValue = "1") Integer reason) {
        wechatExpressService.doCancelExpress(wxOrderId, reason);
        return Response.ok("todo");
    }

    @PostMapping("/express/query/{wxOrderId}")
    public Response<Object> queryExpress(@PathVariable String wxOrderId) {
        wechatExpressService.doQueryExpress(wxOrderId);
        return Response.ok("todo");
    }

    private Map<String, Object> getBaseMap(Object o) throws JsonProcessingException {
        HashMap<String, Object> map = new HashMap<>();
        map.put("wx_store_id", "4000000000000238003");
        map.put("user_name", "收件人姓名");
        map.put("user_phone", "15621063761");
        map.put("user_lng", "120.15221");
        map.put("user_lat", "35.93529");
        map.put("user_address", "青岛市黄岛区观海华庭小区");  // 收件用户详细地市

        HashMap<String, String> cargo = new HashMap<>();
        cargo.put("cargo_name", "小月饼");
        cargo.put("cargo_num", "10");
        cargo.put("cargo_type", "1");
        cargo.put("cargo_weight", "10");
        cargo.put("cargo_price", "10");

        map.put("cargo", cargo); //
        return map;
    }

    public static String getStringMap(Map<String, Object> data) throws JsonProcessingException {
        ObjectMapper objectMapper = new ObjectMapper();
        return objectMapper.writeValueAsString(data);
    }

    public enum ExpressInterface {
        CALCULATE_PRICE("preaddorder"),
        CREATE_ORDER("addorder"),
        CANCEL_ORDER("cancelorder"),
        QUERY_ORDER("queryorder");

        private final String path;

        ExpressInterface(String path) {
            this.path = path;
        }

        public String getPath() {
            String BASE_URL = "https://api.weixin.qq.com/cgi-bin/express/intracity/";
//            String BASE_URL = "http://open.s.bingex.com";
            return BASE_URL + path;
        }
    }

    enum ServiceTransId {
        DADA("达达快送"),
        SFTC("顺丰同城");

        String value;

        ServiceTransId(String v) {
            value = v;
        }
    }

    public class EstimateExpressFee {
        Integer errcode;
        ServiceTransId expressCompany;
        Integer distance;  // 米
        Integer est_fee;   // 分
        Integer expected_finished_time; // SFTC 支持此字段 商品预计送达时间
        Integer promise_delivery_time;  // SFTC 支持此字段  配送时长（单位：分钟）
    }
}
