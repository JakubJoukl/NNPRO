package com.example.nnprorocnikovyprojekt.dtos.conversation;

import com.example.nnprorocnikovyprojekt.dtos.pageinfo.PageInfoDtoRequest;

import java.time.Instant;

public class GetConversationMessagesDto {
    private PageInfoDtoRequest pageInfo;
    private Instant from;
    private Instant to;
    private Integer conversationId;

    public Instant getFrom() {
        return from;
    }

    public void setFrom(Instant from) {
        this.from = from;
    }

    public Instant getTo() {
        return to;
    }

    public void setTo(Instant to) {
        this.to = to;
    }

    public Integer getConversationId() {
        return conversationId;
    }

    public void setConversationId(Integer conversationId) {
        this.conversationId = conversationId;
    }

    public PageInfoDtoRequest getPageInfo() {
        return pageInfo;
    }

    public void setPageInfo(PageInfoDtoRequest pageInfo) {
        this.pageInfo = pageInfo;
    }
}
