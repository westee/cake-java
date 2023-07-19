package com.westee.cake.controller;

import com.westee.cake.entity.Response;
import com.westee.cake.entity.ResponseMessage;
import com.westee.cake.generate.GoodsTypes;
import com.westee.cake.service.GoodsTypeService;
import com.westee.cake.service.UserService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.DeleteMapping;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PatchMapping;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestHeader;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;

import java.util.List;

@RestController
@RequestMapping("/api/v1/")
public class GoodsTypeController {
    private final GoodsTypeService goodsTypeService;
    private final UserService userService;

    @Autowired
    public GoodsTypeController(GoodsTypeService goodsTypeService, UserService userService) {
        this.goodsTypeService = goodsTypeService;
        this.userService = userService;
    }

    @PostMapping("goods-type")
    public Response<GoodsTypes> createGoodsType(@RequestParam("typename") String typeName,
                                                @RequestParam("shop-id") Long shopId,
                                                @RequestHeader("Token") String token) {
        Long userId = userService.getUserByToken(token).getId();

        GoodsTypes goodsType = goodsTypeService.createGoodsType(typeName, shopId, userId);
        return Response.of(ResponseMessage.OK.toString(), goodsType);
    }

    @GetMapping("/goods-type")
    public Response<List<GoodsTypes>> getShop(@RequestParam(name = "shop-id") Long shopId) {
        return Response.of(ResponseMessage.OK.toString(), goodsTypeService.getGoodsTypesByShopId(shopId));
    }

    @PatchMapping("/goods-type")
    public Response<GoodsTypes> updateGoodsTypes(@RequestBody GoodsTypes goodsTypes,
                                                 @RequestHeader("Token") String token) {
        Long userId = userService.getUserByToken(token).getId();
        return Response.ok(goodsTypeService.updateGoodsTypes(goodsTypes, userId));
    }

    @DeleteMapping("/goods-type")
    public Response<GoodsTypes> deleteGoodsTypes(@RequestParam("id") Long id,
                                                 @RequestParam("ownerShopId") Long shopId,
                                                 @RequestHeader("Token") String token) {
        GoodsTypes goodsTypes = new GoodsTypes();
        goodsTypes.setOwnerShopId(shopId);
        goodsTypes.setId(id);
        Long userId = userService.getUserByToken(token).getId();

        return Response.of(ResponseMessage.OK.toString(), goodsTypeService.deleteGoodsTypes(goodsTypes, userId));
    }
}
