package com.example.nnprorocnikovyprojekt.dtos.conversation;

import jakarta.validation.constraints.NotNull;
import jakarta.validation.constraints.Pattern;

import java.util.ArrayList;
import java.util.List;

public class CreateConversationDto {
    @NotNull
    @Pattern(regexp = "^[A-Za-z][A-Za-z0-9_ ]{7,29}$")
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
