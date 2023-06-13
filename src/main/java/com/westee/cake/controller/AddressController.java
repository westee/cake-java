package com.westee.cake.controller;

import com.westee.cake.entity.PageResponse;
import com.westee.cake.entity.Response;
import com.westee.cake.entity.ResponseMessage;
import com.westee.cake.generate.Address;
import com.westee.cake.generate.User;
import com.westee.cake.realm.JWTUtil;
import com.westee.cake.service.AddressService;
import com.westee.cake.service.UserService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.web.bind.annotation.*;

import javax.servlet.http.HttpServletResponse;

@RestController
@RequestMapping("/api/v1")
public class AddressController {
    private final AddressService addressService;
    private final UserService userService;

    @Autowired
    public AddressController(AddressService addressService,UserService userService) {
        this.addressService = addressService;
        this.userService = userService;
    }

    @GetMapping("/address")
    public PageResponse<Address> getShop(@RequestParam(name = "pageNum", defaultValue = "1", required = false) Integer pageNum,
                                         @RequestParam(name = "pageSize", defaultValue = "10", required = false) Integer pageSize,
                                         @RequestHeader("Token") String token) {
        String openid = JWTUtil.getUsername(token);
        User byOpenid = userService.getByOpenid(openid);
        return addressService.getAddressListByShopId(pageNum, pageSize, byOpenid.getId());
    }

    @GetMapping("/address/{addressId}")
    public Response<Address> getShopByAddressId(@PathVariable(name = "addressId", required = false) long addressId) {
        return Response.of("ok", addressService.getAddressById(addressId));
    }

    @PostMapping("/address")
    public Response<Address> createAddress(@RequestBody Address address,
                                           HttpServletResponse response,
                                           @RequestHeader("Token") String token) {
        String openid = JWTUtil.getUsername(token);
        User byOpenid = userService.getByOpenid(openid);
        Response<Address> ret = Response.of(addressService.createAddress(address, byOpenid.getId()));
        response.setStatus(HttpStatus.CREATED.value());
        return ret;
    }

    @PatchMapping("/address/{id}")
    public Response<Address> updateAddress(@PathVariable("id") Long id,
                                           @RequestBody Address address,
                                           @RequestHeader("Token") String token) {
        String openid = JWTUtil.getUsername(token);
        User byOpenid = userService.getByOpenid(openid);
        address.setId(id);
        return Response.of(ResponseMessage.OK.toString(), addressService.updateAddress(address, byOpenid.getId()));
    }

    @DeleteMapping("/address/{id}")
    public Response<Address> deleteAddress(@PathVariable("id") Long addressId,
                                           @RequestHeader("Token") String token) {
        String openid = JWTUtil.getUsername(token);
        User byOpenid = userService.getByOpenid(openid);
        return Response.of(ResponseMessage.OK.toString(), addressService.deleteAddress(addressId, byOpenid.getId()));

    }

}
