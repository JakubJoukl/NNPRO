package com.example.nnprorocnikovyprojekt.dtos.conversation;

import java.util.List;

public class ConversationPageInfoResponseDto {
    private List<ConversationNameDto> conversationNameDtoList;

    private PageInfoDto pageInfoDto;

    public List<ConversationNameDto> getConversationNameDtoList() {
        return conversationNameDtoList;
    }

    public void setConversationNameDtoList(List<ConversationNameDto> conversationNameDtoList) {
        this.conversationNameDtoList = conversationNameDtoList;
    }

    public PageInfoDto getPageInfoDto() {
        return pageInfoDto;
    }

    public void setPageInfoDto(PageInfoDto pageInfoDto) {
        this.pageInfoDto = pageInfoDto;
    }
}
