package com.example.nnprorocnikovyprojekt.dtos.conversation;

import com.example.nnprorocnikovyprojekt.dtos.user.PublicKeyDto;

import java.util.HashMap;

public class ConversationUsersDto {
    private Integer conversationId;

    private HashMap<String, PublicKeyDto> conversationUsers = new HashMap<>();


}
