package com.westee.cake.service;

import com.github.pagehelper.PageHelper;
import com.westee.cake.entity.GoodsWithImages;
import com.westee.cake.entity.PageResponse;
import com.westee.cake.exceptions.HttpException;
import com.westee.cake.generate.DiscountDay;
import com.westee.cake.generate.DiscountDayExample;
import com.westee.cake.generate.DiscountDayMapper;
import com.westee.cake.generate.Role;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.util.Date;
import java.util.List;

@Service
public class DiscountDayService {
    private final UserService userService;
    private final DiscountDayMapper discountDayMapper;
    private final GoodsService goodsService;

    @Autowired
    public DiscountDayService(UserService userService, GoodsService goodsService,
                              DiscountDayMapper discountDayMapper) {
        this.userService = userService;
        this.discountDayMapper = discountDayMapper;
        this.goodsService = goodsService;
    }

    public PageResponse<DiscountDay> getDiscountDayList(Long userId, Integer pageNum, Integer pageSize) {
        if (checkAdmin(userId)) {
            DiscountDayExample discountDayExample = new DiscountDayExample();
            discountDayExample.setOrderByClause("`CREATED_AT` DESC");
            discountDayExample.createCriteria().andDisabledEqualTo(false);
            long count = discountDayMapper.countByExample(discountDayExample);
            long totalPage = count % pageSize == 0 ? count / pageSize : count / pageSize + 1;

            PageHelper.startPage(pageNum, pageSize);
            List<DiscountDay> discountDays = discountDayMapper.selectByExample(discountDayExample);
            discountDays.forEach(this::getGoodsName);

            return PageResponse.pageData(pageNum, pageSize, totalPage, discountDays);
        }
        return null;
    }

    public void getGoodsName(DiscountDay discountDay) {
        GoodsWithImages goodsByGoodsId = goodsService.getGoodsByGoodsId(discountDay.getGoodsId());
        discountDay.setGoodsName(goodsByGoodsId.getName());
        discountDay.setOriginPrice(goodsByGoodsId.getPrice());
    }

    public DiscountDay createDiscountDay(Long userId, DiscountDay discountDay) {
        if (checkAdmin(userId)) {
            DiscountDayExample example = new DiscountDayExample();
            example.createCriteria().andGoodsIdEqualTo(discountDay.getGoodsId());
            List<DiscountDay> result = discountDayMapper.selectByExample(example);
            if (!result.isEmpty()) {
                // GOODS_ID already exists
                throw HttpException.badRequest("商品活动日已设置，请重新选择商品");
            }
            discountDay.setDisabled(false);
            Date date = new Date();
            discountDay.setCreatedAt(date);
            discountDay.setUpdatedAt(date);
            discountDayMapper.insert(discountDay);
        }
        getGoodsName(discountDay);

        return discountDay;
    }

    public DiscountDay updateDiscountDay(Long userId, DiscountDay discountDay) {
        if (checkAdmin(userId)) {
            discountDay.setUpdatedAt(new Date());
            discountDayMapper.updateByPrimaryKeySelective(discountDay);
        }
        getGoodsName(discountDay);
        return discountDay;
    }

    public DiscountDay deleteDiscountDay(Long userId, int discountDayId, boolean status) {
        if (checkAdmin(userId)) {
            DiscountDay discountDay = new DiscountDay();
            discountDay.setId(discountDayId);
            discountDay.setDisabled(status);
            discountDayMapper.updateByPrimaryKeySelective(discountDay);
            return discountDay;
        }
        return null;
    }

    public boolean checkAdmin(long userId) {
        Role userRole = userService.getUserRole(userId);
        if ("admin".equals(userRole.getName())) {
            return true;
        }
        throw HttpException.forbidden("没有权限");
    }


}
