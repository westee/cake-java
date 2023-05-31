package com.westee.cake.dao;

import com.westee.cake.entity.ShoppingCartData;
import org.apache.ibatis.annotations.Mapper;
import org.apache.ibatis.annotations.Param;

import java.util.List;

@Mapper
public interface ShoppingCartQueryMapper {
    int countShopsInUserShoppingCart(long userId);

    List<ShoppingCartData> selectShoppingCartDataByUserId(
            @Param("userId") long userId,
            @Param("limit") int limit,
            @Param("offset") int offset
    );

    List<ShoppingCartData> selectAllShoppingCartDataByUserId(
            @Param("userId") long userId,
            @Param("shopId") long shopId
    );

    void deleteShoppingCart(@Param("goodsId") long goodsId, @Param("userId") long userId);
}