package com.westee.cake.service;

import com.westee.cake.dao.MenuDAO;
import com.westee.cake.entity.Menu;
import jakarta.annotation.Resource;
import org.springframework.stereotype.Service;

import java.util.List;

@Service
public class MenuService  {
    @Resource
    private MenuDAO menuDAO;

    public List<Menu> getAllMenuByRoleId(Long roleId){
        return menuDAO.getAllMenuByRoleId(roleId);
    }
}
