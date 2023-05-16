package com.westee.cake.service;

import com.github.pagehelper.PageHelper;
import com.westee.cake.entity.GoodsStatus;
import com.westee.cake.entity.PageResponse;
import com.westee.cake.exceptions.HttpException;
import com.westee.cake.generate.*;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.util.Date;
import java.util.List;
import java.util.Objects;

@Service
public class GoodsService {
    private final GoodsMapper goodsMapper;
    private final ShopMapper shopMapper;

    @Autowired
    public GoodsService(GoodsMapper goodsMapper, ShopMapper shopMapper) {
        this.goodsMapper = goodsMapper;
        this.shopMapper = shopMapper;
    }

    public PageResponse<Goods> getGoodsByShopId(Integer pageNum, Integer pageSize, Long shopId) {
        User sessionUser = UserService.getSessionUser();
        Long userId = sessionUser.getId();

        Shop shop = shopMapper.selectByPrimaryKey(shopId);
        if (shop != null && Objects.equals(shop.getOwnerUserId(), userId)) {
            GoodsExample goodsExample = new GoodsExample();
            goodsExample.createCriteria().andShopIdEqualTo(shopId).andStatusEqualTo(GoodsStatus.OK.getName());
            long count = goodsMapper.countByExample(goodsExample);
            PageHelper.startPage(pageNum, pageSize);
            List<Goods> goodsList = goodsMapper.selectByExample(goodsExample);
            return PageResponse.pageData(pageNum, pageSize, count, goodsList);
        } else {
            throw HttpException.forbidden("没有权限");
        }
    }

    public Goods getGoodsByGoodsId(long goodsId) {
        return goodsMapper.selectByPrimaryKey(goodsId);
    }

    public Goods createGoods(Goods goods) {
        goods.setCreatedAt(new Date());
        goods.setUpdatedAt(new Date());
        goods.setStatus(GoodsStatus.OK.getName());
        goodsMapper.insert(goods);
        return goodsMapper.selectByPrimaryKey(goods.getId());
    }

    public Goods updateGoods(Goods goods) {
        // 根据goods的shop查询当前用户是不是店铺的拥有者
        User sessionUser = UserService.getSessionUser();
        Long userId = sessionUser.getId();

        Shop shopResult = shopMapper.selectByPrimaryKey(goods.getShopId());
        if (Objects.equals(shopResult, null)) {
            throw HttpException.forbidden("参数不合法");
        }
        Long ownerUserId = shopResult.getOwnerUserId();
        if (Objects.equals(ownerUserId, userId)) {
            goods.setUpdatedAt(new Date());
            goods.setCreatedAt(new Date());
            goodsMapper.updateByPrimaryKey(goods);
            return goods;
        } else {
            throw HttpException.forbidden("拒绝访问");
        }
    }

    public Goods deleteGoods(Long goodsId) {
        User sessionUser = UserService.getSessionUser();
        Long userId = sessionUser.getId();

        Goods goods = goodsMapper.selectByPrimaryKey(goodsId);
        Shop shop = shopMapper.selectByPrimaryKey(goods.getShopId());
        if (Objects.equals(shop, null)) {
            throw HttpException.forbidden("参数不合法");
        }
        Long ownerUserId = shop.getOwnerUserId();
        if (Objects.equals(ownerUserId, userId)) {
            goods.setStatus(GoodsStatus.DELETED.getName());
            goods.setUpdatedAt(new Date());
            goods.setCreatedAt(new Date());
            goodsMapper.updateByPrimaryKey(goods);
            return goods;
        } else {
            throw HttpException.forbidden("拒绝访问");
        }
    }
}