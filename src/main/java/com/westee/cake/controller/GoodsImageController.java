package com.westee.cake.controller;

import com.westee.cake.service.GoodsImageService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.DeleteMapping;
import org.springframework.web.bind.annotation.PathVariable;
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
    public void deleteGoodImage(@PathVariable String imageName){
        goodsImageService.deleteGoodsImage(imageName);
    }
}
