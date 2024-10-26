package com.example.nnprorocnikovyprojekt.entity;

import jakarta.persistence.*;

import java.util.List;

@Entity
@Table(name = "CONVERSATION")
public class Conversation {

    @Id
    private Integer conversationId;

    @OneToMany(mappedBy = "conversation",cascade = CascadeType.ALL, fetch = FetchType.LAZY)
    private List<ConversationUser> conversationUsers;

    public Integer getConversationId() {
        return conversationId;
    }

    public void setConversationId(Integer conversationId) {
        this.conversationId = conversationId;
    }

    public List<ConversationUser> getConversationUsers() {
        return conversationUsers;
    }

    public void setConversationUsers(List<ConversationUser> conversationUsers) {
        this.conversationUsers = conversationUsers;
    }
}
