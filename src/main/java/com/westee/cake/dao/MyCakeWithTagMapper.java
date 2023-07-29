package com.westee.cake.dao;

import com.westee.cake.entity.CakeWithTag;
import org.apache.ibatis.annotations.Mapper;
import org.apache.ibatis.annotations.Param;

@Mapper
public interface MyCakeWithTagMapper {
    CakeWithTag selectCakeWithTagsByCakeId(@Param("cakeId") long cakeId);
}
