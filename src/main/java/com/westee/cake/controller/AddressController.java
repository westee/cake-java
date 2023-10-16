package com.westee.cake.controller;

import com.westee.cake.entity.PageResponse;
import com.westee.cake.entity.Response;
import com.westee.cake.generate.Address;
import com.westee.cake.service.AddressService;
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
public class AddressController {
    private final AddressService addressService;
    private final UserService userService;

    @Autowired
    public AddressController(AddressService addressService, UserService userService) {
        this.addressService = addressService;
        this.userService = userService;
    }

    @GetMapping("/address")
    public PageResponse<Address> getShop(@RequestParam(name = "pageNum", defaultValue = "1", required = false) Integer pageNum,
                                         @RequestParam(name = "pageSize", defaultValue = "10", required = false) Integer pageSize,
                                         @RequestHeader("Token") String token) {
        Long tokenUserId = userService.getUserByToken(token).getId();
        return addressService.getAddressListByShopId(pageNum, pageSize, tokenUserId);
    }

    @GetMapping("/address/{addressId}")
    public Response<Address> getShopByAddressId(@PathVariable(name = "addressId") long addressId,
                                                @RequestHeader("Token") String token) {
        Long tokenUserId = userService.getUserByToken(token).getId();
        return Response.ok(addressService.getAddressById(addressId, tokenUserId));
    }

    @GetMapping("/address/default")
    public Response<Address> getAddressDefault(@RequestHeader("Token") String token) {
        Long tokenUserId = userService.getUserByToken(token).getId();
        return Response.ok(addressService.getAddressDefaultOrNewest(tokenUserId));
    }

    @PostMapping("/address")
    public Response<Address> createAddress(@RequestBody Address address,
                                           HttpServletResponse response,
                                           @RequestHeader("Token") String token) {
        Long tokenUserId = userService.getUserByToken(token).getId();
        Response<Address> ret = Response.ok(addressService.createAddress(address, tokenUserId));
        response.setStatus(HttpStatus.CREATED.value());
        return ret;
    }

    @PatchMapping("/address/{id}")
    public Response<Address> updateAddress(@PathVariable("id") Long id,
                                           @RequestBody Address address,
                                           @RequestHeader("Token") String token) {
        Long tokenUserId = userService.getUserByToken(token).getId();
        address.setId(id);
        return Response.ok(addressService.updateAddress(address, tokenUserId));
    }

    @DeleteMapping("/address/{id}")
    public Response<Address> deleteAddress(@PathVariable("id") Long addressId,
                                           @RequestHeader("Token") String token) {
        Long tokenUserId = userService.getUserByToken(token).getId();
        return Response.ok(addressService.deleteAddress(addressId, tokenUserId));

    }

}
