package com.example.nnprorocnikovyprojekt.dtos.user;

public class UserDto {
    private String name;

    private String email;

    private PublicKeyDto publicKeyDto;

    public UserDto() {
    }

    public UserDto(String name, String email, PublicKeyDto publicKey) {
        this.name = name;
        this.email = email;
        this.publicKeyDto = publicKey;
    }

    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
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
