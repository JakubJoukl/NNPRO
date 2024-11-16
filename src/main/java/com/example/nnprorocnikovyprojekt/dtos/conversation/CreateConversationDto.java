package com.example.nnprorocnikovyprojekt.dtos.conversation;

import java.util.ArrayList;
import java.util.List;

public class CreateConversationDto {
    private String name;

    private List<CipheredSymmetricKeysDto> cipheredSymmetricKeysDtos = new ArrayList<>();

    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }

    public List<CipheredSymmetricKeysDto> getCipheredSymmetricKeysDtos() {
        return cipheredSymmetricKeysDtos;
    }

    public void setCipheredSymmetricKeysDtos(List<CipheredSymmetricKeysDto> cipheredSymmetricKeysDtos) {
        this.cipheredSymmetricKeysDtos = cipheredSymmetricKeysDtos;
    }
}
