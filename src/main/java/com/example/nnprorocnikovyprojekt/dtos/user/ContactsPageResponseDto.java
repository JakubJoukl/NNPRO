package com.example.nnprorocnikovyprojekt.dtos.user;

import com.example.nnprorocnikovyprojekt.dtos.pageinfo.PageInfoDtoResponse;

import java.util.List;

public class ContactsPageResponseDto {
    private List<UserDto> itemList;

    private PageInfoDtoResponse pageInfo;

    public List<UserDto> getItemList() {
        return itemList;
    }

    public void setItemList(List<UserDto> itemList) {
        this.itemList = itemList;
    }

    public PageInfoDtoResponse getPageInfoDto() {
        return pageInfo;
    }

    public void setPageInfoDto(PageInfoDtoResponse pageInfoDtoResponse) {
        this.pageInfo = pageInfoDtoResponse;
    }
}
