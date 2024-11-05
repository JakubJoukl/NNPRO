package com.example.nnprorocnikovyprojekt.controllers;

import com.example.nnprorocnikovyprojekt.dtos.conversation.ConversationPageResponseDto;
import com.example.nnprorocnikovyprojekt.dtos.conversation.MessageDto;
import com.example.nnprorocnikovyprojekt.dtos.pageinfo.PageInfoDtoRequest;
import com.example.nnprorocnikovyprojekt.dtos.general.GeneralResponseDto;
import com.example.nnprorocnikovyprojekt.dtos.pageinfo.PageInfoRequestWrapper;
import com.example.nnprorocnikovyprojekt.entity.Conversation;
import com.example.nnprorocnikovyprojekt.entity.User;
import com.example.nnprorocnikovyprojekt.services.ConversationService;
import com.example.nnprorocnikovyprojekt.services.UserService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.ResponseEntity;
import org.springframework.messaging.handler.annotation.DestinationVariable;
import org.springframework.messaging.handler.annotation.MessageMapping;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;

@Controller("chat")
public class ChatController {

    @Autowired
    private UserService userService;

    @Autowired
    private ConversationService conversationService;

    //https://medium.com/@poojithairosha/spring-boot-3-authenticate-websocket-connections-with-jwt-tokens-2b4ff60532b6
    //Asi chci destination variable a nepotrebuji hodnotu z Dto?
    @MessageMapping("/{conversationId}")
    public ResponseEntity<?> chat(MessageDto messageDto, @DestinationVariable Integer conversationId) {
        User user = userService.getUserFromContext();

        if(user == null) return ResponseEntity.status(403).body(new GeneralResponseDto("User not found"));

        System.out.format("Message received: {%s}", messageDto.getMessage());
        try {
            Conversation conversation = conversationService.getConversationById(conversationId);
            conversationService.sendMessageToAllSubscribersExceptUser(user, conversation, messageDto.getMessage());
        } catch (Exception e){
            return ResponseEntity.status(500).body(new GeneralResponseDto("Failed to process message"));
        }
        return ResponseEntity.status(200).body(new GeneralResponseDto("Message processed, receivers notified"));
    }

    @PostMapping(path = "/listUserConversation")
    public ResponseEntity<?> listUserConversation(@RequestBody PageInfoRequestWrapper pageInfoRequestWrapper){
        try {
            ConversationPageResponseDto userConversations = conversationService.getConversationsByPage(pageInfoRequestWrapper);
            return ResponseEntity.status(200).body(userConversations);
        } catch (Exception e){
            return ResponseEntity.status(500).body(new GeneralResponseDto("Failed to get conversations"));
        }
    }

    /*@MessageMapping("/send/{conversationId}")
    @SendTo("/topic/messages/{conversationId}")
    public Message send(@DestinationVariable Integer conversationId, Message message) {
        return message;
    }*/
}