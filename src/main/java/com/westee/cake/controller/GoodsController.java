package com.westee.cake.controller;

import com.westee.cake.entity.GoodsWithImages;
import com.westee.cake.entity.PageResponse;
import com.westee.cake.entity.Response;
import com.westee.cake.entity.ResponseMessage;
import com.westee.cake.generate.Goods;
import com.westee.cake.service.GoodsService;
import com.westee.cake.service.UserService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequestMapping("api/v1/")
public class GoodsController {
    private final GoodsService goodsService;
    private final UserService userService;

    @Autowired
    public GoodsController(GoodsService goodsService, UserService userService) {
        this.goodsService = goodsService;
        this.userService = userService;
    }

    @GetMapping("/goods")
    public PageResponse<GoodsWithImages> getShop(@RequestParam(name = "pageNum", defaultValue = "1", required = false) Integer pageNum,
                                                 @RequestParam(name = "pageSize", defaultValue = "10", required = false) Integer pageSize,
                                                 @RequestParam(name = "status", defaultValue = "ok", required = false) String status,
                                                 @RequestParam(name = "shopId") Long shopId) {

        return goodsService.getGoodsByShopIdAndCategoryId(pageNum, pageSize, shopId, status);
    }

    @GetMapping("/goods-category")
    public Response<List<GoodsWithImages>> getGoodsByCategoryAndShop(
            @RequestParam(name = "shopId") Long shopId,
            @RequestParam(name = "categoryId") Long categoryId) {

        return Response.of(ResponseMessage.OK.toString() ,goodsService.getGoodsByShopIdAndCategoryId(shopId, categoryId)) ;
    }

    @GetMapping("/goods/{goodsId}")
    public Response<Goods> getShopByShopId(@PathVariable(name = "goodsId") long goodsId,
                                           @RequestHeader("Token") String token) {
        return Response.of(ResponseMessage.OK.toString(), goodsService.getGoodsByGoodsId(goodsId));
    }

    @PostMapping("/goods")
    public Response<Goods> createGoods(@RequestBody GoodsWithImages goods,
                                       @RequestHeader("Token") String token) {
        Long userId = userService.getUserByToken(token).getId();
        return Response.of(ResponseMessage.OK.toString(), goodsService.createGoods(goods, userId));
    }

    @DeleteMapping("/goods/{goodsId}")
    public Response<Goods> deleteGoods(@PathVariable Long goodsId,
                                       @RequestHeader("Token") String token) {
        Long userId = userService.getUserByToken(token).getId();
        return Response.of(ResponseMessage.OK.toString(), goodsService.deleteGoods(goodsId, userId));
    }

    @PatchMapping("/goods")
    public Response<GoodsWithImages> updateGoods(@RequestBody GoodsWithImages goods,
                                                 @RequestHeader("Token") String token) {
        Long userId = userService.getUserByToken(token).getId();
        return Response.of(ResponseMessage.OK.toString(), goodsService.updateGoods(goods, userId));
    }

    @GetMapping("/goods/search")
    public PageResponse<Goods> updateGoods(@RequestParam(name = "pageNum", defaultValue = "1", required = false) Integer pageNum,
                                           @RequestParam(name = "pageSize", defaultValue = "10", required = false) Integer pageSize,
                                           @RequestParam String goodsName) {
        int start = (pageNum - 1) * pageSize;
        int count = goodsService.countGoodsByName(goodsName);
        int totalPage = count % pageSize == 0 ? count / pageSize : count / pageSize + 1;
        return PageResponse.pageData(pageNum,pageSize, totalPage, goodsService.getGoodsByName(start, pageSize, goodsName));
    }

}
