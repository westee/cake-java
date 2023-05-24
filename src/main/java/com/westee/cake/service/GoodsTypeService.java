package com.westee.cake.service;

import com.westee.cake.exceptions.HttpException;
import com.westee.cake.generate.GoodsTypes;
import com.westee.cake.generate.GoodsTypesExample;
import com.westee.cake.generate.GoodsTypesMapper;
import com.westee.cake.generate.Shop;
import com.westee.cake.generate.ShopMapper;
import com.westee.cake.generate.User;
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
    public GoodsTypeService(GoodsTypesMapper goodsTypesMapper, ShopMapper shopMapper) {
        this.goodsTypesMapper = goodsTypesMapper;
        this.shopMapper = shopMapper;
    }

    public GoodsTypes createGoodsType(String typeName, long shopId) {
        GoodsTypes goodsTypes = new GoodsTypes();
        goodsTypes.setName(typeName);
        goodsTypes.setOwnerShopId(shopId);
        goodsTypes.setCreatedAt(new Date());
        goodsTypes.setUpdatedAt(new Date());
        checkBelongToUser(goodsTypes);
        goodsTypesMapper.insert(goodsTypes);
        return goodsTypes;
    }

    public List<GoodsTypes> getGoodsTypesByShopId(Long shopId) {
        GoodsTypesExample goodsTypesExample = new GoodsTypesExample();
        goodsTypesExample.createCriteria().andOwnerShopIdEqualTo(shopId).andDeletedIsNull();
        List<GoodsTypes> goodsTypes = goodsTypesMapper.selectByExample(goodsTypesExample);
        return goodsTypes;
    }

    public GoodsTypes updateGoodsTypes(GoodsTypes goodsTypes) {
        checkBelongToUser(goodsTypes);
        goodsTypesMapper.updateByPrimaryKey(goodsTypes);
        return goodsTypes;
    }

    public GoodsTypes deleteGoodsTypes(GoodsTypes goodsTypes) {
        checkBelongToUser(goodsTypes);
        goodsTypes.setDeleted(1);
        goodsTypesMapper.updateByPrimaryKeySelective(goodsTypes);
        return goodsTypes;
    }

    public void checkBelongToUser(GoodsTypes goodsTypes) {
        // 根据goods的shop查询当前用户是不是店铺的拥有者
        User sessionUser = UserService.getSessionUser();
        Long userId = sessionUser.getId();

        Shop shopResult = shopMapper.selectByPrimaryKey(goodsTypes.getOwnerShopId());
        if (shopResult == null) {
            throw HttpException.forbidden("参数不合法");
        }
        if (!Objects.equals(shopResult.getOwnerUserId(), userId)) {
            throw HttpException.forbidden("拒绝访问");
        }
    }
}
