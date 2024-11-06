package com.example.nnprorocnikovyprojekt.dtos.user;

import com.example.nnprorocnikovyprojekt.dtos.pageinfo.PageInfoDtoRequest;

public class SearchUserDtoRequest {
    private PageInfoDtoRequest pageInfo;

    private String searchTerm;

    public PageInfoDtoRequest getPageInfo() {
        return pageInfo;
    }

    public void setPageInfo(PageInfoDtoRequest pageInfo) {
        this.pageInfo = pageInfo;
    }

    public String getSearchTerm() {
        return searchTerm;
    }

    public void setSearchTerm(String searchTerm) {
        this.searchTerm = searchTerm;
    }
}
