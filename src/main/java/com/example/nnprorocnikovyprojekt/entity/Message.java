package com.example.nnprorocnikovyprojekt.entity;

import jakarta.persistence.*;
import jakarta.validation.constraints.NotNull;

import java.time.LocalDateTime;

@Entity
@Table(name = "MESSAGE")
public class Message {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Integer messageId;

    @ManyToOne
    @JoinColumn(name="sender_id", nullable=false)
    private User sender;

    @Column(length = 4000)
    private String content;

    @Column
    @NotNull
    private LocalDateTime dateSend;

    @ManyToOne
    @JoinColumn(name="conversation_id", nullable = false)
    private Conversation conversation;

    protected Message(){

    }

    public Message(User sender, Conversation conversation, String content) {
        this.sender = sender;
        this.conversation = conversation;
        this.content = content;
        this.dateSend = LocalDateTime.now();
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

    public LocalDateTime getDateSend() {
        return dateSend;
    }

    public void setDateSend(LocalDateTime dateSend) {
        this.dateSend = dateSend;
    }
}
