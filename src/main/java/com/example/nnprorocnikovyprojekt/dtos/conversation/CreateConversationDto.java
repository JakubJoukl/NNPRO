package com.example.nnprorocnikovyprojekt.dtos.conversation;

import java.util.ArrayList;
import java.util.List;

public class CreateConversationDto {
    private String name;

    private List<CipheredSymmetricKeysDto> users = new ArrayList<>();

    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }

    public List<CipheredSymmetricKeysDto> getUsers() {
        return users;
    }

    public void setUsers(List<CipheredSymmetricKeysDto> users) {
        this.users = users;
    }
}
