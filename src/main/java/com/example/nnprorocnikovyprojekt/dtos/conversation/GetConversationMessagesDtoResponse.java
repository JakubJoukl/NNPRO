package com.example.nnprorocnikovyprojekt.dtos.conversation;

import com.example.nnprorocnikovyprojekt.dtos.pageinfo.PageInfoDtoResponse;

import java.util.ArrayList;
import java.util.List;

public class GetConversationMessagesDtoResponse {
    private PageInfoDtoResponse pageInfo;

    private List<MessageDto> itemList = new ArrayList<>();

    public List<MessageDto> getItemList() {
        return itemList;
    }

    public void setItemList(List<MessageDto> itemList) {
        this.itemList = itemList;
    }

    public PageInfoDtoResponse getPageInfo() {
        return pageInfo;
    }

    public void setPageInfo(PageInfoDtoResponse pageInfo) {
        this.pageInfo = pageInfo;
    }
}
