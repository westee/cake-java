package com.westee.cake.mapper;

import com.westee.cake.generate.Goods;
import org.apache.ibatis.annotations.Mapper;

import java.util.List;

@Mapper
public interface MyGoodsMapper {
    List<Goods> selectGoodsByName(int start, int offset, String name);
    int countGoodsByName(String name);
}
