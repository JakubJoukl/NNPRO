package com.example.nnprorocnikovyprojekt.dtos.user;

import jakarta.validation.constraints.NotNull;
import jakarta.validation.constraints.Pattern;

public class AddRemoveContactDto {
    @Pattern(regexp = "^[A-Za-z][A-Za-z0-9_ ]{7,29}$")
    @NotNull
    private String username;

    public String getUsername() {
        return username;
    }

    public void setUsername(String username) {
        this.username = username;
    }
}
