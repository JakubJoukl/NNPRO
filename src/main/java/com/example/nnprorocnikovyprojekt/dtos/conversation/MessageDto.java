package com.example.nnprorocnikovyprojekt.dtos.conversation;

import java.time.Instant;
import java.util.HashMap;

public class MessageDto {

    private Integer id;

    private Integer conversationId;

    private HashMap<String, Integer> iv = new HashMap<>();

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

    public HashMap<String, Integer> getIv() {
        return iv;
    }

    public void setIv(HashMap<String, Integer> iv) {
        this.iv = iv;
    }

    public Integer getId() {
        return id;
    }

    public void setId(Integer id) {
        this.id = id;
    }
}
