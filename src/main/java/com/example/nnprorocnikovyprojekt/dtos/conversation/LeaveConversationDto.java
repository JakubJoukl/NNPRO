package com.example.nnprorocnikovyprojekt.dtos.conversation;

public class LeaveConversationDto {
    private Integer conversationId;

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
