package com.example.nnprorocnikovyprojekt.dtos.user;

import com.example.nnprorocnikovyprojekt.dtos.pageinfo.PageInfoDtoResponse;

import java.util.List;

public class ListBannedUnbannedUsersDto {
    private List<UsernameDto> itemList;

    private PageInfoDtoResponse pageInfo;

    public List<UsernameDto> getItemList() {
        return itemList;
    }

    public void setItemList(List<UsernameDto> itemList) {
        this.itemList = itemList;
    }

    public PageInfoDtoResponse getPageInfoDto() {
        return pageInfo;
    }

    public void setPageInfoDto(PageInfoDtoResponse pageInfoDtoResponse) {
        this.pageInfo = pageInfoDtoResponse;
    }
}
