package com.westee.cake.service;

import com.github.pagehelper.PageHelper;
import com.westee.cake.entity.GoodsStatus;
import com.westee.cake.entity.PageResponse;
import com.westee.cake.exceptions.HttpException;
import com.westee.cake.generate.Shop;
import com.westee.cake.generate.ShopExample;
import com.westee.cake.generate.ShopMapper;
import com.westee.cake.generate.User;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.util.Date;
import java.util.List;
import java.util.Objects;

@Service
public class ShopService {
    private final ShopMapper shopMapper;

    @Autowired
    public ShopService(ShopMapper mapper) {
        this.shopMapper = mapper;
    }

    public PageResponse<Shop> getShopsByUserId(Integer pageNum, Integer pageSize) {
        User sessionUser = UserService.getSessionUser();
        Long userId = sessionUser.getId();
        ShopExample shopExample = new ShopExample();
        shopExample.createCriteria().andOwnerUserIdEqualTo(userId).andStatusEqualTo(GoodsStatus.OK.getName());
        long count = shopMapper.countByExample(shopExample);
        long totalPage = count % pageSize == 0 ? count % pageSize : count % pageSize + 1;
        PageHelper.startPage(pageNum, pageSize);
        List<Shop> shops = shopMapper.selectByExample(shopExample);
        return PageResponse.pageData(pageNum, pageSize, totalPage, shops);
    }

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

    public Shop updateShop(Shop shop) {
        User sessionUser = UserService.getSessionUser();
        Long userId = sessionUser.getId();

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

    public Shop deleteShop(Long shopId) {
        User sessionUser = UserService.getSessionUser();
        Long userId = sessionUser.getId();

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
