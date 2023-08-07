package com.westee.cake.controller;

import com.westee.cake.entity.PageResponse;
import com.westee.cake.entity.Response;
import com.westee.cake.generate.DiscountDay;
import com.westee.cake.service.DiscountDayService;
import com.westee.cake.service.UserService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.DeleteMapping;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PatchMapping;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestHeader;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;

@RestController
@RequestMapping("api/v1/")
public class DiscountDayController {
    private final DiscountDayService discountDayService;
    private final UserService userService;

    @Autowired
    public DiscountDayController(DiscountDayService discountDayService, UserService userService) {
        this.discountDayService = discountDayService;
        this.userService = userService;
    }

    @GetMapping("discount_day")
    public PageResponse<DiscountDay> getChargeHistory(@RequestParam(name = "pageNum", required = false, defaultValue = "1") Integer pageNum,
                                                      @RequestParam(name = "pageSize", required = false, defaultValue = "10") Integer pageSize,
                                                      @RequestHeader("Token") String token) {
        Long userId = userService.getUserByToken(token).getId();
        return discountDayService.getDiscountDayList(userId, pageNum, pageSize);
    }

    @PostMapping("discount_day")
    public Response<DiscountDay> doCharge(@RequestBody DiscountDay discountDay,
                                          @RequestHeader("Token") String token) {
        Long userId = userService.getUserByToken(token).getId();
        return Response.ok(discountDayService.createDiscountDay(userId, discountDay));
    }

    @PatchMapping("discount_day")
    public Response<DiscountDay> patchCharge(@RequestBody DiscountDay discountDay,
                                             @RequestHeader("Token") String token) {
        Long userId = userService.getUserByToken(token).getId();
        return Response.ok(discountDayService.updateDiscountDay(userId, discountDay));
    }

    @DeleteMapping("discount_day")
    public Response<DiscountDay> deleteCharge(@RequestParam Integer discountDayId,
                                              @RequestParam Boolean status,
                                              @RequestHeader("Token") String token) {
        Long userId = userService.getUserByToken(token).getId();
        return Response.ok(discountDayService.deleteDiscountDay(userId, discountDayId, status));
    }

}
