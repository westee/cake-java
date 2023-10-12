package com.westee.cake.controller;

import com.westee.cake.entity.Response;
import com.westee.cake.generate.ExpressGoodsType;
import com.westee.cake.service.ExpressGoodsTypeService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import java.util.List;

@RestController
@RequestMapping("api/v1/")
public class ExpressGoodsTypeController {

    private final ExpressGoodsTypeService expressGoodsTypeService;

    @Autowired
    public ExpressGoodsTypeController(ExpressGoodsTypeService expressGoodsTypeService) {
        this.expressGoodsTypeService = expressGoodsTypeService;
    }

    @GetMapping("expressGoodsType")
    public Response<List<ExpressGoodsType>> getExpressGoodsTypeList() {
        return Response.ok(expressGoodsTypeService.getExpressGoodsTypes());
    }
}
