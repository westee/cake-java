package com.westee.cake.controller;

import com.westee.cake.entity.PageResponse;
import com.westee.cake.entity.Response;
import com.westee.cake.generate.DiscountDay;
import com.westee.cake.service.DiscountDayService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.DeleteMapping;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PatchMapping;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;

@RestController
@RequestMapping("api/v1/")
public class DiscountDayController {
    private final DiscountDayService discountDayService;

    @Autowired
    public DiscountDayController(DiscountDayService discountDayService) {
        this.discountDayService = discountDayService;
    }

    @GetMapping("discount_day")
    public PageResponse<DiscountDay> getChargeHistory(@RequestParam(name = "pageNum", required = false, defaultValue = "1") Integer pageNum,
                                                      @RequestParam(name = "pageSize", required = false, defaultValue = "10") Integer pageSize) {
        return discountDayService.getDiscountDayList( pageNum, pageSize);
    }

    @PostMapping("discount_day")
    public Response<DiscountDay> doCharge(@RequestBody DiscountDay discountDay) {
        return Response.ok(discountDayService.createDiscountDay( discountDay));
    }

    @PatchMapping("discount_day")
    public Response<DiscountDay> patchCharge(@RequestBody DiscountDay discountDay) {
        return Response.ok(discountDayService.updateDiscountDay( discountDay));
    }

    @DeleteMapping("discount_day")
    public Response<DiscountDay> deleteCharge(@RequestParam Integer discountDayId) {
        return Response.ok(discountDayService.deleteDiscountDay(discountDayId));
    }

}
