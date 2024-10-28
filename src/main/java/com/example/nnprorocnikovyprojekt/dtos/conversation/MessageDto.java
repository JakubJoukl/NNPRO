package com.example.nnprorocnikovyprojekt.dtos.conversation;

public class MessageDto {

    private String message;

    private Integer conversationId;

    public String getMessage() {
        return message;
    }

    public void setMessage(String message) {
        this.message = message;
    }

    public Integer getConversationId() {
        return conversationId;
    }

    public void setConversationId(Integer conversationId) {
        this.conversationId = conversationId;
    }
}
