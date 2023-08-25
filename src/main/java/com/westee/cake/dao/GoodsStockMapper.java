package com.westee.cake.dao;

import com.westee.cake.data.GoodsInfo;
import org.apache.ibatis.annotations.Mapper;

@Mapper
public interface GoodsStockMapper {
    int deductStock(GoodsInfo goodsInfo);
    int addStock(Long id, Long number);
}
