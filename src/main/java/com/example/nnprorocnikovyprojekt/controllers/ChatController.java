package com.example.nnprorocnikovyprojekt.controllers;

import com.example.nnprorocnikovyprojekt.dtos.conversation.*;
import com.example.nnprorocnikovyprojekt.dtos.general.GeneralResponseDto;
import com.example.nnprorocnikovyprojekt.dtos.pageinfo.PageInfoRequestWrapper;
import com.example.nnprorocnikovyprojekt.services.ConversationService;
import com.fasterxml.jackson.core.JsonProcessingException;
import jakarta.validation.Valid;
import jakarta.validation.constraints.NotNull;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.messaging.handler.annotation.MessageMapping;
import org.springframework.stereotype.Controller;
import org.springframework.validation.annotation.Validated;
import org.springframework.web.bind.annotation.*;

import java.security.Principal;

@Validated
@Controller("chat") //sad endpoint noises
public class ChatController {

    @Autowired
    private ConversationService conversationService;

    //https://medium.com/@poojithairosha/spring-boot-3-authenticate-websocket-connections-with-jwt-tokens-2b4ff60532b6
    //Asi chci destination variable a nepotrebuji hodnotu z Dto?
    //topic/${conversationId}
    @MessageMapping("/sendMessageToConversation")
    public ResponseEntity<?> chat(Principal principal, @Valid MessageDto messageDto) {
        //TODO sout po otestovani funkcionalit smazat
        System.out.format("Message received: {%s}", messageDto.getMessage());
        conversationService.sendMessageToAllSubscribers(principal, messageDto);
        return ResponseEntity.status(HttpStatus.OK).body(new GeneralResponseDto("Message processed, receivers notified"));
    }

    @PostMapping("/rotateKeys")
    public ResponseEntity<?> rotateKeys(@Valid @RequestBody RotateKeysDto rotateKeysDto) {
        conversationService.rotateKeys(rotateKeysDto);
        return ResponseEntity.status(HttpStatus.OK).body(new GeneralResponseDto("New keys set"));
    }

    @PostMapping(path = "/listUserConversation")
    public ResponseEntity<?> listUserConversation(@Valid @RequestBody PageInfoRequestWrapper pageInfoRequestWrapper){
        ConversationPageResponseDto userConversations = conversationService.getConversationsByPage(pageInfoRequestWrapper);
        return ResponseEntity.status(HttpStatus.OK).body(userConversations);
    }

    @PostMapping("/addUserToConversation")
    public ResponseEntity<?> addUserToConversation(@Valid @RequestBody AddRemoveUserToConversationDto addRemoveUserToConversationDto) throws JsonProcessingException {
        AddUserToConversationResponse addUserToConversationResponse = conversationService.addUserToConversation(addRemoveUserToConversationDto);
        return ResponseEntity.status(HttpStatus.OK).body(addUserToConversationResponse);
    }

    @DeleteMapping("/leaveConversation")
    public ResponseEntity<?> removeUserFromConversation(@Valid @RequestBody LeaveConversationDto leaveConversationDto){
        conversationService.leaveFromConversation(leaveConversationDto);
        return ResponseEntity.status(HttpStatus.OK).body(new GeneralResponseDto("User left the conversation"));
    }

    @PostMapping("/listMessages")
    public ResponseEntity<?> listMessages(@Valid @RequestBody GetConversationMessagesDto getConversationMessagesDto){
        GetConversationMessagesDtoResponse conversationMessagesDtoResponse = conversationService.getConversationMessagesDtoResponse(getConversationMessagesDto);
        return ResponseEntity.status(HttpStatus.OK).body(conversationMessagesDtoResponse);
    }

    @GetMapping("/getConversation/{conversationId}")
    public ResponseEntity<?> getConversation(@NotNull @PathVariable Integer conversationId){
        GetConversationResponseDto getConversationResponseDto = conversationService.getConversation(conversationId);
        return ResponseEntity.status(HttpStatus.OK).body(getConversationResponseDto);
    }

    @PostMapping("/createConversation")
    public ResponseEntity<?> createConversation(@Valid @RequestBody CreateConversationDto createConversationDto){
        ConversationNameDto conversationNameDto = conversationService.createConversation(createConversationDto);
        return ResponseEntity.status(HttpStatus.OK).body(conversationNameDto);
    }

    @DeleteMapping("/deleteMessage")
    public ResponseEntity<?> deleteMessage(@Valid @RequestBody DeleteMessageDto deleteMessageDto){
        conversationService.deleteMessage(deleteMessageDto);
        return ResponseEntity.status(200).body(new GeneralResponseDto("Message deleted"));
    }

    @DeleteMapping("/deleteConversation")
    public ResponseEntity<?> deleteConversation(@Valid @RequestBody ConversationNameDto conversationNameDto){
        conversationService.deleteUserConversation(conversationNameDto);
        return ResponseEntity.status(200).body(new GeneralResponseDto("Conversation deleted"));
    }

    /*@MessageMapping("/send/{conversationId}")
    @SendTo("/topic/messages/{conversationId}")
    public Message send(@DestinationVariable Integer conversationId, Message message) {
        return message;
    }*/
}