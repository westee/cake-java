package com.westee.cake.dao;

import com.westee.cake.entity.Menu;
import org.apache.ibatis.annotations.Mapper;
import org.apache.ibatis.annotations.Select;

import java.util.List;

@Mapper
public interface MenuDAO extends MyBatisBaseDao<Menu, Integer>{
    /**
     * 通过角色id获取所有权限信息
     * @param id
     * @return
     */
    @Select("select NAME from MENU where MENU_ID = #{id}")
    List<Menu> getAllMenuByRoleId(Long id);
}
