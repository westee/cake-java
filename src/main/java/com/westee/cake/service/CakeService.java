package com.westee.cake.service;

import com.github.pagehelper.PageHelper;
import com.westee.cake.dao.MyCakeWithTagMapper;
import com.westee.cake.entity.CakeWithTag;
import com.westee.cake.entity.PageResponse;
import com.westee.cake.exceptions.HttpException;
import com.westee.cake.generate.Cake;
import com.westee.cake.generate.CakeExample;
import com.westee.cake.generate.CakeMapper;
import com.westee.cake.generate.CakeTagMapping;
import com.westee.cake.generate.CakeTagMappingExample;
import com.westee.cake.generate.CakeTagMappingMapper;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.util.ArrayList;
import java.util.Date;
import java.util.List;
import java.util.stream.Collectors;

@Service
public class CakeService {
    private final CakeMapper cakeMapper;
    private final CakeTagMappingMapper cakeTagMappingMapper;
    private final RoleService roleService;
    private final MyCakeWithTagMapper myCakeWithTagMapper;

    @Autowired
    public CakeService(CakeMapper cakeMapper, RoleService roleService, CakeTagMappingMapper cakeTagMappingMapper,
                       MyCakeWithTagMapper myCakeWithTagMapper) {
        this.cakeMapper = cakeMapper;
        this.roleService = roleService;
        this.cakeTagMappingMapper = cakeTagMappingMapper;
        this.myCakeWithTagMapper = myCakeWithTagMapper;
    }

    public PageResponse<CakeWithTag> getCakeList(Integer pageNum, Integer pageSize) {
        CakeExample cakeExample = new CakeExample();
        cakeExample.setOrderByClause("`CREATED_AT` DESC");
        long count = cakeMapper.countByExample(cakeExample);
        long totalPage = count % pageSize == 0 ? count / pageSize : count / pageSize + 1;

        PageHelper.startPage(pageNum, pageSize);
        List<Cake> cakes = cakeMapper.selectByExample(cakeExample);
        List<CakeWithTag> collect = cakes.stream().map(cake -> {
            Long id = cake.getId();
            return myCakeWithTagMapper.selectCakeWithTagsByCakeId(id);
        }).collect(Collectors.toList());

        return PageResponse.pageData(pageNum, pageSize, totalPage, collect);
    }

    public PageResponse<CakeWithTag> getCakeByCakeTag(Integer pageNum, Integer pageSize, Integer categoryId) {
        CakeTagMappingExample cakeTagMappingExample = new CakeTagMappingExample();
        cakeTagMappingExample.createCriteria().andTagIdEqualTo(String.valueOf(categoryId));
        PageHelper.startPage(pageNum, pageSize);
        List<CakeTagMapping> cakeTagMappings = cakeTagMappingMapper.selectByExample(cakeTagMappingExample);
        ArrayList<Cake> cakes = new ArrayList<>();
        cakeTagMappings.forEach(cakeTagMapping -> cakes.add(cakeMapper.selectByPrimaryKey(Long.valueOf(cakeTagMapping.getCakeId()))));

        long count = cakeTagMappingMapper.countByExample(cakeTagMappingExample);
        long totalPage = count % pageSize == 0 ? count / pageSize : count / pageSize + 1;

        List<CakeWithTag> collect = cakes.stream().map(cake -> {
            Long id = cake.getId();
            return myCakeWithTagMapper.selectCakeWithTagsByCakeId(id);
        }).collect(Collectors.toList());

        return PageResponse.pageData(pageNum, pageSize, totalPage, collect);
    }

    public PageResponse<CakeWithTag> getCakeByCakeName(Integer pageNum, Integer pageSize, String keyword) {
        // 计算分页偏移量
        int offset = (pageNum - 1) * pageSize;
        Integer count = myCakeWithTagMapper.countByNameLike(keyword);
        int totalPage = count % pageSize == 0 ? count / pageSize : count / pageSize + 1;
        List<CakeWithTag> cakeWithTags = myCakeWithTagMapper.selectByNameLike(keyword, pageSize, offset);

        return PageResponse.pageData(pageNum, pageSize, totalPage, cakeWithTags);
    }

    public Cake insertCake(CakeWithTag cake, long roleId) {
        checkAuthorization(roleId);
        cake.setCreatedAt(new Date());
        cake.setUpdatedAt(new Date());
        cake.setDeleted(false);
        cakeMapper.insert(cake);

        insertCakeTagMapping(cake);
        return cake;
    }

    public Cake updateCake(CakeWithTag cake, long roleId) {
        checkAuthorization(roleId);
        cake.setUpdatedAt(new Date());
        cakeMapper.updateByPrimaryKeySelective(cake);

        // 删除旧的cake tag mapping
        CakeTagMappingExample cakeTagMappingExample = new CakeTagMappingExample();
        cakeTagMappingExample.createCriteria().andCakeIdEqualTo(String.valueOf(cake.getId()));
        cakeTagMappingMapper.deleteByExample(cakeTagMappingExample);

        // 插入新的cake tag mapping
        insertCakeTagMapping(cake);

        return cake;
    }

    public void insertCakeTagMapping (CakeWithTag cake) {
        // 插入新的cake tag mapping
        cake.getTags().forEach(tag -> {
            CakeTagMapping cakeTagMapping = new CakeTagMapping();
            cakeTagMapping.setCakeId(cake.getId().toString());
            cakeTagMapping.setTagId(tag.getId().toString());
            cakeTagMapping.setDeleted(false);
            cakeTagMapping.setCreatedAt(new Date());
            cakeTagMapping.setUpdatedAt(new Date());
            cakeTagMappingMapper.insert(cakeTagMapping);
        });
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
        if (!"admin".equals(roleService.getUserRoleById(roleId).getName())) throw HttpException.forbidden("没有权限");
    }
}
