package com.example.nnprorocnikovyprojekt.controllers;

import com.example.nnprorocnikovyprojekt.dtos.conversation.*;
import com.example.nnprorocnikovyprojekt.dtos.general.GeneralResponseDto;
import com.example.nnprorocnikovyprojekt.dtos.pageinfo.PageInfoRequestWrapper;
import com.example.nnprorocnikovyprojekt.entity.Conversation;
import com.example.nnprorocnikovyprojekt.entity.User;
import com.example.nnprorocnikovyprojekt.services.ConversationService;
import com.example.nnprorocnikovyprojekt.services.UserService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.messaging.handler.annotation.DestinationVariable;
import org.springframework.messaging.handler.annotation.MessageMapping;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;

import java.util.List;

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
            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR).body(new GeneralResponseDto("Failed to process message"));
        }
        return ResponseEntity.status(HttpStatus.OK).body(new GeneralResponseDto("Message processed, receivers notified"));
    }

    @PostMapping(path = "/listUserConversation")
    public ResponseEntity<?> listUserConversation(@RequestBody PageInfoRequestWrapper pageInfoRequestWrapper){
        try {
            ConversationPageResponseDto userConversations = conversationService.getConversationsByPage(pageInfoRequestWrapper);
            return ResponseEntity.status(HttpStatus.OK).body(userConversations);
        } catch (Exception e){
            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR).body(new GeneralResponseDto("Failed to get conversations"));
        }
    }

    @PostMapping("/addUserToConversation")
    public ResponseEntity<?> addUserToConversation(@RequestBody AddUserToConversationDto addUserToConversationDto){
        try {
            AddUserToConversationResponse addUserToConversationResponse = conversationService.addUserToConversation(addUserToConversationDto);
            return ResponseEntity.status(HttpStatus.OK).body(addUserToConversationResponse);
        } catch (Exception e){
            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR).body(new GeneralResponseDto("Failed to get conversations"));
        }
    }

    @PostMapping("/getConversationMessages")
    public ResponseEntity<?> getConversationMessages(@RequestBody GetConversationMessagesDto getConversationMessagesDto){
        try {
            List<MessageDto> messageDtos = conversationService.getConversationMessages(getConversationMessagesDto);
            return ResponseEntity.status(HttpStatus.OK).body(messageDtos);
        } catch (Exception e){
            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR).body(new GeneralResponseDto("Failed to get conversations"));
        }
    }

    /*@PostMapping("/createConversation")
    public ResponseEntity<?> createConversation(@RequestBody CreateConversationDto createConversationDto){
        try{
            
        } catch ()
    }*/

    /*@MessageMapping("/send/{conversationId}")
    @SendTo("/topic/messages/{conversationId}")
    public Message send(@DestinationVariable Integer conversationId, Message message) {
        return message;
    }*/
}