package com.westee.cake.controller;

import com.westee.cake.entity.PageResponse;
import com.westee.cake.exceptions.HttpException;
import com.westee.cake.generate.Charge;
import com.westee.cake.service.ChargeService;
import com.westee.cake.service.UserService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestHeader;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;

import java.math.BigDecimal;

@RestController
@RequestMapping("/api/v1/")
public class ChargeController {
    private final ChargeService chargeService;
    private final UserService userService;

    @Autowired
    public ChargeController(ChargeService chargeService, UserService userService) {
        this.chargeService = chargeService;
        this.userService = userService;
    }

    @GetMapping("charge")
    public PageResponse<Charge> getChargeHistory(@RequestParam(name = "pageNum", required = false, defaultValue = "1") Integer pageNum,
                                                 @RequestParam(name = "pageSize", required = false, defaultValue = "10") Integer pageSize,
                                                 @RequestHeader("Token") String token) {
        Long userId = userService.getUserByToken(token).getId();
        return chargeService.getChargeList(userId, pageNum, pageSize);
    }

    @PostMapping("charge")
    public Charge doCharge(@RequestParam("price") long price,
                           @RequestHeader("Token") String token) {
        if(!isValidRMBFormat(price)) {
            throw HttpException.badRequest("金额不合法");
        }
        BigDecimal bigDecimal = BigDecimal.valueOf(price);
        Long userId = userService.getUserByToken(token).getId();
        return chargeService.charge(bigDecimal, userId);
    }

    public static boolean isValidRMBFormat(long amount) {
        String amountStr = String.valueOf(amount);
        String regex = "^([1-9]\\d{0,15}|0)(\\.\\d{1,2})?$"; // 人民币格式正则表达式
        return amountStr.matches(regex);
    }
}
