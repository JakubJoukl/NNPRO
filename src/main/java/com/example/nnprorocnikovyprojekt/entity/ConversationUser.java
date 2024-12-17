package com.example.nnprorocnikovyprojekt.entity;

import jakarta.persistence.*;
import jakarta.validation.constraints.NotNull;

import java.io.Serializable;
import java.time.Instant;
import java.util.Objects;

@Entity
@Table(name = "CONVERSATION_USER")
public class ConversationUser {
    @EmbeddedId
    private ConversationUserId conversationUserId;

    @ManyToOne(fetch = FetchType.LAZY)
    @MapsId("userId")
    @JoinColumn(name = "user_id", nullable = false)
    private User user;

    @ManyToOne(fetch = FetchType.LAZY)
    @MapsId("conversationId")
    @JoinColumn(name = "conversation_id", nullable = false)
    private Conversation conversation;

    //budeme rozsifrovavat privatnim klicem, je zasifrovana verejnym klicem
    @Column
    private String encryptedSymmetricKey;

    @Column(nullable = false, updatable = true, columnDefinition = "DATETIME(6) DEFAULT CURRENT_TIMESTAMP(6)")
    private Instant encryptedSymmetricKeyAddedOn;

    @Column(length = 400)
    private String initiationVector;

    @Column(length = 4000)
    private String cipheringPublicKey;

    @Column
    @NotNull
    private boolean isActive;

    protected ConversationUser() {
    }

    public ConversationUser(User user, Conversation conversation, String encryptedSymmetricKey, String cipheringPublicKey, String initiationVector) {
        this.user = user;
        this.conversation = conversation;
        this.isActive = true;
        this.conversationUserId = new ConversationUserId(conversation.getConversationId(), user.getUserId());
        this.encryptedSymmetricKey = encryptedSymmetricKey;
        this.cipheringPublicKey = cipheringPublicKey;
        this.initiationVector = initiationVector;
        this.encryptedSymmetricKeyAddedOn = Instant.now();
        user.getConversationUsers().add(this);
        conversation.getConversationUsers().add(this);
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

    public String getEncryptedSymmetricKey() {
        return encryptedSymmetricKey;
    }

    public void setEncryptedSymmetricKey(String cypheredSymmetricKey) {
        this.encryptedSymmetricKey = cypheredSymmetricKey;
    }

    public String getCipheringPublicKey() {
        return cipheringPublicKey;
    }

    public void setCipheringPublicKey(String cipheringPublicKey) {
        this.cipheringPublicKey = cipheringPublicKey;
    }

    public String getInitiationVector() {
        return initiationVector;
    }

    public void setInitiationVector(String initiationVector) {
        this.initiationVector = initiationVector;
    }

    public Instant getEncryptedSymmetricKeyAddedOn() {
        return encryptedSymmetricKeyAddedOn;
    }

    public void setEncryptedSymmetricKeyAddedOn(Instant encryptedSymmetricKeyAddedOn) {
        this.encryptedSymmetricKeyAddedOn = encryptedSymmetricKeyAddedOn;
    }

    public boolean isEncryptedSymmetricKeyExpired() {
        return getEncryptedSymmetricKeyAddedOn().plusSeconds(30 * 24 * 3600).isBefore(Instant.now());
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
