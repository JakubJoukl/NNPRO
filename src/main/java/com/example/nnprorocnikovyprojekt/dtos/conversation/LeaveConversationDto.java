package com.example.nnprorocnikovyprojekt.dtos.conversation;

import jakarta.validation.constraints.NotNull;
import jakarta.validation.constraints.Pattern;

public class LeaveConversationDto {
    @NotNull
    private Integer conversationId;

    public Integer getConversationId() {
        return conversationId;
    }

    public void setConversationId(Integer conversationId) {
        this.conversationId = conversationId;
    }
}
