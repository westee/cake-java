package com.westee.cake.entity;

public class Result <T>{
    String status;
    String message;
    T data;

    public Result(String status, String msg) {
        this(status, msg, null);
    }

    public Result(String status, String msg,  T data) {
        this.status = status;
        this.message = msg;
        this.data = data;
    }

    public String getStatus() {
        return status;
    }

    public void setStatus(String status) {
        this.status = status;
    }

    public String getMessage() {
        return message;
    }

    public void setMessage(String msg) {
        this.message = msg;
    }

    public T getData() {
        return data;
    }

    public void setData(T data) {
        this.data = data;
    }
}
