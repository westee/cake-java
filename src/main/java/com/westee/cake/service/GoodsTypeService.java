package com.westee.cake.service;

import com.github.pagehelper.PageHelper;
import com.westee.cake.entity.GoodsStatus;
import com.westee.cake.entity.PageResponse;
import com.westee.cake.entity.Response;
import com.westee.cake.exceptions.HttpException;
import com.westee.cake.generate.*;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.util.Date;
import java.util.List;
import java.util.Objects;

@Service
public class GoodsTypeService {
    GoodsTypesMapper goodsTypesMapper;
    ShopMapper shopMapper;

    @Autowired
    public GoodsTypeService(GoodsTypesMapper goodsTypesMapper,ShopMapper shopMapper) {
        this.goodsTypesMapper = goodsTypesMapper;
        this.shopMapper = shopMapper;
    }

    public GoodsTypes createGoodsType(String typeName, long shopId) {
        GoodsTypes goodsTypes = new GoodsTypes();
        goodsTypes.setName(typeName);
        goodsTypes.setOwnerShopId(shopId);
        goodsTypes.setCreatedAt(new Date());
        goodsTypes.setUpdatedAt(new Date());
        long insert = goodsTypesMapper.insert(goodsTypes);
        return goodsTypesMapper.selectByPrimaryKey(insert);
    }

    public PageResponse<GoodsTypes> getGoodsTypesByShopId(Integer pageNum, Integer pageSize, Long shopId) {
        User sessionUser = UserService.getSessionUser();
        Long userId = sessionUser.getId();

        Shop shop = shopMapper.selectByPrimaryKey(shopId);
        if(shop != null && Objects.equals(shop.getOwnerUserId(), userId)) {
            GoodsTypesExample goodsTypesExample = new GoodsTypesExample();
            goodsTypesExample.createCriteria().andOwnerShopIdEqualTo(shopId).andDeletedIsNull();
            long count = goodsTypesMapper.countByExample(goodsTypesExample);
            PageHelper.startPage(pageNum, pageSize);
            List<GoodsTypes> goodsTypes = goodsTypesMapper.selectByExample(goodsTypesExample);
            return PageResponse.pageData(pageNum, pageSize, count, goodsTypes);
        } else {
            throw HttpException.forbidden("没有权限");
        }
    }

    public GoodsTypes updateGoodsTypes(GoodsTypes goodsTypes) {
        int insertId = goodsTypesMapper.updateByPrimaryKey(goodsTypes);
        return goodsTypesMapper.selectByPrimaryKey((long) insertId);
    }

    public GoodsTypes deleteGoodsTypes(GoodsTypes goodsTypes) {
        goodsTypes.setDeleted(1);
        int insertId = goodsTypesMapper.updateByPrimaryKey(goodsTypes);
        return goodsTypesMapper.selectByPrimaryKey((long) insertId);
    }
}
