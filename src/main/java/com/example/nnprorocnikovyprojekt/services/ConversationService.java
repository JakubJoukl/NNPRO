package com.example.nnprorocnikovyprojekt.services;

import com.example.nnprorocnikovyprojekt.dtos.conversation.*;
import com.example.nnprorocnikovyprojekt.dtos.pageinfo.PageInfoDtoResponse;
import com.example.nnprorocnikovyprojekt.dtos.pageinfo.PageInfoRequestWrapper;
import com.example.nnprorocnikovyprojekt.dtos.user.PublicKeyDto;
import com.example.nnprorocnikovyprojekt.entity.Conversation;
import com.example.nnprorocnikovyprojekt.entity.ConversationUser;
import com.example.nnprorocnikovyprojekt.entity.Message;
import com.example.nnprorocnikovyprojekt.entity.User;
import com.example.nnprorocnikovyprojekt.repositories.ConversationRepository;
import com.example.nnprorocnikovyprojekt.repositories.ConversationUserRepository;
import com.example.nnprorocnikovyprojekt.repositories.MessageRepository;
import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.databind.ObjectMapper;
import org.modelmapper.ModelMapper;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Pageable;
import org.springframework.data.domain.Sort;
import org.springframework.messaging.simp.SimpMessagingTemplate;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.time.LocalDateTime;
import java.util.List;
import java.util.stream.Collectors;

@Service
public class ConversationService {

    @Autowired
    private ConversationRepository conversationRepository;

    @Autowired
    private SimpMessagingTemplate simpMessagingTemplate;

    @Autowired
    private MessageService messageService;

    @Autowired
    private UserService userService;

    @Autowired
    private ConversationUserRepository conversationUserRepository;

    @Autowired
    private MessageRepository messageRepository;

    @Autowired
    private ModelMapper modelMapper;

    public ObjectMapper objectMapper;

    public Conversation getConversationById(Integer conversationId){
        return conversationRepository.getConversationByConversationId(conversationId).orElseThrow(() -> new RuntimeException("Conversation not found"));
    }

    //Neukladame zpravy, ktere nejsme schopni odeslat?
    @Transactional(rollbackFor = Exception.class)
    public void sendMessageToAllSubscribersExceptUser(User user, Conversation conversation, String content) {
        Message message = new Message(user, conversation, content);
        messageService.saveMessage(message);
        List<ConversationUser> subscriptions = conversation.getActiveConversationUsers()
                .stream()
                .filter(conversationUser -> !conversationUser.getUser().getUsername().equals(user.getUsername()))
                .toList();
        subscriptions.forEach(subscription -> simpMessagingTemplate.convertAndSendToUser(subscription.getUser().getUsername(), "/topic/" + conversation.getConversationId(), content));
    }

    public AddUserToConversationResponse addUserToConversation(AddUserToConversationDto addUserToConversationDto) throws JsonProcessingException {
        User user = userService.getUserByUsername(addUserToConversationDto.getUsername());

        if(user == null) throw new RuntimeException("User is null");

        Conversation conversation = getConversationById(addUserToConversationDto.getConversationId());

        if(conversation == null) throw new RuntimeException("Conversation is null");

        ConversationUser conversationUser = new ConversationUser(user, conversation);
        saveConversationUser(conversationUser);
        if(user.getActivePublicKey().orElse(null) == null) return new AddUserToConversationResponse();
        else return new AddUserToConversationResponse(objectMapper.readValue(user.getActivePublicKey().get().getKey(), PublicKeyDto.class));
    }

    @Transactional(rollbackFor = Exception.class)
    public ConversationUser saveConversationUser(ConversationUser conversationUser){
        return conversationUserRepository.save(conversationUser);
    }

    public ConversationPageResponseDto getConversationsByPage(PageInfoRequestWrapper conversationPageinfoRequestDto) {
        User user = userService.getUserFromContext();
        Pageable pageInfo = PageRequest.of(conversationPageinfoRequestDto.getPageIndex(), conversationPageinfoRequestDto.getPageSize()).withSort(Sort.Direction.DESC, "conversationId");
        return conversationsToConversationNameDtos(conversationRepository.getConversationsByUsername(user, pageInfo));
    }

    private ConversationPageResponseDto conversationsToConversationNameDtos(Page<Conversation> page){
        if(page == null) return null;
        List<ConversationNameDto> conversationDtos = page.getContent().stream()
                .map(this::convertConversationToConversationNameDto)
                .collect(Collectors.toList());

        ConversationPageResponseDto conversationPageResponseDto = new ConversationPageResponseDto();
        conversationPageResponseDto.setItemList(conversationDtos);
        conversationPageResponseDto.setPageInfoDto(new PageInfoDtoResponse(page.getSize(), page.getNumber(), page.getTotalElements()));
        return conversationPageResponseDto;
    }

    private ConversationNameDto convertConversationToConversationNameDto(Conversation conversation) {
        return new ConversationNameDto(conversation.getConversationId(), conversation.getConversationName());
    }

    private List<Conversation> conversationNameDtosToConversations(List<ConversationNameDto> conversations){
        if(conversations == null) return null;
        return conversations.stream()
                .map(user -> getConversationById(user.getId()))
                .collect(Collectors.toList());
    }

    public List<MessageDto> getConversationMessages(GetConversationMessagesDto getConversationMessagesDto) {
        Conversation conversation = getConversationById(getConversationMessagesDto.getConversationId());
        LocalDateTime dateFrom = getConversationMessagesDto.getFrom();
        LocalDateTime dateTo = getConversationMessagesDto.getTo();
        if(dateFrom == null) dateFrom = LocalDateTime.MIN;
        if(dateTo == null) dateTo = LocalDateTime.MAX;
        if(conversation == null) throw new RuntimeException("Conversation is null");
        List<Message> messages = messageRepository.getMessageByConversationAndDateSendIsBetween(conversation, dateFrom, dateTo);
        return convertMessagesToMessageDtos(messages);
    }

    private List<MessageDto> convertMessagesToMessageDtos(List<Message> messages){
        return messages.stream().map(this::convertMessageToMessageDto).collect(Collectors.toList());
    }

    private MessageDto convertMessageToMessageDto(Message message) {
        MessageDto messageDto = new MessageDto();
        messageDto.setMessageId(message.getMessageId());
        messageDto.setMessage(message.getContent());
        messageDto.setSender(message.getSender().getUsername());
        messageDto.setConversationId(message.getConversation().getConversationId());
        messageDto.setDateSend(message.getDateSend());
        return messageDto;
    }
}
