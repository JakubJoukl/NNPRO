package com.example.nnprorocnikovyprojekt.entity;

import jakarta.persistence.*;
import jakarta.validation.constraints.NotNull;

import java.io.Serializable;
import java.util.Objects;

@Entity
@Table(name = "CONVERSATION_USER")
public class ConversationUser {
    @EmbeddedId
    private ConversationUserId conversationUserId;

    @ManyToOne(cascade = {CascadeType.MERGE, CascadeType.PERSIST})
    @MapsId("userId")
    @JoinColumn(name = "user_id", nullable = false)
    private User user;

    @ManyToOne(cascade = {CascadeType.MERGE, CascadeType.PERSIST})
    @MapsId("conversationId")
    @JoinColumn(name = "conversation_id", nullable = false)
    private Conversation conversation;

    //budeme rozsifrovavat privatnim klicem, je zasifrovana verejnym klicem
    @Column
    private String cypheredSymmetricKey;

    @Column
    @NotNull
    private boolean isActive;

    public ConversationUser() {
    }

    public ConversationUser(User user, Conversation conversation, String cypheredSymmetricKey) {
        this.user = user;
        this.conversation = conversation;
        this.isActive = true;
        this.conversationUserId = new ConversationUserId(conversation.getConversationId(), user.getUserId());
        this.cypheredSymmetricKey = cypheredSymmetricKey;
    }

    public ConversationUserId getConversationUserId() {
        return conversationUserId;
    }

    public void setConversationUserId(ConversationUserId conversationUserId) {
        this.conversationUserId = conversationUserId;
    }

    public User getUser() {
        return user;
    }

    public void setUser(User user) {
        this.user = user;
    }

    public Conversation getConversation() {
        return conversation;
    }

    public void setConversation(Conversation conversation) {
        this.conversation = conversation;
    }

    public boolean isActive() {
        return isActive;
    }

    public void setActive(boolean active) {
        isActive = active;
    }

    public String getCypheredSymmetricKey() {
        return cypheredSymmetricKey;
    }

    public void setCypheredSymmetricKey(String cypheredSymmetricKey) {
        this.cypheredSymmetricKey = cypheredSymmetricKey;
    }
}

@Embeddable
class ConversationUserId implements Serializable {
    @Column(name = "conversation_id")
    private Integer conversationId;

    @Column(name = "user_id")
    private Integer userId;

    public ConversationUserId(Integer conversationId, Integer userId) {
        this.conversationId = conversationId;
        this.userId = userId;
    }

    public ConversationUserId() {

    }

    public Integer getConversationId() {
        return conversationId;
    }

    public void setConversationId(Integer conversationId) {
        this.conversationId = conversationId;
    }

    public Integer getUserId() {
        return userId;
    }

    public void setUserId(Integer userId) {
        this.userId = userId;
    }

    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (o == null || getClass() != o.getClass()) return false;
        ConversationUserId that = (ConversationUserId) o;
        return Objects.equals(conversationId, that.conversationId) && Objects.equals(userId, that.userId);
    }

    @Override
    public int hashCode() {
        return Objects.hash(conversationId, userId);
    }
}
