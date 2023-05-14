package com.westee.cake.controller;

import com.westee.cake.entity.Response;
import com.westee.cake.generate.GoodsTypes;
import com.westee.cake.service.GoodsTypeService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;

@RestController("/api/v1/")
public class GoodsTypeController {
    private final GoodsTypeService goodsTypeService;

    @Autowired
    public GoodsTypeController(GoodsTypeService goodsTypeService) {
        this.goodsTypeService = goodsTypeService;
    }

    @PostMapping("goods-type")
    public Response<GoodsTypes> createGoodsType(@RequestParam("typename") String typeName,
                                                @RequestParam("shop-id") Long shopId) {
        GoodsTypes goodsType = goodsTypeService.createGoodsType(typeName, shopId);
        return Response.of("", goodsType);
    }
}
