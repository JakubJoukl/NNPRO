package com.example.nnprorocnikovyprojekt.dtos.pageinfo;

public class PageInfoDtoRequest {
    private Integer pageSize;

    private Integer pageIndex;

    public PageInfoDtoRequest() {
    }

    public PageInfoDtoRequest(Integer pageSize, Integer pageIndex) {
        this.pageSize = pageSize;
        this.pageIndex = pageIndex;
    }

    public Integer getPageSize() {
        return pageSize;
    }

    public void setPageSize(Integer pageSize) {
        this.pageSize = pageSize;
    }

    public Integer getPageIndex() {
        return pageIndex;
    }

    public void setPageIndex(Integer pageIndex) {
        this.pageIndex = pageIndex;
    }
}
