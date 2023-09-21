package com.westee.cake.controller;

import com.westee.cake.service.WxExpressService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import java.util.HashMap;

@RequestMapping("/notify")
@RestController
public class WeChatExpressCallbackController {
    WxExpressService expressService;

    @Autowired
    public WeChatExpressCallbackController(WxExpressService expressService) {
        this.expressService = expressService;
    }

    @PostMapping("express")
    public void updateExpressStatus(@RequestBody HashMap<String, String> express) {
        expressService.updateWxExpress(express);
    }
}
