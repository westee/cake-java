package com.westee.cake.dao;

import com.westee.cake.entity.GoodsWithImages;
import org.apache.ibatis.annotations.Mapper;
import org.apache.ibatis.annotations.Param;

import java.util.List;

@Mapper
public interface MyGoodsWithImageMapper {
    GoodsWithImages getGoodsWithImage(long goodsId);
    List<GoodsWithImages> getGoodsListWithImageByShopId(@Param("id") long shopId,
                                                        @Param("limit") int limit,
                                                        @Param("offset") int offset,
                                                        @Param("status") String status);

    List<GoodsWithImages> getGoodsListWithImageByShopIdAndCategory(@Param("id") long shopId,
                                                        @Param("categoryId") long categoryId);
}
