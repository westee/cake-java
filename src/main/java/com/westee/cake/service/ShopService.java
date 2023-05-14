package com.westee.cake.service;

import com.westee.cake.entity.GoodsStatus;
import com.westee.cake.exceptions.HttpException;
import com.westee.cake.generate.Shop;
import com.westee.cake.generate.ShopMapper;
import com.westee.cake.generate.User;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.util.Date;
import java.util.Objects;

@Service
public class ShopService {
    private final ShopMapper shopMapper;

    @Autowired
    public ShopService(ShopMapper mapper) {
        this.shopMapper = mapper;
    }

//    public PageResponse<Shop> getShopsByUserId(Long userId, Integer pageNum, Integer pageSize) {
//        ShopExample shopExample = new ShopExample();
//        shopExample.createCriteria().andOwnerUserIdEqualTo(userId).andStatusEqualTo(GoodsStatus.OK.getName());
//        long count = shopMapper.countByExample(shopExample);
//        PageHelper.startPage(pageNum, pageSize);
//        List<Shop> shops = shopMapper.selectByExample(shopExample);
//        return PageResponse.pageData(pageNum, pageSize, count, shops);
//    }

    public Shop createShop(Shop shop) {
        User sessionUser = UserService.getSessionUser();
        Long userId = sessionUser.getId();
        shop.setOwnerUserId(userId);
        shop.setId(null);
        shop.setStatus(GoodsStatus.OK.getName());
        shop.setUpdatedAt(new Date());
        shop.setCreatedAt(new Date());
        int shopId = shopMapper.insertSelective(shop);
        shop.setId((long) shopId);
        return shop;
    }

    public Shop updateShop(Shop shop, Long userId) {
        Shop shopResult = shopMapper.selectByPrimaryKey(shop.getId());
        if (Objects.equals(shopResult, null)) {
            throw HttpException.forbidden("参数不合法");
        }
        Long ownerUserId = shopResult.getOwnerUserId();
        if (Objects.equals(ownerUserId, userId)) {
            shop.setUpdatedAt(new Date());
            shop.setCreatedAt(new Date());
            shopMapper.updateByPrimaryKeySelective(shop);
            return shop;
        } else {
            throw HttpException.forbidden("拒绝访问");
        }
    }

    public Shop deleteShop(Long shopId, Long userId) {
        Shop shop = shopMapper.selectByPrimaryKey(shopId);
        if (Objects.equals(shop, null)) {
            throw HttpException.forbidden("参数不合法");
        }
        Long ownerUserId = shop.getOwnerUserId();
        if (Objects.equals(ownerUserId, userId)) {
            shop.setStatus(GoodsStatus.DELETED.getName());
            shop.setUpdatedAt(new Date());
            shop.setCreatedAt(new Date());
            shopMapper.updateByPrimaryKeySelective(shop);
            return shop;
        } else {
            throw HttpException.forbidden("拒绝访问");
        }
    }

    public Shop getShopByShopId(long shopId) {
        return shopMapper.selectByPrimaryKey(shopId);
    }

}
