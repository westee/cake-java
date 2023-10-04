package com.westee.cake.controller;

import com.westee.cake.entity.Response;
import com.westee.cake.service.WeChatExpressService;
import com.westee.cake.validator.ExpressSendValidator;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;

import java.util.HashMap;

@RestController
@RequestMapping("/api/v1/")
public class WeChatExpressController {
    private final WeChatExpressService wechatExpressService;

    @Autowired
    public WeChatExpressController(WeChatExpressService wechatExpressService) {
        this.wechatExpressService = wechatExpressService;
    }

    @PostMapping("/express/estimate")
    public Response<HashMap<String, Object>> getExpressEstimateFee(@RequestBody ExpressSendValidator expressInfo) {
        return Response.ok(wechatExpressService.estimateExpressFee(expressInfo));
    }

    @PostMapping("/express")
    public Response<HashMap<String, Object>> createExpress(@RequestBody ExpressSendValidator expressInfo) {
        return null;
    }

    @PostMapping("/express/cancel/{wxOrderId}")
    public Response<Object> cancelExpress(@PathVariable String wxOrderId,
                                          @RequestParam(name = "reason", defaultValue = "1") Integer reason) {
        wechatExpressService.doCancelExpress(wxOrderId, reason);
        return Response.ok("todo");
    }

    @PostMapping("/express/query/{wxOrderId}")
    public Response<Object> queryExpress(@PathVariable String wxOrderId) {
        HashMap<String, Object> stringObjectHashMap = wechatExpressService.doQueryExpress(wxOrderId);
        return Response.ok(stringObjectHashMap);
    }

    enum ServiceTransId {
        DADA("达达快送"),
        SFTC("顺丰同城");

        final String value;

        ServiceTransId(String v) {
            value = v;
        }
    }
}
