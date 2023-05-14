package com.westee.cake.service;

import com.westee.cake.generate.GoodsTypes;
import com.westee.cake.generate.GoodsTypesMapper;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.util.Date;

@Service
public class GoodsTypeService {
    GoodsTypesMapper goodsTypesMapper;

    @Autowired
    public GoodsTypeService(GoodsTypesMapper goodsTypesMapper) {
        this.goodsTypesMapper = goodsTypesMapper;
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
}
