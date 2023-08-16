package com.westee.cake.controller;

import com.westee.cake.entity.CakeWithTag;
import com.westee.cake.entity.PageResponse;
import com.westee.cake.entity.Response;
import com.westee.cake.generate.Cake;
import com.westee.cake.service.CakeService;
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

@RestController
@RequestMapping("/api/v1")
public class CakeController {
    private final CakeService cakeService;
    private final UserService userService;

    @Autowired
    public CakeController(CakeService cakeService, UserService userService) {
        this.cakeService = cakeService;
        this.userService = userService;
    }

    @GetMapping("cake")
    public PageResponse<CakeWithTag> getCakeList(@RequestParam(name = "pageNum", required = false, defaultValue = "1") Integer pageNum,
                                                 @RequestParam(name = "pageSize", required = false, defaultValue = "10") Integer pageSize,
                                                 @RequestParam(name = "categoryId", required = false) Integer categoryId,
                                                 @RequestParam(name = "name", required = false) String name) {
        if (categoryId != null) { // categoryId参数已传递
            return cakeService.getCakeByCakeTag(pageNum, pageSize, categoryId);
        }if (name != null) { // categoryId参数已传递
            return cakeService.getCakeByCakeName(pageNum, pageSize, name);
        } else {
            return cakeService.getCakeList(pageNum, pageSize);
        }
    }

    @PostMapping("cake")
    public Response<Cake> createCake(@RequestBody CakeWithTag cake,
                                     @RequestHeader("Token") String token) {
        Long roleId = userService.getUserByToken(token).getRoleId();
        return Response.ok(cakeService.insertCake(cake, roleId));
    }

    @PatchMapping("cake")
    public Response<Cake> patchCake(@RequestBody CakeWithTag cake,
                                    @RequestHeader("Token") String token) {
        Long roleId = userService.getUserByToken(token).getRoleId();
        return Response.ok(cakeService.updateCake(cake, roleId));
    }

    @DeleteMapping("cake")
    public Response<Cake> deleteCake(@RequestParam long cakeId,
                                     @RequestHeader("Token") String token) {
        Long roleId = userService.getUserByToken(token).getRoleId();
        return Response.ok(cakeService.deleteCakeByCakeId(cakeId, roleId));
    }
}
