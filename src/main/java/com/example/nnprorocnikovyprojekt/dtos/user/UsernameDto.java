package com.example.nnprorocnikovyprojekt.dtos.user;

import jakarta.validation.constraints.NotNull;

public class UsernameDto {
    @NotNull
    private String username;

    public String getUsername() {
        return username;
    }

    public void setUsername(String username) {
        this.username = username;
    }
}
