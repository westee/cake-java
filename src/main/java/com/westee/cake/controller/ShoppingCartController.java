package com.westee.cake.controller;

import com.westee.cake.entity.AddToShoppingCartRequest;
import com.westee.cake.entity.PageResponse;
import com.westee.cake.entity.Response;
import com.westee.cake.entity.ResponseMessage;
import com.westee.cake.entity.ShoppingCartData;
import com.westee.cake.realm.JWTUtil;
import com.westee.cake.service.ShoppingCartService;
import com.westee.cake.service.UserService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.DeleteMapping;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestHeader;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;

@RestController
@RequestMapping("/api/v1/")
public class ShoppingCartController {
    ShoppingCartService shoppingCartService;
    UserService userService;

    @Autowired
    public ShoppingCartController(ShoppingCartService shoppingCartService, UserService userService) {
        this.shoppingCartService = shoppingCartService;
        this.userService = userService;
    }

    @GetMapping("shoppingCart")
    public PageResponse<ShoppingCartData> getShoppingCart(
            @RequestParam(name = "pageSize", required = false, defaultValue = "10") int pageSize,
            @RequestParam(name = "pageNum", required = false, defaultValue = "1") int pageNum,
            @RequestHeader("Token") String token) {
        String openid = JWTUtil.getUsername(token);
        Long userId = userService.getByOpenid(openid).getId();
        return shoppingCartService.getShoppingCartOfUser(pageNum, pageSize, userId);
    }

    @PostMapping("shoppingCart")
    public Response<ShoppingCartData> getShoppingCart(@RequestBody AddToShoppingCartRequest request,
                                                      @RequestHeader("Token") String token) {
        String openid = JWTUtil.getUsername(token);
        Long userId = userService.getByOpenid(openid).getId();
        return Response.of(shoppingCartService.addGoodsToShoppingCart(request, userId));
    }

    @DeleteMapping("shoppingCart/{goodsId}")
    public void deleteShoppingCart(@PathVariable("goodsId") long goodsId,
                                   @RequestHeader("Token") String token) {
        String openid = JWTUtil.getUsername(token);
        Long userId = userService.getByOpenid(openid).getId();
        shoppingCartService.deleteShoppingCart(goodsId, userId);
    }

    @GetMapping("shoppingCart/items/{goodsId}/count")
    public Response<Long> getCountByGoodsId(@PathVariable("goodsId") long goodsId,
                                            @RequestHeader("Token") String token) {
        String openid = JWTUtil.getUsername(token);
        Long id = userService.getByOpenid(openid).getId();
        long l = shoppingCartService.countByGoodsIdAndUserId(goodsId, id);
        return Response.of(ResponseMessage.OK.toString(), l);
    }

}
