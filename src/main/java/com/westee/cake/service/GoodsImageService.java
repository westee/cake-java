package com.westee.cake.service;

import com.westee.cake.exceptions.HttpException;
import com.westee.cake.generate.Goods;
import com.westee.cake.generate.GoodsImage;
import com.westee.cake.generate.GoodsImageExample;
import com.westee.cake.generate.GoodsImageMapper;
import com.westee.cake.generate.GoodsMapper;
import com.westee.cake.generate.Shop;
import com.westee.cake.generate.ShopMapper;
import com.westee.cake.generate.User;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.util.Objects;

@Service
public class GoodsImageService {
    private final GoodsImageMapper goodsImageMapper;
    private final GoodsMapper goodsMapper;
    private final ShopMapper shopMapper;

    @Autowired
    public GoodsImageService(GoodsMapper goodsMapper, ShopMapper shopMapper, GoodsImageMapper goodsImageMapper) {
        this.goodsMapper = goodsMapper;
        this.shopMapper = shopMapper;
        this.goodsImageMapper = goodsImageMapper;
    }

    public void deleteGoodsImage(String imageName) {
        GoodsImage goodsImage = new GoodsImage();
        GoodsImageExample goodsImageExample = new GoodsImageExample();
        goodsImageExample.createCriteria().andUrlEqualTo(imageName);
        checkGoodsBelongToUser(goodsImageExample);
        goodsImage.setDeleted(1);
        goodsImageMapper.updateByExampleSelective(goodsImage, goodsImageExample);
    }

    public void checkGoodsBelongToUser(GoodsImageExample example) {
        GoodsImage goodsImage = goodsImageMapper.selectByExample(example).get(0);
        Long goodsId = goodsImage.getOwnerGoodsId();
        // 根据goods的shop查询当前用户是不是店铺的拥有者
        User sessionUser = UserService.getSessionUser();
        Long userId = sessionUser.getId();

        Goods goodsResult;
        Shop shopResult;
        goodsResult = goodsMapper.selectByPrimaryKey(goodsId);
        Long shopId = goodsResult.getShopId();
        shopResult = shopMapper.selectByPrimaryKey(shopId);

        if (shopResult == null) {
            throw HttpException.forbidden("参数不合法");
        }
        if (!Objects.equals(shopResult.getOwnerUserId(), userId)) {
            throw HttpException.forbidden("拒绝访问");
        }
    }
}
