package com.westee.cake.controller;

import com.westee.cake.entity.PageResponse;
import com.westee.cake.entity.Response;
import com.westee.cake.generate.Shop;
import com.westee.cake.service.ShopService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.web.bind.annotation.*;

import javax.servlet.http.HttpServletResponse;

@RestController
@RequestMapping("/api/v1")
public class ShopController {
    private final ShopService shopService;

    @Autowired
    public ShopController(ShopService shopService) {
        this.shopService = shopService;
    }

    @GetMapping("/shop")
    public PageResponse<Shop> getShop(@RequestParam(name = "pageNum", defaultValue = "1", required = false) Integer pageNum,
                                      @RequestParam(name = "pageSize", defaultValue = "10", required = false) Integer pageSize) {

        return shopService.getShopsByUserId(pageNum, pageSize);
    }

    @GetMapping("/shop/{shopId}")
    public Response<Shop> getShopByShopId(@PathVariable(name = "shopId", required = false) long shopId) {
        return Response.of("ok", shopService.getShopByShopId(shopId));
    }

    @PostMapping("/shop")
    public Response<Shop> createShop(@RequestBody Shop shop, HttpServletResponse response) {
        Response<Shop> ret = Response.of(shopService.createShop(shop));
        response.setStatus(HttpStatus.CREATED.value());
        return ret;
    }

    @PatchMapping("/shop/{id}")
    public Response<Shop> updateShop(@PathVariable("id") Long id,
                                     @RequestBody Shop shop) {
        shop.setId(id);
        return Response.of(shopService.updateShop(shop));
    }

    @DeleteMapping("/shop/{id}")
    public Response<Shop> deleteShop(@PathVariable("id") Long shopId) {
        return Response.of(shopService.deleteShop(shopId));

    }
}
