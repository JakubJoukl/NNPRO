package com.example.nnprorocnikovyprojekt.dtos.conversation;

import com.example.nnprorocnikovyprojekt.dtos.user.PublicKeyDto;

public class AddUserToConversationResponse {
    private PublicKeyDto publicKey;

    public AddUserToConversationResponse() {
    }

    public AddUserToConversationResponse(PublicKeyDto publicKey) {
        this.publicKey = publicKey;
    }

    public PublicKeyDto getPublicKey() {
        return publicKey;
    }

    public void setPublicKey(PublicKeyDto publicKey) {
        this.publicKey = publicKey;
    }
}
