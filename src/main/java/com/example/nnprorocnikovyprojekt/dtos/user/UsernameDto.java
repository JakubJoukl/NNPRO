package com.example.nnprorocnikovyprojekt.dtos.user;

public class UsernameDto {
    private String username;

    public UsernameDto(String username) {
        this.username = username;
    }

    public UsernameDto() {

    }

    public String getUsername() {
        return username;
    }

    public void setUsername(String username) {
        this.username = username;
    }
}
