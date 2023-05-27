package com.westee.cake.dao;

import org.apache.ibatis.annotations.Mapper;
import org.apache.ibatis.annotations.Param;
import org.apache.ibatis.annotations.Select;

import java.util.List;

@Mapper
public interface MyGoodsImageMapper {
    @Select("select URL from GOODS_IMAGE where OWNER_GOODS_ID = #{id}")
    List<String> getGoodsImages(@Param("id") Long id);
}
