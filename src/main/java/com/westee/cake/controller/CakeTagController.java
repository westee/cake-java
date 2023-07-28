package com.westee.cake.controller;

import com.westee.cake.entity.PageResponse;
import com.westee.cake.entity.Response;
import com.westee.cake.generate.CakeTag;
import com.westee.cake.service.CakeTagService;
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
public class CakeTagController {
    private final CakeTagService cakeTagService;
    private final UserService userService;

    @Autowired
    public CakeTagController(CakeTagService cakeTagService, UserService userService) {
        this.cakeTagService = cakeTagService;
        this.userService = userService;
    }

    @GetMapping("cake_tag")
    public PageResponse<CakeTag> getCakeList(@RequestParam(name = "pageNum", required = false, defaultValue = "1") Integer pageNum,
                                          @RequestParam(name = "pageSize", required = false, defaultValue = "10") Integer pageSize) {
        return cakeTagService.getCakeTagList(pageNum, pageSize);
    }

    @PostMapping("cake_tag")
    public Response<CakeTag> createCake(@RequestBody CakeTag tag,
                                        @RequestHeader("Token") String token) {
        Long roleId = userService.getUserByToken(token).getRoleId();
        return Response.ok(cakeTagService.insertCakeTag(tag, roleId));
    }

    @PatchMapping("cake_tag")
    public Response<CakeTag> patchCake(@RequestBody CakeTag tag,
                                       @RequestHeader("Token") String token) {
        Long roleId = userService.getUserByToken(token).getRoleId();
        return Response.ok(cakeTagService.updateCakeTag(tag, roleId));
    }

    @DeleteMapping("cake_tag")
    public Response<CakeTag> deleteCake(@RequestBody long cakeTagId,
                                        @RequestHeader("Token") String token) {
        Long roleId = userService.getUserByToken(token).getRoleId();
        return Response.ok(cakeTagService.deleteCakeTagByCakeId(cakeTagId, roleId));
    }

}
