package com.westee.cake.service;

import com.westee.cake.dao.ShoppingCartQueryMapper;
import com.westee.cake.entity.AddToShoppingCartItem;
import com.westee.cake.entity.AddToShoppingCartRequest;
import com.westee.cake.entity.GoodsStatus;
import com.westee.cake.entity.PageResponse;
import com.westee.cake.entity.ShoppingCartData;
import com.westee.cake.entity.ShoppingCartGoods;
import com.westee.cake.generate.Goods;
import com.westee.cake.generate.ShoppingCart;
import com.westee.cake.generate.ShoppingCartMapper;
import org.apache.ibatis.session.ExecutorType;
import org.apache.ibatis.session.SqlSession;
import org.apache.ibatis.session.SqlSessionFactory;
import org.springframework.stereotype.Service;

import java.util.ArrayList;
import java.util.Date;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.stream.Collectors;

@Service
public class ShoppingCartService {
    private final ShoppingCartQueryMapper shoppingCartQueryMapper;
    private final GoodsService goodsService;
    private final SqlSessionFactory sqlSessionFactory;

    public ShoppingCartService(ShoppingCartQueryMapper shoppingCartQueryMapper,
                               GoodsService goodsService, SqlSessionFactory sqlSessionFactory) {
        this.goodsService = goodsService;
        this.sqlSessionFactory = sqlSessionFactory;
        this.shoppingCartQueryMapper = shoppingCartQueryMapper;
    }

    public PageResponse<ShoppingCartData> getShoppingCartOfUser(int pageNum, int pageSize, Long userId) {
        int count = shoppingCartQueryMapper.countShopsInUserShoppingCart(userId);
        int offset = (pageNum - 1) * pageSize;
        int totalPage = count % pageSize == 0 ? count / pageSize : count / pageSize + 1;
        List<ShoppingCartData> shoppingCartData = shoppingCartQueryMapper.selectShoppingCartDataByUserId(userId, pageSize, offset)
                .stream()
                .collect(Collectors.groupingBy(shoppingCartData1 -> shoppingCartData1.getShop().getId()))
                .values()
                .stream()
                .map(this::merge)
                .collect(Collectors.toList());
        return PageResponse.pageData(pageNum, pageSize, totalPage, shoppingCartData);
    }

    /**
     * 将同一个shop下的多条goods记录放到同一个ShoppingCartData的goods List中
     *
     * @param shoppingCartData      待处理购物车数据列表
     * @return ShoppingCartData     处理好的购物车数据列表
     */
    private ShoppingCartData merge(List<ShoppingCartData> shoppingCartData) {
        ShoppingCartData result = new ShoppingCartData();
        result.setShop(shoppingCartData.get(0).getShop());
        List<ShoppingCartGoods> goods = shoppingCartData.stream()
                .map(ShoppingCartData::getGoods)
                .flatMap(List::stream)
                .collect(Collectors.toList());

        List<ShoppingCartGoods> shoppingCartGoods = mergeRepeatGoods(goods);
        result.setGoods(shoppingCartGoods);
        return result;

    }

    /**
     * 将多个相同的goodsId合并成一个，并修改number
     * @param goodsList                 同一个店铺下的购物车中的商品
     * @return List<ShoppingCartGoods>  将多个相同商品合并为一个
     */
    private List<ShoppingCartGoods> mergeRepeatGoods(List<ShoppingCartGoods> goodsList) {
        HashMap<Long, ShoppingCartGoods> goodsMap = new HashMap<>();
        goodsList.forEach
                (goods -> {
                    Long goodsId = goods.getId();
                    if (goodsMap.containsKey(goodsId)) {
                        goods.setNumber(goodsMap.get(goodsId).getNumber() + goods.getNumber());
                    } else {
                        goodsMap.put(goodsId, goods);
                    }
                });

        return new ArrayList<>(goodsMap.values());
    }

    public void deleteShoppingCart(long goodsId, Long userId) {
        shoppingCartQueryMapper.deleteShoppingCart(goodsId, userId);
    }

    public ShoppingCartData addGoodsToShoppingCart(AddToShoppingCartRequest request, Long userId) {
        List<Long> goodsIds = request.getGoods().stream()
                .map(AddToShoppingCartItem::getId)
                .collect(Collectors.toList());

        Map<Long, Goods> goodsToMapByGoodsIds = goodsService.getGoodsToMapByGoodsIds(goodsIds);

        List<ShoppingCart> collect = request.getGoods().stream().map(item -> makeShoppingCartRow(item, goodsToMapByGoodsIds)).collect(Collectors.toList());


        try (SqlSession sqlSession = sqlSessionFactory.openSession(ExecutorType.BATCH)) {
            ShoppingCartMapper mapper = sqlSession.getMapper(ShoppingCartMapper.class);
            collect.forEach(mapper::insert);
            sqlSession.commit();
        }

        return getLatestShoppingCartDataByUserIdShopId(new ArrayList<>(goodsToMapByGoodsIds.values()).get(0).getShopId(), userId);
    }

    private ShoppingCartData getLatestShoppingCartDataByUserIdShopId(long shopId, long userId) {
        List<ShoppingCartData> result = shoppingCartQueryMapper.selectAllShoppingCartDataByUserId(userId, shopId);
        return merge(result);
    }

    private ShoppingCart makeShoppingCartRow(AddToShoppingCartItem goodsItem, Map<Long, Goods> goodsToMapByGoodsIds) {
        Goods goods = goodsToMapByGoodsIds.get(goodsItem.getId());
        ShoppingCart shoppingCart = new ShoppingCart();
        shoppingCart.setNumber(goodsItem.getNumber());
        shoppingCart.setCreatedAt(new Date());
        shoppingCart.setUpdatedAt(new Date());
        shoppingCart.setStatus(GoodsStatus.OK.getName());

        shoppingCart.setGoodsId(goodsItem.getId());
        shoppingCart.setShopId(goods.getShopId());
        shoppingCart.setUserId(UserContext.getCurrentUser().getId());

        return shoppingCart;
    }

}
