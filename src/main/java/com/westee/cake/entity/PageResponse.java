package com.westee.cake.entity;

import java.util.List;

public class PageResponse<T> {
    int pageNum;
    int pageSize;
    long totalPage;
    List<T> data;

    public PageResponse() {}

    public PageResponse(int pageNum, int pageSize, long count, List<T> data) {
        this.pageNum = pageNum;
        this.pageSize = pageSize;
        this.totalPage = count;
        this.data = data;
    }

    public int getPageNum() {
        return pageNum;
    }

    public int getPageSize() {
        return pageSize;
    }

    public long getTotalPage() {
        return totalPage;
    }

    public void setPageNum(int pageNum) {
        this.pageNum = pageNum;
    }

    public void setPageSize(int pageSize) {
        this.pageSize = pageSize;
    }

    public void setTotalPage(long totalPage) {
        this.totalPage = totalPage;
    }

    public void setData(List<T> data) {
        this.data = data;
    }

    public List<T> getData() {
        return data;
    }

    public static <T> PageResponse<T> pageData(Integer pageNum, Integer pageSize, long count, List<T> data) {
        return new PageResponse<>(pageNum, pageSize, count, data);
    }
}