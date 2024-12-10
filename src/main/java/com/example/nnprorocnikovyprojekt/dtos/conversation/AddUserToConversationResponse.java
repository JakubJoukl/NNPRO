package com.example.nnprorocnikovyprojekt.dtos.conversation;

import com.example.nnprorocnikovyprojekt.dtos.user.PublicKeyDto;

import java.util.HashMap;

public class AddUserToConversationResponse extends CipheredSymmetricKeysDto {
    private PublicKeyDto publicKey;

    private String username;

    private String encryptedSymmetricKey;

    private HashMap<String, Integer> iv = new HashMap<>();

    private PublicKeyDto cipheringPublicKey;

    public AddUserToConversationResponse() {

    }

    //TODO pro testy -> odebrat?
    public AddUserToConversationResponse(String username, String cipheredSymmetricKey) {
        this.username = username;
        this.encryptedSymmetricKey = cipheredSymmetricKey;
    }

    public AddUserToConversationResponse(PublicKeyDto publicKey) {
        this.publicKey = publicKey;
    }

    public String getUsername() {
        return username;
    }

    public void setUsername(String username) {
        this.username = username;
    }

    public PublicKeyDto getPublicKey() {
        return publicKey;
    }

    public void setPublicKey(PublicKeyDto publicKey) {
        this.publicKey = publicKey;
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
