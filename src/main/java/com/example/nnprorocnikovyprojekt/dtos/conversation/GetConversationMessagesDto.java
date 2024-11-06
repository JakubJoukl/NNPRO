package com.example.nnprorocnikovyprojekt.dtos.conversation;

import java.time.LocalDateTime;

public class GetConversationMessagesDto {
    private LocalDateTime from;
    private LocalDateTime to;
    private Integer conversationId;

    public LocalDateTime getFrom() {
        return from;
    }

    public void setFrom(LocalDateTime from) {
        this.from = from;
    }

    public LocalDateTime getTo() {
        return to;
    }

    public void setTo(LocalDateTime to) {
        this.to = to;
    }

    public Integer getConversationId() {
        return conversationId;
    }

    public void setConversationId(Integer conversationId) {
        this.conversationId = conversationId;
    }
}
