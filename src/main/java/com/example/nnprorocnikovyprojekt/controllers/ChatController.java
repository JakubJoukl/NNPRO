package com.example.nnprorocnikovyprojekt.controllers;

import com.example.nnprorocnikovyprojekt.dtos.conversation.*;
import com.example.nnprorocnikovyprojekt.dtos.general.GeneralResponseDto;
import com.example.nnprorocnikovyprojekt.dtos.pageinfo.PageInfoRequestWrapper;
import com.example.nnprorocnikovyprojekt.services.ConversationService;
import com.fasterxml.jackson.core.JsonProcessingException;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.messaging.handler.annotation.MessageMapping;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.*;

import java.security.Principal;

@Controller("chat")
public class ChatController {

    @Autowired
    private ConversationService conversationService;

    //https://medium.com/@poojithairosha/spring-boot-3-authenticate-websocket-connections-with-jwt-tokens-2b4ff60532b6
    //Asi chci destination variable a nepotrebuji hodnotu z Dto?
    //topic/${conversationId}
    @MessageMapping("/sendMessageToConversation")
    public ResponseEntity<?> chat(Principal principal, MessageDto messageDto) {
        //TODO sout po otestovani funkcionalit smazat
        System.out.format("Message received: {%s}", messageDto.getMessage());
        conversationService.sendMessageToAllSubscribers(principal, messageDto);
        return ResponseEntity.status(HttpStatus.OK).body(new GeneralResponseDto("Message processed, receivers notified"));
    }

    @PostMapping(path = "/listUserConversation")
    public ResponseEntity<?> listUserConversation(@RequestBody PageInfoRequestWrapper pageInfoRequestWrapper){
        ConversationPageResponseDto userConversations = conversationService.getConversationsByPage(pageInfoRequestWrapper);
        return ResponseEntity.status(HttpStatus.OK).body(userConversations);
    }

    @PostMapping("/addUserToConversation")
    public ResponseEntity<?> addUserToConversation(@RequestBody AddRemoveUserToConversationDto addRemoveUserToConversationDto) throws JsonProcessingException {
        AddUserToConversationResponse addUserToConversationResponse = conversationService.addUserToConversation(addRemoveUserToConversationDto);
        return ResponseEntity.status(HttpStatus.OK).body(addUserToConversationResponse);
    }

    @DeleteMapping("/leaveConversation")
    public ResponseEntity<?> removeUserFromConversation(@RequestBody LeaveConversationDto leaveConversationDto){
        conversationService.removeUserFromConversation(leaveConversationDto);
        return ResponseEntity.status(HttpStatus.OK).body(new GeneralResponseDto("User left the conversation"));
    }

    @PostMapping("/listMessages")
    public ResponseEntity<?> listMessages(@RequestBody GetConversationMessagesDto getConversationMessagesDto){
        GetConversationMessagesDtoResponse conversationMessagesDtoResponse = conversationService.getConversationMessagesDtoResponse(getConversationMessagesDto);
        return ResponseEntity.status(HttpStatus.OK).body(conversationMessagesDtoResponse);
    }

    @GetMapping("/getConversation/{conversationId}")
    public ResponseEntity<?> getConversation(@PathVariable Integer conversationId){
        GetConversationResponseDto getConversationResponseDto = conversationService.getConversation(conversationId);
        return ResponseEntity.status(HttpStatus.OK).body(getConversationResponseDto);
    }

    @PostMapping("/createConversation")
    public ResponseEntity<?> createConversation(@RequestBody CreateConversationDto createConversationDto){
        ConversationNameDto conversationNameDto = conversationService.createConversation(createConversationDto);
        return ResponseEntity.status(HttpStatus.OK).body(conversationNameDto);
    }

    @MessageMapping("/deleteMessage")
    public ResponseEntity<?> deleteMessage(@RequestBody DeleteMessageDto deleteMessageDto){
        conversationService.deleteMessage(deleteMessageDto);
        return ResponseEntity.status(200).body(new GeneralResponseDto("Message deleted"));
    }

    @DeleteMapping("/deleteConversation")
    public ResponseEntity<?> deleteConversations(@RequestBody ConversationNameDto conversationNameDto){
        conversationService.deleteUserConversation(conversationNameDto);
        return ResponseEntity.status(200).body(new GeneralResponseDto("Conversation deleted"));
    }

    /*@MessageMapping("/send/{conversationId}")
    @SendTo("/topic/messages/{conversationId}")
    public Message send(@DestinationVariable Integer conversationId, Message message) {
        return message;
    }*/
}