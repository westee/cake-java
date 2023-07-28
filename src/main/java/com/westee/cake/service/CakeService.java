package com.westee.cake.service;

import com.github.pagehelper.PageHelper;
import com.westee.cake.entity.PageResponse;
import com.westee.cake.exceptions.HttpException;
import com.westee.cake.generate.Cake;
import com.westee.cake.generate.CakeExample;
import com.westee.cake.generate.CakeMapper;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.util.Date;
import java.util.List;

@Service
public class CakeService {
    private final CakeMapper cakeMapper;
    private final RoleService roleService;

    @Autowired
    public CakeService(CakeMapper cakeMapper, RoleService roleService) {
        this.cakeMapper = cakeMapper;
        this.roleService = roleService;
    }

    public PageResponse<Cake> getCakeList(Integer pageNum, Integer pageSize) {
        CakeExample cakeExample = new CakeExample();
        cakeExample.setOrderByClause("`CREATED_AT` DESC");
        long count = cakeMapper.countByExample(cakeExample);
        long totalPage = count % pageSize == 0 ? count / pageSize : count / pageSize + 1;

        PageHelper.startPage(pageNum, pageSize);
        List<Cake> cakes = cakeMapper.selectByExample(cakeExample);

        return PageResponse.pageData(pageNum, pageSize, totalPage, cakes);
    }

    public Cake insertCake(Cake cake, long roleId) {
        checkAuthorization(roleId);
        cake.setCreatedAt(new Date());
        cake.setUpdatedAt(new Date());
        cakeMapper.insert(cake);
        return cake;
    }

    public Cake updateCake(Cake cake, long roleId) {
        checkAuthorization(roleId);

        cake.setUpdatedAt(new Date());
        cakeMapper.updateByPrimaryKeySelective(cake);
        return cake;
    }

    public Cake deleteCakeByCakeId(long cakeId, long roleId) {
        checkAuthorization(roleId);

        Cake cakeSelective = new Cake();
        cakeSelective.setDeleted(true);
        cakeSelective.setId(cakeId);
        cakeSelective.setUpdatedAt(new Date());
        cakeMapper.updateByPrimaryKeySelective(cakeSelective);
        return cakeSelective;
    }

    private void checkAuthorization(long roleId) {
        if(!"admin".equals(roleService.getUserRoleById(roleId).getName())) throw HttpException.forbidden("没有权限");
    }
}
