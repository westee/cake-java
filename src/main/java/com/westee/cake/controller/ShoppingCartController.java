package com.westee.cake.controller;

import com.westee.cake.entity.AddToShoppingCartRequest;
import com.westee.cake.entity.PageResponse;
import com.westee.cake.entity.Response;
import com.westee.cake.entity.ShoppingCartData;
import com.westee.cake.service.ShoppingCartService;
import com.westee.cake.service.UserContext;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.DeleteMapping;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;

@RestController
@RequestMapping("/api/v1/")
public class ShoppingCartController {
    ShoppingCartService shoppingCartService;

    @Autowired
    public ShoppingCartController(ShoppingCartService shoppingCartService) {
        this.shoppingCartService = shoppingCartService;
    }

    @GetMapping("shoppingCart")
    public PageResponse<ShoppingCartData> getShoppingCart(
            @RequestParam(name = "pageSize", required = false, defaultValue = "10") int pageSize,
            @RequestParam(name = "pageNum", required = false, defaultValue = "1") int pageNum) {
        Long userId = UserContext.getCurrentUser().getId();
        return shoppingCartService.getShoppingCartOfUser(pageNum, pageSize, userId);
    }

    @PostMapping("shoppingCart")
    public Response<ShoppingCartData> getShoppingCart(@RequestBody AddToShoppingCartRequest request) {
        Long userId = UserContext.getCurrentUser().getId();
        return Response.of(shoppingCartService.addGoodsToShoppingCart(request, userId));
    }

    @DeleteMapping("shoppingCart/{goodsId}")
    public void deleteShoppingCart(@PathVariable("goodsId") long goodsId) {
        Long userId = UserContext.getCurrentUser().getId();
        shoppingCartService.deleteShoppingCart(goodsId, userId);
    }

}
