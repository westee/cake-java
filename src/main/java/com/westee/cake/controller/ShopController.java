package com.westee.cake.controller;

import com.westee.cake.entity.PageResponse;
import com.westee.cake.entity.Response;
import com.westee.cake.entity.ResponseMessage;
import com.westee.cake.generate.Shop;
import com.westee.cake.service.ShopService;
import com.westee.cake.service.UserService;
import jakarta.servlet.http.HttpServletResponse;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.web.bind.annotation.DeleteMapping;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PatchMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestHeader;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;

@RestController
@RequestMapping("/api/v1")
public class ShopController {
    private final ShopService shopService;
    private final UserService userService;

    @Autowired
    public ShopController(ShopService shopService, UserService userService) {
        this.shopService = shopService;
        this.userService = userService;
    }

    @GetMapping("/shop")
    public PageResponse<Shop> getShop(@RequestParam(name = "pageNum", defaultValue = "1", required = false) Integer pageNum,
                                      @RequestParam(name = "pageSize", defaultValue = "10", required = false) Integer pageSize,
                                      @RequestHeader("Token") String token) {
        Long userId = userService.getUserByToken(token).getId();
        return shopService.getShopsByUserId(pageNum, pageSize, userId);
    }

    @GetMapping("/shop/{shopId}")
    public Response<Shop> getShopByShopId(@PathVariable(name = "shopId", required = false) long shopId) {
        return Response.of(ResponseMessage.OK.toString(), shopService.getShopByShopId(shopId));
    }

    @PostMapping("/shop")
    public Response<Shop> createShop(@RequestBody Shop shop, HttpServletResponse response,
                                     @RequestHeader("Token") String token) {
        Long userId = userService.getUserByToken(token).getId();

        Response<Shop> ret = Response.of(ResponseMessage.OK.toString(), shopService.createShop(shop, userId));
        response.setStatus(HttpStatus.CREATED.value());
        return ret;
    }

    @PatchMapping("/shop/{id}")
    public Response<Shop> updateShop(@PathVariable("id") Long id,
                                     @RequestBody Shop shop,
                                     @RequestHeader("Token") String token) {
        shop.setId(id);
        Long userId = userService.getUserByToken(token).getId();
        return Response.of(ResponseMessage.OK.toString(),shopService.updateShop(shop, userId));
    }

    @DeleteMapping("/shop/{id}")
    public Response<Shop> deleteShop(@PathVariable("id") Long shopId,
                                     @RequestHeader("Token") String token) {
        Long userId = userService.getUserByToken(token).getId();
        return Response.of(ResponseMessage.OK.toString(), shopService.deleteShop(shopId, userId));
    }
}
