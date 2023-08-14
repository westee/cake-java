package com.westee.cake.controller;

import com.westee.cake.entity.Response;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;

import java.math.BigDecimal;
import java.util.ArrayList;
import java.util.List;

@RestController
@RequestMapping("/api/v1/statistics")
public class StatisticsController {
    private final StatisticsService statisticsService;

    @Autowired
    public StatisticsController(StatisticsService statisticsService) {
        this.statisticsService = statisticsService;
    }

    @GetMapping("money")
    public Response<List<BigDecimal>> getMoneyStatistics(@RequestParam String type,
                                                         @RequestParam Integer num) {
        List<BigDecimal> sums =  new ArrayList<>();
        if("day".equals(type)) {
           sums = statisticsService.getOrderAmountByPeriod(num); // 7 || 30
        } else {
           sums = statisticsService.getOrderAmountByMonth(12);
        }
        return Response.ok(sums);
    }

    @GetMapping("order")
    public Response<List<Integer>> getOrderStatistics(@RequestParam String type,
                                                      @RequestParam Integer num) {
        List<Integer> sums =  new ArrayList<>();
        if("day".equals(type)) {
            sums = statisticsService.getOrderCountByPeriod(num); // 7 || 30
        } else {
            sums = statisticsService.getOrderCountByMonth(12);
        }
        return Response.ok(sums);
    }

    @GetMapping("charge")
    public Response<List<BigDecimal>> getChargeStatistics(@RequestParam String type,
                                                         @RequestParam Integer num) {
        List<BigDecimal> sums =  new ArrayList<>();
        if("day".equals(type)) {
            sums = statisticsService.getChargeAmountByPeriod(num); // 7 || 30
        } else {
            sums = statisticsService.getChargeAmountByMonth(12);
        }
        return Response.ok(sums);
    }
}
