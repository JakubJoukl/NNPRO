package com.example.nnprorocnikovyprojekt.dtos.user;

public class ContactDto {
    private String username;

    private String email;

    private PublicKeyDto publicKey;

    private Boolean alreadyAdded;

    public ContactDto() {
    }

    public ContactDto(String username, String email, PublicKeyDto publicKey, Boolean alreadyAdded) {
        this.username = username;
        this.email = email;
        this.publicKey = publicKey;
        this.alreadyAdded = alreadyAdded;
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

    public Boolean getAlreadyAdded() {
        return alreadyAdded;
    }

    public void setAlreadyAdded(Boolean alreadyAdded) {
        this.alreadyAdded = alreadyAdded;
    }
}
