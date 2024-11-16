package com.example.nnprorocnikovyprojekt.dtos.conversation;

public class CipheredSymmetricKeysDto {
    private String username;

    private String cipheredSymmetricKey;

    public CipheredSymmetricKeysDto() {

    }

    public CipheredSymmetricKeysDto(String username, String cipheredSymmetricKey) {
        this.username = username;
        this.cipheredSymmetricKey = cipheredSymmetricKey;
    }

    public String getUsername() {
        return username;
    }

    public void setUsername(String username) {
        this.username = username;
    }

    public String getCipheredSymmetricKey() {
        return cipheredSymmetricKey;
    }

    public void setCipheredSymmetricKey(String cipheredSymmetricKey) {
        this.cipheredSymmetricKey = cipheredSymmetricKey;
    }
}
