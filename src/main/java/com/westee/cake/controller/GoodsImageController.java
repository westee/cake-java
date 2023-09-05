package com.westee.cake.controller;

import com.westee.cake.entity.Response;
import com.westee.cake.service.GoodsImageService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.DeleteMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestHeader;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

@RestController
@RequestMapping("/api/v1/")
public class GoodsImageController {
    GoodsImageService goodsImageService;

    @Autowired
    public GoodsImageController(GoodsImageService goodsImageService) {
        this.goodsImageService = goodsImageService;
    }

    @DeleteMapping("goods-image/{imageName}")
    public Response<String> deleteGoodImage(@PathVariable String imageName,
                                            @RequestHeader("Token") String token){
        goodsImageService.deleteGoodsImage(imageName, token);
        return Response.ok("");
    }
}
