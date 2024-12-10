package com.example.nnprorocnikovyprojekt.dtos.conversation;

import com.example.nnprorocnikovyprojekt.dtos.user.PublicKeyDto;
import jakarta.validation.constraints.NotNull;

import java.util.HashMap;

public class AddRemoveUserToConversationDto extends CipheredSymmetricKeysDto {
    @NotNull
    private Integer conversationId;

    private PublicKeyDto publicKey;

    private String user;

    private String encryptedSymmetricKey;

    private HashMap<String, Integer> iv = new HashMap<>();

    private PublicKeyDto cipheringPublicKey;

    public AddRemoveUserToConversationDto() {

    }

    //TODO pro testy -> odebrat?
    public AddRemoveUserToConversationDto(String username, String cipheredSymmetricKey) {
        this.user = username;
        this.encryptedSymmetricKey = cipheredSymmetricKey;
    }

    public String getUser() {
        return user;
    }

    @Override
    public String getUsername() {
        return getUser();
    }

    @Override
    public void setUsername(String username) {
        setUser(username);
    }

    public void setUser(String user) {
        this.user = user;
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

    public void setConversationId(Integer conversationId) {
        this.conversationId = conversationId;
    }

    public Integer getConversationId() {
        return conversationId;
    }
}
