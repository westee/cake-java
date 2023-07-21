package com.westee.cake.controller;

import com.westee.cake.entity.Response;
import com.westee.cake.generate.ChargeOption;
import com.westee.cake.generate.User;
import com.westee.cake.service.ChargeOptionService;
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

import java.util.List;

@RestController
@RequestMapping("/api/v1/")
public class ChargeOptionController {
    private final ChargeOptionService chargeOptionService;
    private final UserService userService;

    @Autowired
    public ChargeOptionController(ChargeOptionService chargeOptionService, UserService userService) {
        this.chargeOptionService = chargeOptionService;
        this.userService = userService;
    }

    @GetMapping("charge_option")
    public Response<List<ChargeOption>> getChargeOptionList () {
       return Response.ok(chargeOptionService.getChargeOptionList());
    }

    @PostMapping("charge_option")
    public Response<ChargeOption> postChargeOptionList (@RequestBody ChargeOption chargeOption,
                                                        @RequestParam long shopId,
                                                        @RequestHeader("Token") String token) {
        User userByToken = userService.getUserByToken(token);
        return Response.ok(chargeOptionService.createChargeOption(chargeOption, shopId, userByToken.getId()));
    }

    @DeleteMapping("charge_option")
    public Response<ChargeOption> deleteChargeOptionList (@RequestParam int chargeOptionId,
                                                          @RequestHeader("Token") String token) {
        User userByToken = userService.getUserByToken(token);
        return Response.ok(chargeOptionService.deleteChargeOption(chargeOptionId, userByToken.getId()));
    }

    @PatchMapping("charge_option")
    public Response<ChargeOption> patchChargeOptionList (@RequestBody ChargeOption chargeOption,
                                                         @RequestHeader("Token") String token) {
        User userByToken = userService.getUserByToken(token);
        return Response.ok(chargeOptionService.updateChargeOption(chargeOption, userByToken.getId()));
    }
}
