package com.westee.cake.controller;

import com.westee.cake.entity.PageResponse;
import com.westee.cake.entity.Response;
import com.westee.cake.generate.GoodsTypes;
import com.westee.cake.service.GoodsTypeService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.*;

@RestController
@RequestMapping("/api/v1/")
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
        return Response.of("创建成功", goodsType);
    }

    @GetMapping("/goods-type")
    public PageResponse<GoodsTypes> getShop(@RequestParam(name = "pageNum", defaultValue = "1", required = false) Integer pageNum,
                                       @RequestParam(name = "pageSize", defaultValue = "10", required = false) Integer pageSize,
                                       @RequestParam(name = "shop-id") Long shopId) {

        return goodsTypeService.getGoodsTypesByShopId(pageNum, pageSize, shopId);
    }

    @PatchMapping("/goods-type")
    public Response<GoodsTypes> updateGoodsTypes(@RequestBody GoodsTypes goodsTypes) {
        return Response.of("更新成功",goodsTypeService.updateGoodsTypes(goodsTypes));
    }

    @DeleteMapping("/goods-type")
    public Response<GoodsTypes> deleteGoodsTypes(@RequestBody GoodsTypes goodsTypes) {
        return Response.of("删除成功", goodsTypeService.deleteGoodsTypes(goodsTypes)) ;
    }
}
