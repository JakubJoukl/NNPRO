package com.example.nnprorocnikovyprojekt.dtos.user;

import java.util.List;

public class UserDto {
    private String username;

    private String email;

    private PublicKeyDto publicKey;

    private List<String> authorities;

    public UserDto() {
    }

    public UserDto(String username, String email, PublicKeyDto publicKey, List<String> authorities) {
        this.username = username;
        this.email = email;
        this.publicKey = publicKey;
        this.authorities = authorities;
    }

    public String getUsername() {
        return username;
    }

    public void setUsername(String username) {
        this.username = username;
    }

    public String getEmail() {
        return email;
    }

    public void setEmail(String email) {
        this.email = email;
    }

    public PublicKeyDto getPublicKey() {
        return publicKey;
    }

    public void setPublicKey(PublicKeyDto publicKey) {
        this.publicKey = publicKey;
    }

    public List<String> getAuthorities() {
        return authorities;
    }

    public void setAuthorities(List<String> authorities) {
        this.authorities = authorities;
    }
}
