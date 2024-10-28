package com.example.nnprorocnikovyprojekt.entity;

import jakarta.persistence.*;
import jakarta.validation.constraints.NotNull;

import java.util.ArrayList;
import java.util.List;

@Entity
@Table(name = "CONVERSATION")
public class Conversation {

    @Id
    private Integer conversationId;

    @OneToMany(mappedBy = "conversation",cascade = CascadeType.ALL, fetch = FetchType.LAZY)
    private List<ConversationUser> conversationUsers = new ArrayList<>();

    @Column
    @NotNull
    private String conversationName;

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

    public String getConversationName() {
        return conversationName;
    }

    public void setConversationName(String conversationName) {
        this.conversationName = conversationName;
    }
}
