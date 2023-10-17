package com.westee.cake.controller;

import com.westee.cake.service.OrderService;
import com.westee.cake.service.WxExpressService;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import java.util.HashMap;

@RequestMapping("/notify")
@RestController
public class WeChatExpressCallbackController {
    private final WxExpressService expressService;
    private static final Logger log = LoggerFactory.getLogger(OrderService.class);

    @Autowired
    public WeChatExpressCallbackController(WxExpressService expressService) {
        this.expressService = expressService;
    }

    @PostMapping("express")
    public void updateExpressStatus(@RequestBody HashMap<String, String> express) {
        log.info("快递状态回调： {}", express.toString());
        expressService.updateWxExpress(express);
    }
}
