package com.example.nnprorocnikovyprojekt.dtos.conversation;

import java.time.Instant;

public class MessageDto {

    private Integer conversationId;

    private String sender;

    private String message;

    private Instant dateSend;

    private Instant validTo;

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

    public String getSender() {
        return sender;
    }

    public void setSender(String sender) {
        this.sender = sender;
    }

    public Instant getDateSend() {
        return dateSend;
    }

    public void setDateSend(Instant dateSend) {
        this.dateSend = dateSend;
    }

    public Instant getValidTo() {
        return validTo;
    }

    public void setValidTo(Instant validTo) {
        this.validTo = validTo;
    }
}
