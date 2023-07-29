package com.westee.cake.service;

import com.github.pagehelper.PageHelper;
import com.westee.cake.entity.PageResponse;
import com.westee.cake.exceptions.HttpException;
import com.westee.cake.generate.CakeTag;
import com.westee.cake.generate.CakeTagExample;
import com.westee.cake.generate.CakeTagMapper;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.util.Date;
import java.util.List;

@Service
public class CakeTagService {
    private final RoleService roleService;
    private final CakeTagMapper cakeTagMapper;

    @Autowired
    public CakeTagService(CakeTagMapper cakeTagMapper, RoleService roleService) {
        this.cakeTagMapper = cakeTagMapper;
        this.roleService = roleService;
    }

    public PageResponse<CakeTag> getCakeTagList(Integer pageNum, Integer pageSize) {
        CakeTagExample cakeTagExample = new CakeTagExample();
        cakeTagExample.setOrderByClause("`CREATED_AT` DESC");
        long count = cakeTagMapper.countByExample(cakeTagExample);
        long totalPage = count % pageSize == 0 ? count / pageSize : count / pageSize + 1;

        PageHelper.startPage(pageNum, pageSize);
        List<CakeTag> cakes = cakeTagMapper.selectByExample(cakeTagExample);

        return PageResponse.pageData(pageNum, pageSize, totalPage, cakes);
    }

    public CakeTag insertCakeTag(CakeTag tag, long roleId) {
        checkAuthorization(roleId);
        tag.setCreatedAt(new Date());
        tag.setUpdatedAt(new Date());
        tag.setDeleted(false);
        cakeTagMapper.insert(tag);
        return tag;
    }

    public CakeTag updateCakeTag(CakeTag tag, long roleId) {
        checkAuthorization(roleId);

        tag.setUpdatedAt(new Date());
        cakeTagMapper.updateByPrimaryKeySelective(tag);
        return tag;
    }

    public CakeTag deleteCakeTagByCakeId(long cakeTagId, long roleId) {
        checkAuthorization(roleId);

        CakeTag cakeSelective = new CakeTag();
        cakeSelective.setDeleted(true);
        cakeSelective.setId(cakeTagId);
        cakeSelective.setUpdatedAt(new Date());
        cakeTagMapper.updateByPrimaryKeySelective(cakeSelective);
        return cakeSelective;
    }

    private void checkAuthorization(long roleId) {
        if(!"admin".equals(roleService.getUserRoleById(roleId).getName())) throw HttpException.forbidden("没有权限");
    }
}
