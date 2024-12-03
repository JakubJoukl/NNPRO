package com.example.nnprorocnikovyprojekt.dtos.conversation;

import jakarta.validation.constraints.NotNull;
import jakarta.validation.constraints.Pattern;

import java.time.Instant;
import java.util.HashMap;

public class MessageDto {

    @NotNull
    private Integer id;

    @NotNull
    private Integer conversationId;

    private HashMap<String, Integer> iv = new HashMap<>();

    @Pattern(regexp = "^[A-Za-z][A-Za-z0-9_ ]{7,29}$")
    @NotNull
    private String sender;

    @NotNull
    private String message;

    @NotNull
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
