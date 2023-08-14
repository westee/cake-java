package com.westee.cake.controller;

import com.westee.cake.mapper.MyOrderMapper;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.math.BigDecimal;
import java.time.LocalDateTime;
import java.time.temporal.TemporalAdjusters;
import java.util.ArrayList;
import java.util.List;

@Service
public class StatisticsService {
    private final MyOrderMapper myOrderMapper;

    @Autowired
    public StatisticsService(MyOrderMapper myOrderMapper) {
        this.myOrderMapper = myOrderMapper;
    }

    public List<BigDecimal> getOrderAmountByPeriod(int days) {
        List<BigDecimal> result = new ArrayList<>();
        LocalDateTime now = LocalDateTime.now();
        for (int i = days - 1; i >= 0; i--) {
            LocalDateTime startOfDay = now.minusDays(i).withHour(0).withMinute(0).withSecond(0).withNano(0);
            LocalDateTime endOfDay = startOfDay.withHour(23).withMinute(59).withSecond(59).withNano(999999999);
            BigDecimal amount = myOrderMapper.selectOrderAmountByDate(startOfDay, endOfDay);
            result.add(amount);
        }
        return result;
    }

    public List<BigDecimal> getOrderAmountByMonth(int months) {
        List<BigDecimal> result = new ArrayList<>();
        LocalDateTime now = LocalDateTime.now();
        for (int i = months - 1; i >= 0; i--) {
            LocalDateTime startOfMonth = now.minusMonths(i).withDayOfMonth(1).withHour(0).withMinute(0).withSecond(0).withNano(0);
            LocalDateTime endOfMonth = startOfMonth.plusMonths(1).minusNanos(1);
            BigDecimal amount = myOrderMapper.selectOrderAmountByDate(startOfMonth, endOfMonth);
            result.add(amount);
        }
        return result;
    }

    public List<Integer> getOrderCountByPeriod(int days) {
        List<Integer> result = new ArrayList<>();
        LocalDateTime now = LocalDateTime.now();
        for (int i = days - 1; i >= 0; i--) {
            LocalDateTime startOfDay = now.minusDays(i).withHour(0).withMinute(0).withSecond(0).withNano(0);
            LocalDateTime endOfDay = startOfDay.withHour(23).withMinute(59).withSecond(59).withNano(999999999);
            int count = myOrderMapper.selectOrderCountByDate(startOfDay, endOfDay);
            result.add(count);
        }
        return result;
    }

    public List<Integer> getOrderCountByMonth(int months) {
        List<Integer> result = new ArrayList<>();
        LocalDateTime now = LocalDateTime.now();
        for (int i = months - 1; i >= 0; i--) {
            LocalDateTime startOfMonth = now.minusMonths(i).withDayOfMonth(1).withHour(0).withMinute(0).withSecond(0).withNano(0);
            LocalDateTime endOfMonth = startOfMonth.with(TemporalAdjusters.lastDayOfMonth()).withHour(23).withMinute(59).withSecond(59).withNano(999999999);
            int count = myOrderMapper.selectOrderCountByDate(startOfMonth, endOfMonth);
            result.add(count);
        }
        return result;
    }

    public List<BigDecimal> getChargeAmountByPeriod(int days) {
        List<BigDecimal> result = new ArrayList<>();
        LocalDateTime now = LocalDateTime.now();
        for (int i = days - 1; i >= 0; i--) {
            LocalDateTime startOfDay = now.minusDays(i).withHour(0).withMinute(0).withSecond(0).withNano(0);
            LocalDateTime endOfDay = startOfDay.withHour(23).withMinute(59).withSecond(59).withNano(999999999);
            BigDecimal amount = myOrderMapper.selectChargeAmountByDate(startOfDay, endOfDay);
            result.add(amount);
        }
        return result;
    }

    public List<BigDecimal> getChargeAmountByMonth(int months) {
        List<BigDecimal> result = new ArrayList<>();
        LocalDateTime now = LocalDateTime.now();
        for (int i = months - 1; i >= 0; i--) {
            LocalDateTime startOfMonth = now.minusMonths(i).withDayOfMonth(1).withHour(0).withMinute(0).withSecond(0).withNano(0);
            LocalDateTime endOfMonth = startOfMonth.plusMonths(1).minusNanos(1);
            BigDecimal amount = myOrderMapper.selectChargeAmountByDate(startOfMonth, endOfMonth);
            result.add(amount);
        }
        return result;
    }
}
