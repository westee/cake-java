package com.westee.cake.controller;

import com.westee.cake.data.DataStatus;
import com.westee.cake.data.OrderInfo;
import com.westee.cake.entity.OrderResponse;
import com.westee.cake.entity.PageResponse;
import com.westee.cake.entity.Response;
import com.westee.cake.entity.ResponseMessage;
import com.westee.cake.exceptions.HttpException;
import com.westee.cake.generate.OrderTable;
import com.westee.cake.service.OrderService;
import com.westee.cake.service.UserService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.DeleteMapping;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestHeader;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestMethod;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;

@RestController
@RequestMapping("/api/v1")
public class OrderController {
    private final OrderService orderService;
    private final UserService userService;

    @Autowired
    public OrderController(OrderService orderService, UserService userService) {
        this.orderService = orderService;
        this.userService = userService;
    }

    /**
     * 分页获取订单
     *
     * @param pageNum  当前页码
     * @param pageSize 一页数量
     * @param status   订单状态
     * @return 结果
     */
    @GetMapping("/order")
    public PageResponse<OrderResponse> getOrder(@RequestParam(name = "pageNum", required = false, defaultValue = "1") Integer pageNum,
                                                @RequestParam(name = "pageSize", required = false, defaultValue = "10") Integer pageSize,
                                                @RequestParam(value = "status", required = false) String status,
                                                @RequestHeader("Token") String token) {
        if (status != null && DataStatus.fromStatus(status) == null) {
            throw HttpException.badRequest("非法status: " + status);
        }
        Long userId = userService.getUserByToken(token).getId();
        return orderService.getOrder(userId, pageNum, pageSize, DataStatus.fromStatus(status));
    }

    /**
     * 根据id获取订单
     *
     * @param id 订单id
     * @return 订单
     */
    @GetMapping("/order/{id}")
    public Response<OrderResponse> getOrderById(@PathVariable("id") long id,
                                                @RequestHeader("Token") String token) {
        Long userId = userService.getUserByToken(token).getId();
        return Response.of(ResponseMessage.OK.toString(), orderService.getOrderById(userId, id));
    }

    /**
     * @param orderInfo 订单信息
     * @return 响应
     */
    @PostMapping("/order")
    public Response<OrderResponse> createOrder(@RequestBody OrderInfo orderInfo,
                                               @RequestParam(required = false) Long couponId,
                                               @RequestHeader("Token") String token) throws RuntimeException {
        orderService.deductStock(orderInfo);
        Long userId = userService.getUserByToken(token).getId();
        return Response.of(ResponseMessage.OK.toString(), orderService.createOrder(orderInfo, userId, couponId));
    }

    /**
     * 更新订单
     *
     * @param id    订单id
     * @param order 订单信息
     * @return 更新后的订单
     */
    @RequestMapping(value = "/order/{id}", method = {RequestMethod.POST, RequestMethod.PATCH})
    public Response<OrderResponse> updateOrder(@PathVariable("id") long id, @RequestBody OrderTable order,
                                               @RequestHeader("Token") String token) {
        order.setId(id);
        Long userId = userService.getUserByToken(token).getId();
        if (order.getExpressCompany() != null) {
            return Response.of(ResponseMessage.OK.toString(), orderService.updateExpressInformation(order, userId));
        } else {
            return Response.of(ResponseMessage.OK.toString(), orderService.updateOrderStatus(order, userId));
        }
    }

    /**
     * 删除订单
     *
     * @param orderId 订单id
     * @return 删除后的订单
     */
    @DeleteMapping("/order/{id}")
    public Response<OrderResponse> deleteOrder(@PathVariable("id") long orderId,
                                               @RequestHeader("Token") String token) {
        Long userId = userService.getUserByToken(token).getId();
        return Response.of(ResponseMessage.OK.toString(), orderService.deleteOrder(orderId, userId));
    }
}