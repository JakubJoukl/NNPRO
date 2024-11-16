package com.example.nnprorocnikovyprojekt.dtos.conversation;

import com.example.nnprorocnikovyprojekt.dtos.user.PublicKeyDto;

import java.util.HashMap;

public class CipheredSymmetricKeysDto {
    private String username;

    private String encryptedSymmetricKey;

    private HashMap<String, Integer> iv = new HashMap<>();

    private PublicKeyDto cipheringPublicKey;

    public CipheredSymmetricKeysDto() {

    }

    //TODO pro testy -> odebrat?
    public CipheredSymmetricKeysDto(String username, String cipheredSymmetricKey) {
        this.username = username;
        this.encryptedSymmetricKey = cipheredSymmetricKey;
    }

    public String getUsername() {
        return username;
    }

    public void setUsername(String username) {
        this.username = username;
    }

    public String getEncryptedSymmetricKey() {
        return encryptedSymmetricKey;
    }

    public void setEncryptedSymmetricKey(String encryptedSymmetricKey) {
        this.encryptedSymmetricKey = encryptedSymmetricKey;
    }

    public HashMap<String, Integer> getIv() {
        return iv;
    }

    public void setIv(HashMap<String, Integer> iv) {
        this.iv = iv;
    }

    public PublicKeyDto getCipheringPublicKey() {
        return cipheringPublicKey;
    }

    public void setCipheringPublicKey(PublicKeyDto cipheringPublicKey) {
        this.cipheringPublicKey = cipheringPublicKey;
    }
}
