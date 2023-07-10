package com.westee.cake.mapper;

import com.westee.cake.generate.Address;
import org.apache.ibatis.annotations.Mapper;
import org.apache.ibatis.annotations.Select;

@Mapper
public interface MyAddressMapper {
    @Select("select * from ADDRESS where USER_ID = #{userId} order by CREATED_AT desc limit 1")
    Address selectLatestAddress(long userId);
}
