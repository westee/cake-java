package com.westee.cake.entity;

public class Response<T> {

    private String message;
    private T data;

    public static <T> Response<T> of(String message, T data) {
        return new Response<T>(message, data);
    }

    public static <T> Response<T> ok(T data) {
        return new Response<T>(ResponseMessage.OK.toString(), data);
    }

    public static <T> Response<T> of(T data) {
        return new Response<T>(null, data);
    }

    public Response(String message, T data) {
        this.message = message;
        this.data = data;
    }

    public Response() {
    }

    public String getMessage() {
        return message;
    }

    public T getData() {
        return data;
    }
}
