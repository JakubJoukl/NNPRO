package com.example.nnprorocnikovyprojekt.dtos.conversation;

import com.example.nnprorocnikovyprojekt.dtos.user.PublicKeyDto;

import java.util.HashMap;

public class CipheredSymmetricKeysDto {
    private String username;

    private String cipheredSymmetricKey;

    private HashMap<String, Integer> iv = new HashMap<>();

    private PublicKeyDto publicKeyDto;

    public CipheredSymmetricKeysDto() {

    }

    //TODO pro testy -> odebrat?
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

    public HashMap<String, Integer> getIv() {
        return iv;
    }

    public void setIv(HashMap<String, Integer> iv) {
        this.iv = iv;
    }

    public PublicKeyDto getPublicKeyDto() {
        return publicKeyDto;
    }

    public void setPublicKeyDto(PublicKeyDto publicKeyDto) {
        this.publicKeyDto = publicKeyDto;
    }
}
