package com.westee.cake.controller;

import com.westee.cake.entity.PageResponse;
import com.westee.cake.entity.Response;
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
    public PageResponse<Goods> getShop(@RequestParam(name = "pageNum", defaultValue = "1", required = false) Integer pageNum,
                                       @RequestParam(name = "pageSize", defaultValue = "10", required = false) Integer pageSize,
                                       @RequestParam(name = "shopId") Long shopId) {

        return goodsService.getGoodsByShopId(pageNum, pageSize, shopId);
    }

    @GetMapping("/goods/{goodsId}")
    public Response<Goods> getShopByShopId(@PathVariable(name = "goodsId") long goodsId) {
        return Response.of("ok", goodsService.getGoodsByGoodsId(goodsId));
    }

    @PostMapping("/goods")
    public Response<Goods> createGoods(@RequestBody Goods goods) {
        return Response.of(goodsService.createGoods(goods));
    }

    @DeleteMapping("/goods/{goodsId}")
    public Response<Goods> deleteGoods(@PathVariable Long goodsId) {
        Response<Goods> ret = Response.of(goodsService.deleteGoods(goodsId));
        return ret;
    }

    @PatchMapping("/goods")
    public Response<Goods> updateGoods(@RequestBody Goods goods) {
        Response<Goods> ret = Response.of(goodsService.updateGoods(goods));
        return ret;
    }

}
