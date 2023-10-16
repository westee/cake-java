package com.westee.cake.controller;

import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.westee.cake.entity.Response;
import com.westee.cake.exceptions.HttpException;
import jakarta.servlet.http.HttpServletResponse;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.ControllerAdvice;
import org.springframework.web.bind.annotation.ExceptionHandler;
import org.springframework.web.bind.annotation.ResponseBody;
import org.springframework.web.multipart.MultipartException;

/**
 * @ControllerAdvice 注解用于定义全局异常处理的类，其作用是捕获控制器中发生的异常并进行统一处理。
 * 在 Spring MVC 的应用中，当控制器中抛出异常时，Spring MVC 会自动匹配对应的全局异常处理类并执行相关代码逻辑。
 * <p>
 * ErrorHandlingController 类定义了一个 @ExceptionHandler 方法，它会捕获 HttpException 异常，
 * 并返回自定义的响应格式。具体来说，当控制器中抛出 HttpException 异常时，该方法会将 HTTP 响应码设置为 HttpException 中定义的状态码，
 * 同时设置响应内容为 JSON 格式的 Response 对象（其中包括异常信息和数据），从而实现了统一的异常处理。
 *
 * 通过使用 @ControllerAdvice 注解，可以集中管理异常处理逻辑，提高代码复用性和可维护性，同时也能更好地保护系统安全，防止敏感信息泄露和攻击。
 */

@ControllerAdvice
public class ErrorHandlingController {
    @ExceptionHandler(HttpException.class)
    public @ResponseBody
    Response<?> onError(HttpServletResponse response, HttpException e) {
        response.setStatus(e.getStatusCode());
        return Response.of(e.getMessage(), null);
    }

    @ExceptionHandler(MultipartException.class)
    public ResponseEntity<String> handleMultipartException(MultipartException e) throws JsonProcessingException {
        Response<Object> response = Response.of("文件大小超过限制", null);
        ObjectMapper objectMapper = new ObjectMapper();
        String s = objectMapper.writeValueAsString(response);
        System.out.println(s);
        return ResponseEntity.status(HttpStatus.BAD_REQUEST).body(s);
    }
}
