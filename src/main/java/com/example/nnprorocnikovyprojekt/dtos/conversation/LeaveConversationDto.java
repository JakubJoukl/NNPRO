package com.example.nnprorocnikovyprojekt.dtos.conversation;

import jakarta.validation.constraints.NotNull;
import jakarta.validation.constraints.Pattern;

public class LeaveConversationDto {
    @NotNull
    private Integer conversationId;

    @NotNull
    @Pattern(regexp = "^[A-Za-z][A-Za-z0-9_ ]{7,29}$")
    private String username;

    public Integer getConversationId() {
        return conversationId;
    }

    public void setConversationId(Integer conversationId) {
        this.conversationId = conversationId;
    }

    public String getUsername() {
        return username;
    }

    public void setUsername(String username) {
        this.username = username;
    }
}
