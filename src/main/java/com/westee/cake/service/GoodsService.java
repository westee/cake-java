package com.westee.cake.service;

import com.westee.cake.dao.MyGoodsWithImageMapper;
import com.westee.cake.entity.GoodsStatus;
import com.westee.cake.entity.GoodsWithImages;
import com.westee.cake.entity.PageResponse;
import com.westee.cake.exceptions.HttpException;
import com.westee.cake.generate.DiscountDay;
import com.westee.cake.generate.DiscountDayExample;
import com.westee.cake.generate.DiscountDayMapper;
import com.westee.cake.generate.Goods;
import com.westee.cake.generate.GoodsExample;
import com.westee.cake.generate.GoodsImage;
import com.westee.cake.generate.GoodsImageExample;
import com.westee.cake.generate.GoodsImageMapper;
import com.westee.cake.generate.GoodsMapper;
import com.westee.cake.generate.Shop;
import com.westee.cake.generate.ShopMapper;
import com.westee.cake.mapper.MyGoodsMapper;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.math.BigDecimal;
import java.util.Calendar;
import java.util.Date;
import java.util.List;
import java.util.Map;
import java.util.Objects;
import java.util.stream.Collectors;

@Service
public class GoodsService {
    private final GoodsMapper goodsMapper;
    private final ShopMapper shopMapper;
    private final GoodsImageMapper goodsImageMapper;
    private final MyGoodsWithImageMapper myGoodsWithImageMapper;
    private final MyGoodsMapper myGoodsMapper;
    private final DiscountDayMapper discountDayMapper;

    @Autowired
    public GoodsService(GoodsMapper goodsMapper, ShopMapper shopMapper, GoodsImageMapper goodsImageMapper,
                        MyGoodsWithImageMapper myGoodsWithImageMapper, MyGoodsMapper myGoodsMapper,
                         DiscountDayMapper discountDayMapper) {
        this.goodsMapper = goodsMapper;
        this.shopMapper = shopMapper;
        this.myGoodsMapper = myGoodsMapper;
        this.goodsImageMapper = goodsImageMapper;
        this.myGoodsWithImageMapper = myGoodsWithImageMapper;
        this.discountDayMapper = discountDayMapper;
    }

    public PageResponse<GoodsWithImages> getGoodsByShopIdAndCategoryId(Integer pageNum, Integer pageSize, Long shopId, String status) {
        Shop shop = shopMapper.selectByPrimaryKey(shopId);
        if (shop != null) {
            GoodsExample goodsExample = new GoodsExample();
            String statusString = "ok|deleted";
            if(statusString.contains(status)) {
                goodsExample.createCriteria().andStatusEqualTo(status);
            } else {
                goodsExample.createCriteria().andStockEqualTo(0);
            }
            long count = goodsMapper.countByExample(goodsExample);
            long totalPage = count % pageSize == 0 ? count / pageSize : count / pageSize + 1;
            int offset = (pageNum - 1) * pageSize;
            List<GoodsWithImages> goodsListWithImageByShopId =
                    myGoodsWithImageMapper.getGoodsListWithImageByShopId(shopId, pageSize, offset, status);
            goodsListWithImageByShopId.forEach(goodsWithImages -> goodsWithImages.setVipPrice(getGoodsDiscountPrice(goodsWithImages)));
            return PageResponse.pageData(pageNum, pageSize, totalPage, goodsListWithImageByShopId);
        } else {
            throw HttpException.forbidden("没有权限");
        }
    }

    public List<GoodsWithImages> getGoodsByShopIdAndCategoryId(Long shopId, Long categoryId) {
        Shop shop = shopMapper.selectByPrimaryKey(shopId);
        if (shop != null) {
            GoodsExample goodsExample = new GoodsExample();
            goodsExample.createCriteria().andCategoryIdEqualTo(categoryId).andStatusEqualTo(GoodsStatus.OK.getName());
            List<GoodsWithImages> goodsListWithImageByShopIdAndCategory = myGoodsWithImageMapper.getGoodsListWithImageByShopIdAndCategory(shopId, categoryId);
            goodsListWithImageByShopIdAndCategory.forEach(goodsWithImages -> goodsWithImages.setVipPrice(getGoodsDiscountPrice(goodsWithImages)));
            return goodsListWithImageByShopIdAndCategory;
        } else {
            throw HttpException.forbidden("没有权限");
        }
    }

    public GoodsWithImages getGoodsByGoodsId(long goodsId) {
        GoodsWithImages goodsWithImage = myGoodsWithImageMapper.getGoodsWithImage(goodsId);
        goodsWithImage.setVipPrice(getGoodsDiscountPrice(goodsWithImage));
        return goodsWithImage;
    }

    public GoodsWithImages createGoods(GoodsWithImages goods, long userId) {
        checkGoodsBelongToUser(goods, userId);
        goods.setCreatedAt(new Date());
        goods.setUpdatedAt(new Date());
        goods.setStatus(GoodsStatus.OK.getName());
        if (checkFieldsLegal(goods)) {
            goodsMapper.insert(goods);
            insertGoodsImage(goods.getImages(), goods.getId());
        } else {
            throw HttpException.badRequest("参数不合法");
        }
        return myGoodsWithImageMapper.getGoodsWithImage(goods.getId());
    }

    private void insertGoodsImage(List<String> images, long goodsId) {
        images.forEach(item -> {
            GoodsImage goodsImage = new GoodsImage();
            goodsImage.setOwnerGoodsId(goodsId);
            goodsImage.setUrl(item);
            goodsImage.setCreatedAt(new Date());
            goodsImage.setUpdatedAt(new Date());
            goodsImageMapper.insertSelective(goodsImage);
        });
    }

    public GoodsWithImages updateGoods(GoodsWithImages goods, Long userId) {
        checkGoodsBelongToUser(goods, userId);
        goods.setUpdatedAt(new Date());
        goods.setCreatedAt(new Date());
        goodsMapper.updateByPrimaryKeySelective(goods);
        insertGoodsImage(goods.getImages(), goods.getId());
        return myGoodsWithImageMapper.getGoodsWithImage(goods.getId());
    }

    public Goods deleteGoods(Long goodsId, Long userId) {
        Goods goods = new Goods();
        goods.setId(goodsId);
        checkGoodsBelongToUser(goods, userId);
        goods.setStatus(GoodsStatus.DELETED.getName());
        goods.setUpdatedAt(new Date());
        goods.setCreatedAt(new Date());
        goodsMapper.updateByPrimaryKeySelective(goods);
        return goods;
    }

    public Map<Long, Goods> getGoodsToMapByGoodsIds(List<Long> goodsIds) {
        GoodsExample example = new GoodsExample();
        example.createCriteria().andIdIn(goodsIds);
        List<Goods> goods = goodsMapper.selectByExample(example);
        GoodsImageExample goodsImageExample = new GoodsImageExample();
        goods.forEach(item -> {
            goodsImageExample.createCriteria().andOwnerGoodsIdEqualTo(item.getId());
            List<GoodsImage> goodsImages = goodsImageMapper.selectByExample(goodsImageExample);
            if (goodsImages.isEmpty()) {
                item.setImgUrl("");
            } else {
                item.setImgUrl(goodsImages.get(0).getUrl());
            }
        });
        return goods.stream().collect(Collectors.toMap(Goods::getId, x -> x));
    }

    public List<Goods> getGoodsByName(int pageNum, int pageSize, String goodsName) {
        List<Goods> goodsList = myGoodsMapper.selectGoodsByName(pageNum, pageSize, goodsName);
        goodsList.forEach(goods -> goods.setVipPrice(getGoodsDiscountPrice(goods)));
        return goodsList;
    }

    public int countGoodsByName(String goodsName) {
        return myGoodsMapper.countGoodsByName(goodsName);
    }

    /**
     * 折扣日
     * 活动价格放在vip price中
     * @param goods     商品
     * @return          优惠价格
     */
    public BigDecimal getGoodsDiscountPrice(Goods goods) {
        DiscountDayExample discountDayExample = new DiscountDayExample();
        discountDayExample.createCriteria().andGoodsIdEqualTo(goods.getId());
        List<DiscountDay> discountDays = discountDayMapper.selectByExample(discountDayExample);
        if (discountDays.isEmpty()) {
            return null;
        }
        DiscountDay discountDay = discountDays.get(0);
        if (discountDay.getDisabled() || !isDiscountDay(discountDay.getDays())) {
            return null;
        }
        return discountDay.getPrice();
    }

    private boolean isValidName(String name) {
        return name.length() <= 100 && !name.contains("_") && !name.contains("-");
    }

    private boolean isValidDescription(String description) {
        return description.length() <= 1024 && !description.contains("_") && !description.contains("-");
    }

    public boolean checkFieldsLegal(Goods goods) {
        boolean isLegal = true;
        // 检查名称是否合法
        String name = goods.getName();
        if (name == null || !isValidName(name)) {
            isLegal = false;
        }
        // 检查描述是否合法
        String description = goods.getDescription();
        if (description == null || !isValidDescription(description)) {
            isLegal = false;
        }
        // 检查价格是否合法
        double price = goods.getPrice().doubleValue();
        if (price < 0 || price > 100000) {
            isLegal = false;
        }
        // 检查 VIP 价格是否合法
//        double vipPrice = goods.getVIPPrice();
//        if (vipPrice < 0 || vipPrice > 100000) {
//            isLegal = false;
//        }
        // 检查库存是否合法
        int stock = goods.getStock();
        if (stock < 0 || stock > 500) {
            isLegal = false;
        }
        // 如果所有字段都是合法的，则返回 true
        return isLegal;
    }

    public void checkGoodsBelongToUser(Goods goods, Long userId) {
        // 根据goods的shop查询当前用户是不是店铺的拥有者
        Long goodsId = goods.getId();
        Goods goodsResult;
        Shop shopResult;
        // 非新创建商品 删除商品时只传了商品id
        if (goodsId != null) {
            goodsResult = goodsMapper.selectByPrimaryKey(goodsId);
            Long shopId = goodsResult.getShopId();
            shopResult = shopMapper.selectByPrimaryKey(shopId);
        } else {
            // 新创建的商品 没有商品id
            shopResult = shopMapper.selectByPrimaryKey(goods.getShopId());
        }
        if (shopResult == null) {
            throw HttpException.forbidden("参数不合法");
        }
        if (!Objects.equals(shopResult.getOwnerUserId(), userId)) {
            throw HttpException.forbidden("拒绝访问");
        }
    }

    public boolean isDiscountDay(String days) {
        Calendar calendar = Calendar.getInstance();
        int dayOfWeek = calendar.get(Calendar.DAY_OF_WEEK);
        String dayOfWeekStr = String.valueOf(dayOfWeek - 1);
        return days.contains(dayOfWeekStr);
    }
}
