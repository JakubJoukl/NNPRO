package com.example.nnprorocnikovyprojekt.dtos.conversation;

import jakarta.validation.constraints.NotNull;

public class AddRemoveUserToConversationDto {
    @NotNull
    private Integer conversationId;

    @NotNull
    private CipheredSymmetricKeysDto user;

    public Integer getConversationId() {
        return conversationId;
    }

    public void setConversationId(Integer conversationId) {
        this.conversationId = conversationId;
    }

    public CipheredSymmetricKeysDto getUser() {
        return user;
    }

    public void setUser(CipheredSymmetricKeysDto user) {
        this.user = user;
    }
}
