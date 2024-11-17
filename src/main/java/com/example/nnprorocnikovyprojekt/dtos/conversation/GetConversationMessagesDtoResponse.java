package com.example.nnprorocnikovyprojekt.dtos.conversation;

import com.example.nnprorocnikovyprojekt.dtos.pageinfo.PageInfoDtoRequest;
import com.example.nnprorocnikovyprojekt.dtos.pageinfo.PageInfoDtoResponse;

import java.util.ArrayList;
import java.util.List;

public class GetConversationMessagesDtoResponse {
    private PageInfoDtoResponse pageInfo;

    private String cipheredSymmetricKey;

    private List<MessageDto> messages = new ArrayList<>();

    public String getCipheredSymmetricKey() {
        return cipheredSymmetricKey;
    }

    public void setCipheredSymmetricKey(String cipheredSymmetricKey) {
        this.cipheredSymmetricKey = cipheredSymmetricKey;
    }

    public List<MessageDto> getMessages() {
        return messages;
    }

    public void setMessages(List<MessageDto> messages) {
        this.messages = messages;
    }

    public PageInfoDtoResponse getPageInfo() {
        return pageInfo;
    }

    public void setPageInfo(PageInfoDtoResponse pageInfo) {
        this.pageInfo = pageInfo;
    }
}
