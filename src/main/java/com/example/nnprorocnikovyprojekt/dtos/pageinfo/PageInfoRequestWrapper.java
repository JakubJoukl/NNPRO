package com.example.nnprorocnikovyprojekt.dtos.pageinfo;

public class PageInfoRequestWrapper {
    private PageInfoDtoRequest pageInfo;

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
}
