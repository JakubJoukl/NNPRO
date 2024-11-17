package com.example.nnprorocnikovyprojekt.dtos.conversation;

import java.util.ArrayList;
import java.util.List;

//stejne jako createConversationDtoResponse
public class GetConversationResponseDto {
    private Integer conversationId;

    private String name;

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

    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }
}
