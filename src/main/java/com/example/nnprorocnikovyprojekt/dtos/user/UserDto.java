package com.example.nnprorocnikovyprojekt.dtos.user;

public class UserDto {
    private String username;

    private String email;

    private PublicKeyDto publicKeyDto;

    public UserDto() {
    }

    public UserDto(String username, String email, PublicKeyDto publicKey) {
        this.username = username;
        this.email = email;
        this.publicKeyDto = publicKey;
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

    public PublicKeyDto getPublicKeyDto() {
        return publicKeyDto;
    }

    public void setPublicKeyDto(PublicKeyDto publicKeyDto) {
        this.publicKeyDto = publicKeyDto;
    }
}
