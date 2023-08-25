package com.westee.cake.controller;

import com.wechat.pay.java.service.payments.jsapi.model.PrepayWithRequestPaymentResponse;
import com.westee.cake.entity.PageResponse;
import com.westee.cake.entity.Response;
import com.westee.cake.exceptions.HttpException;
import com.westee.cake.generate.Charge;
import com.westee.cake.service.ChargeService;
import com.westee.cake.service.OrderService;
import com.westee.cake.service.UserService;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestHeader;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;

@RestController
@RequestMapping("/api/v1/")
public class ChargeController {
    private final ChargeService chargeService;
    private final UserService userService;
    private static final Logger log = LoggerFactory.getLogger(OrderService.class);

    @Autowired
    public ChargeController(ChargeService chargeService, UserService userService) {
        this.chargeService = chargeService;
        this.userService = userService;
    }

    @GetMapping("charge")
    public PageResponse<Charge> getChargeHistory(@RequestParam(name = "pageNum", required = false, defaultValue = "1") Integer pageNum,
                                                 @RequestParam(name = "pageSize", required = false, defaultValue = "10") Integer pageSize,
                                                 @RequestParam(name = "isAdmin", required = false, defaultValue = "false") Boolean isAdmin,
                                                 @RequestHeader("Token") String token) {
        Long userId = userService.getUserByToken(token).getId();
        return chargeService.doGetChargeList(userId, pageNum, pageSize, isAdmin);
    }

    @PostMapping("charge")
    public Response<PrepayWithRequestPaymentResponse> doCharge(@RequestParam("price") String price,
                                                               @RequestParam("id") Integer id,
                                                               @RequestHeader("Token") String token) throws Exception {
        if(!isValidRMBFormat(price)) {
            throw HttpException.badRequest("金额不合法");
        }
        log.warn("charge post：" + price);
        Long userId = userService.getUserByToken(token).getId();
        return Response.ok(chargeService.charge(id, userId));
    }

    public static boolean isValidRMBFormat(String amount) {
        String amountStr = String.valueOf(amount);
        String regex = "^([1-9]\\d{0,15}|0)(\\.\\d{1,2})?$"; // 人民币格式正则表达式
        return amountStr.matches(regex);
    }
}
