package com.example.nnprorocnikovyprojekt.dtos.user;

import com.example.nnprorocnikovyprojekt.dtos.pageinfo.PageInfoDtoRequest;
import jakarta.validation.constraints.NotNull;

import java.util.List;

public class SearchUserDtoRequest {
    @NotNull
    private PageInfoDtoRequest pageInfo;

    @NotNull
    private String username;

    private List<String> authorities;

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

    public List<String> getAuthorities() {
        return authorities;
    }

    public void setAuthorities(List<String> authorities) {
        this.authorities = authorities;
    }
}
