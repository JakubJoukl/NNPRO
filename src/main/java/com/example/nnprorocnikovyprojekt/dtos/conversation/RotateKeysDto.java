package com.example.nnprorocnikovyprojekt.dtos.conversation;

import java.util.ArrayList;
import java.util.List;

public class RotateKeysDto {
    private Integer conversationId;

    private List<CipheredSymmetricKeysDto> users = new ArrayList<>();

    public Integer getConversationId() {
        return conversationId;
    }

    public void setConversationId(Integer conversationId) {
        this.conversationId = conversationId;
    }

    public List<CipheredSymmetricKeysDto> getUsers() {
        return users;
    }

    public void setUsers(List<CipheredSymmetricKeysDto> users) {
        this.users = users;
    }
}
