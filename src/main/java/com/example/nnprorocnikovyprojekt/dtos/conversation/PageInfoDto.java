package com.example.nnprorocnikovyprojekt.dtos.conversation;

public class PageInfoDto {
    private Integer pageSize;

    private Integer pageIndex;

    private Integer total;

    public PageInfoDto() {
    }

    public PageInfoDto(Integer pageSize, Integer pageIndex, Integer total) {
        this.pageSize = pageSize;
        this.pageIndex = pageIndex;
        this.total = total;
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

    public Integer getTotal() {
        return total;
    }

    public void setTotal(Integer total) {
        this.total = total;
    }
}
