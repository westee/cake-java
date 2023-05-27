package com.westee.cake.controller;

import com.westee.cake.entity.GoodsWithImages;
import com.westee.cake.entity.PageResponse;
import com.westee.cake.entity.Response;
import com.westee.cake.entity.ResponseMessage;
import com.westee.cake.generate.Goods;
import com.westee.cake.service.GoodsService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.*;

@RestController
@RequestMapping("api/v1/")
public class GoodsController {
    private final GoodsService goodsService;

    @Autowired
    public GoodsController(GoodsService goodsService) {
        this.goodsService = goodsService;
    }

    @GetMapping("/goods")
    public PageResponse<GoodsWithImages> getShop(@RequestParam(name = "pageNum", defaultValue = "1", required = false) Integer pageNum,
                                       @RequestParam(name = "pageSize", defaultValue = "10", required = false) Integer pageSize,
                                       @RequestParam(name = "shopId") Long shopId) {

        return goodsService.getGoodsByShopId(pageNum, pageSize, shopId);
    }

    @GetMapping("/goods/{goodsId}")
    public Response<Goods> getShopByShopId(@PathVariable(name = "goodsId") long goodsId) {
        return Response.of(ResponseMessage.OK.toString(), goodsService.getGoodsByGoodsId(goodsId));
    }

    @PostMapping("/goods")
    public Response<Goods> createGoods(@RequestBody GoodsWithImages goods) {
        return Response.of(ResponseMessage.OK.toString(), goodsService.createGoods(goods));
    }

    @DeleteMapping("/goods/{goodsId}")
    public Response<Goods> deleteGoods(@PathVariable Long goodsId) {
        return Response.of(ResponseMessage.OK.toString(), goodsService.deleteGoods(goodsId));
    }

    @PatchMapping("/goods")
    public Response<GoodsWithImages> updateGoods(@RequestBody GoodsWithImages goods) {
        return Response.of(ResponseMessage.OK.toString(), goodsService.updateGoods(goods));
    }

}
