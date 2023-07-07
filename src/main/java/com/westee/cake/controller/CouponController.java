package com.westee.cake.controller;

import com.westee.cake.entity.PageResponse;
import com.westee.cake.entity.Response;
import com.westee.cake.exceptions.HttpException;
import com.westee.cake.generate.Coupon;
import com.westee.cake.service.CouponService;
import com.westee.cake.service.UserService;
import com.westee.cake.validator.CouponValidator;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestHeader;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;

import java.util.ArrayList;


@RestController
@RequestMapping("/api/v1")
public class CouponController {
    private final CouponService couponService;
    private final UserService userService;

    @Autowired
    public CouponController(CouponService couponService, UserService userService) {
        this.couponService = couponService;
        this.userService = userService;
    }

    @GetMapping("coupon")
    public PageResponse<Coupon> getCouponHistory(@RequestParam(name = "pageNum", required = false, defaultValue = "1") Integer pageNum,
                                                 @RequestParam(name = "pageSize", required = false, defaultValue = "10") Integer pageSize,
                                                 @RequestHeader("Token") String token) {
        Long userId = userService.getUserByToken(token).getId();
        return couponService.getCouponList(pageNum, pageSize);
    }

    @PostMapping("coupon")
    public Response<Coupon> createCoupon(@RequestBody Coupon coupon,
                                         @RequestHeader("Token") String token) {
        if (CouponValidator.validateCouponParameters(coupon)) {
            Long userId = userService.getUserByToken(token).getId();
            return Response.ok(couponService.insertCoupon(coupon));
        } else {
            throw HttpException.badRequest("金额不合法");

        }

    }

    @GetMapping("coupon/send")
    public Response<String> distributeCoupon(@RequestParam long couponId,
                                             @RequestHeader("Token") String token) {
        Long userId = userService.getUserByToken(token).getId();
        couponService.insertUserCoupon(couponId);
        return Response.ok("");
    }

    @GetMapping("coupon/user")
    public Response<ArrayList<Coupon>> getUserCoupons(@RequestParam(defaultValue = "", required = false) String status,
                                                      @RequestHeader("Token") String token) {
        Long tokenUserId = userService.getUserByToken(token).getId();
        ArrayList<Coupon> userCouponList = couponService.getUserCouponList(tokenUserId, status);
        return Response.ok(userCouponList);
    }

    @GetMapping("coupon/user/count")
    public Response<Long> getUserCouponCount(@RequestHeader("Token") String token) {
        Long tokenUserId = userService.getUserByToken(token).getId();
        return Response.ok(couponService.countUserCoupon(tokenUserId));
    }
}
