package com.example.nnprorocnikovyprojekt.dtos.conversation;

import java.util.ArrayList;
import java.util.List;

public class CreateConversationDto {
    private String name;

    private List<String> users = new ArrayList<>();

    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }

    public List<String> getUsers() {
        return users;
    }

    public void setUsers(List<String> users) {
        this.users = users;
    }
}
