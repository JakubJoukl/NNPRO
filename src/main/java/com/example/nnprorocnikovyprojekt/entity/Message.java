package com.example.nnprorocnikovyprojekt.entity;

import jakarta.persistence.*;
import jakarta.validation.constraints.NotNull;

import java.time.Instant;

@Entity
@Table(name = "MESSAGE")
public class Message {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Integer messageId;

    @ManyToOne
    @JoinColumn(name="sender_id", nullable=false)
    private User sender;

    @NotNull
    @Column(length = 10000)
    private String content;

    @Column
    @NotNull
    private Instant dateSend;

    @Column
    private Instant validTo;

    @NotNull
    @Column(length = 400)
    private String initiationVector;

    @ManyToOne
    @JoinColumn(name="conversation_id", nullable = false)
    private Conversation conversation;

    protected Message(){

    }

    public Message(User sender, Conversation conversation, String content, Instant validTo, String initiationVector) {
        this.sender = sender;
        this.conversation = conversation;
        this.content = content;
        this.dateSend = Instant.now();
        this.validTo = validTo;
        this.initiationVector = initiationVector;
    }

    public Integer getMessageId() {
        return messageId;
    }

    public void setMessageId(Integer messageId) {
        this.messageId = messageId;
    }

    public User getSender() {
        return sender;
    }

    public void setSender(User sender) {
        this.sender = sender;
    }

    public Conversation getConversation() {
        return conversation;
    }

    public void setConversation(Conversation conversation) {
        this.conversation = conversation;
    }

    public String getContent() {
        return content;
    }

    public void setContent(String message) {
        this.content = message;
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

    public String getInitiationVector() {
        return initiationVector;
    }

    public void setInitiationVector(String initiationVector) {
        this.initiationVector = initiationVector;
    }
}
