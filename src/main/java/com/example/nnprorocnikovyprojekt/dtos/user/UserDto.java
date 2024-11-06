package com.example.nnprorocnikovyprojekt.dtos.user;

public class UserDto {
    private String username;

    private String email;

    private PublicKeyDto publicKey;

    public UserDto() {
    }

    public UserDto(String username, String email, PublicKeyDto publicKey) {
        this.username = username;
        this.email = email;
        this.publicKey = publicKey;
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
}
