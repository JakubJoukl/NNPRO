package com.example.nnprorocnikovyprojekt.dtos.pageinfo;

import jakarta.validation.constraints.NotNull;

public class PageInfoRequestWrapper {
    @NotNull
    private PageInfoDtoRequest pageInfo = new PageInfoDtoRequest();

    public Integer getPageSize() {
        return pageInfo.getPageSize();
    }

    public void setPageSize(Integer pageSize) {
        this.pageInfo.setPageSize(pageSize);
    }

    public Integer getPageIndex() {
        return pageInfo.getPageIndex();
    }

    public void setPageIndex(Integer pageIndex) {
        this.pageInfo.setPageIndex(pageIndex);
    }

    public PageInfoDtoRequest getPageInfo() {
        return pageInfo;
    }

    public void setPageInfo(PageInfoDtoRequest pageInfo) {
        this.pageInfo = pageInfo;
    }
}
