package com.example.nnprorocnikovyprojekt.dtos.user;

import com.example.nnprorocnikovyprojekt.dtos.pageinfo.PageInfoDtoResponse;

import java.util.List;

public class ContactPageResponseDto {
    private List<ContactDto> itemList;

    private PageInfoDtoResponse pageInfo;

    public List<ContactDto> getItemList() {
        return itemList;
    }

    public void setItemList(List<ContactDto> itemList) {
        this.itemList = itemList;
    }

    public PageInfoDtoResponse getPageInfoDto() {
        return pageInfo;
    }

    public void setPageInfoDto(PageInfoDtoResponse pageInfoDtoResponse) {
        this.pageInfo = pageInfoDtoResponse;
    }
}
