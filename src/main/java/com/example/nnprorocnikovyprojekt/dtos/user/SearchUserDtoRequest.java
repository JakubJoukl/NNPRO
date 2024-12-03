package com.example.nnprorocnikovyprojekt.dtos.user;

import com.example.nnprorocnikovyprojekt.dtos.pageinfo.PageInfoDtoRequest;
import jakarta.validation.constraints.NotNull;

public class SearchUserDtoRequest {
    @NotNull
    private PageInfoDtoRequest pageInfo;

    @NotNull
    private String username;

    public PageInfoDtoRequest getPageInfo() {
        return pageInfo;
    }

    public void setPageInfo(PageInfoDtoRequest pageInfo) {
        this.pageInfo = pageInfo;
    }

    public String getUsername() {
        return username;
    }

    public void setUsername(String username) {
        this.username = username;
    }
}
