package com.westee.cake.service;

import com.westee.cake.generate.ExpressGoodsType;
import com.westee.cake.generate.ExpressGoodsTypeExample;
import com.westee.cake.generate.ExpressGoodsTypeMapper;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.util.List;

@Service
public class ExpressGoodsTypeService {
    private final ExpressGoodsTypeMapper expressGoodsTypeMapper;

    @Autowired
    public ExpressGoodsTypeService(ExpressGoodsTypeMapper expressGoodsTypeMapper) {
        this.expressGoodsTypeMapper = expressGoodsTypeMapper;
    }

    public List<ExpressGoodsType> getExpressGoodsTypes() {
        ExpressGoodsTypeExample expressGoodsTypeExample = new ExpressGoodsTypeExample();
        return expressGoodsTypeMapper.selectByExample(expressGoodsTypeExample);
    }
}
