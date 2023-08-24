package com.westee.cake.controller;

import com.westee.cake.entity.Response;
import com.westee.cake.generate.Swiper;
import com.westee.cake.service.SwiperService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.DeleteMapping;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PatchMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;

import java.util.List;

@RestController
@RequestMapping("api/v1/")
public class SwiperController {
    private final SwiperService swiperService;

    @Autowired
    public SwiperController(SwiperService swiperService) {
        this.swiperService = swiperService;
    }

    @GetMapping("/swiper")
    public Response<List<Swiper>> getShop(@RequestParam(name = "type", required = false) String type) {
        return Response.ok(swiperService.getSwiperByType(type));
    }

    @PostMapping("/swiper")
    public Response<Swiper> createSwiper(@RequestBody Swiper swiper) {
        return Response.ok(swiperService.createSwiper(swiper));
    }

    @PatchMapping("/swiper")
    public Response<Swiper> patchSwiper(@RequestBody Swiper swiper) {
        return Response.ok(swiperService.updateSwiper(swiper));
    }

    @DeleteMapping("/swiper/{swiperId}")
    public Response<Swiper> deleteSwiper(@PathVariable Integer swiperId) {
        return Response.ok(swiperService.deleteSwiper(swiperId));
    }

}
