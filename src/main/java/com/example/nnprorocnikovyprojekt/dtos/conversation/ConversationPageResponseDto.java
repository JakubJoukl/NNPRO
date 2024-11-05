package com.example.nnprorocnikovyprojekt.dtos.conversation;

import com.example.nnprorocnikovyprojekt.dtos.pageinfo.PageInfoDtoResponse;

import java.util.List;

public class ConversationPageResponseDto {
    private List<ConversationNameDto> itemList;

    private PageInfoDtoResponse pageInfo;

    public List<ConversationNameDto> getItemList() {
        return itemList;
    }

    public void setItemList(List<ConversationNameDto> itemList) {
        this.itemList = itemList;
    }

    public PageInfoDtoResponse getPageInfoDto() {
        return pageInfo;
    }

    public void setPageInfoDto(PageInfoDtoResponse pageInfoDtoResponse) {
        this.pageInfo = pageInfoDtoResponse;
    }
}
