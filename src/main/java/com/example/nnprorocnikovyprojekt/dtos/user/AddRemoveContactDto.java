package com.example.nnprorocnikovyprojekt.dtos.user;

import jakarta.validation.constraints.NotNull;
import jakarta.validation.constraints.Pattern;

public class AddRemoveContactDto {
    @NotNull
    private String username;

    public String getUsername() {
        return username;
    }

    public void setUsername(String username) {
        this.username = username;
    }
}
