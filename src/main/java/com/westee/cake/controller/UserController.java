package com.westee.cake.controller;

import com.auth0.jwt.exceptions.JWTVerificationException;
import com.westee.cake.entity.Response;
import com.westee.cake.entity.ResponseMessage;
import com.westee.cake.exceptions.HttpException;
import com.westee.cake.generate.User;
import com.westee.cake.realm.JWTUtil;
import com.westee.cake.service.UserService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PatchMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestHeader;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import java.util.Objects;

@RestController
@RequestMapping("/api/v1/")
public class UserController {
    private final UserService userService;

    @Autowired
    public UserController(UserService userService) {
        this.userService = userService;
    }

    @PatchMapping("user")
    public Response<User> updateUser(@RequestBody User user, @RequestHeader("Token") String token ) {
        String openid = JWTUtil.getUsername(token);
        User byOpenid = userService.getByOpenid(openid);
        if (Objects.equals(user.getId(), null) || !Objects.equals(byOpenid.getId(), user.getId())) {
            throw HttpException.forbidden("没有权限");
        }
        return Response.of(ResponseMessage.OK.toString(), userService.updateUser(user)) ;
    }

    @GetMapping("user")
    public Response<User> getUser(@RequestHeader("Token") String token) {
        try {
            JWTUtil.verify(token);
        } catch (JWTVerificationException e) {
            throw  HttpException.notAuthorized("认证失败");
        }
        String openid = JWTUtil.getUsername(token);
        User byOpenid = userService.getByOpenid(openid);
        return Response.of(ResponseMessage.OK.toString(), byOpenid);
    }
}
