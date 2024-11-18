package com.example.nnprorocnikovyprojekt.services;

import com.example.nnprorocnikovyprojekt.dtos.conversation.*;
import com.example.nnprorocnikovyprojekt.dtos.pageinfo.PageInfoDtoRequest;
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

import java.security.Principal;
import java.time.LocalDateTime;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
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

    public ObjectMapper objectMapper = new ObjectMapper();

    public Conversation getConversationById(Integer conversationId){
        return conversationRepository.getConversationByConversationId(conversationId).orElseThrow(() -> new RuntimeException("Conversation not found"));
    }

    //Neukladame zpravy, ktere nejsme schopni odeslat?
    @Transactional(rollbackFor = Exception.class)
    public void sendMessageToAllSubscribersExceptUser(Principal principal, MessageDto messageDto) {
        User user = userService.getUserByUsername(principal.getName());

        if(user == null) throw  new RuntimeException("User not found");

        Conversation conversation = getConversationById(messageDto.getConversationId());

        Message message = new Message(user, conversation, messageDto.getMessage(), messageDto.getValidTo());
        messageService.saveMessage(message);
        List<ConversationUser> subscriptions = conversation.getActiveConversationUsers()
                .stream()
                .filter(conversationUser -> !conversationUser.getUser().getUsername().equals(user.getUsername()))
                .collect(Collectors.toList());
        subscriptions.forEach(subscription -> simpMessagingTemplate.convertAndSendToUser(subscription.getUser().getUsername(), "/topic/" + conversation.getConversationId(), messageDto.getMessage()));
    }

    public AddUserToConversationResponse addUserToConversation(AddRemoveUserToConversationDto addRemoveUserToConversationDto) throws JsonProcessingException {
        User user = userService.getUserByUsername(addRemoveUserToConversationDto.getUser().getUsername());
        if(user == null) throw new RuntimeException("User is null");

        Conversation conversation = getConversationById(addRemoveUserToConversationDto.getConversationId());
        if(conversation == null) throw new RuntimeException("Conversation is null");

        if(conversation.getConversationUsers().stream().map(ConversationUser::getUser).anyMatch(user1 -> user1.equals(user))){
            throw new RuntimeException("User is already a member of this conversation");
        }

        ConversationUser conversationUser = getConversationUserFromCipheredSymmetricKeyDto(conversation, addRemoveUserToConversationDto.getUser());
        saveConversationUser(conversationUser);
        if(user.getActivePublicKey().orElse(null) == null) return new AddUserToConversationResponse();
        else return new AddUserToConversationResponse(objectMapper.readValue(user.getActivePublicKey().get().getKeyValue(), PublicKeyDto.class));
    }

    @Transactional(rollbackFor = Exception.class)
    public ConversationUser saveConversationUser(ConversationUser conversationUser){
        return conversationUserRepository.save(conversationUser);
    }

    @Transactional(rollbackFor = Exception.class)
    public Conversation saveConversation(Conversation conversation) {
        return conversationRepository.save(conversation);
    }

    public ConversationPageResponseDto getConversationsByPage(PageInfoRequestWrapper conversationPageinfoRequestDto) {
        User user = userService.getUserFromContext();
        Pageable pageInfo = PageRequest.of(conversationPageinfoRequestDto.getPageIndex(), conversationPageinfoRequestDto.getPageSize()).withSort(Sort.Direction.DESC, "conversationId");
        return conversationsToConversationNameDtos(conversationRepository.getConversationsByUsername(user, pageInfo));
    }

    public GetConversationMessagesDtoResponse getConversationMessagesDtoResponse(GetConversationMessagesDto getConversationMessagesDto) {
        User user = userService.getUserFromContext();
        Conversation conversation = getConversationById(getConversationMessagesDto.getConversationId());
        LocalDateTime dateFrom = getConversationMessagesDto.getFrom();
        LocalDateTime dateTo = getConversationMessagesDto.getTo();
        if(dateFrom == null) dateFrom = LocalDateTime.MIN;
        if(dateTo == null) dateTo = LocalDateTime.MAX;
        if(conversation == null) throw new RuntimeException("Conversation is null");

        ConversationUser conversationUser = conversation.getConversationUserByUsername(user.getUsername());
        PageInfoDtoRequest pageInfoDtoRequest = getConversationMessagesDto.getPageInfo();
        Integer pageIndex = pageInfoDtoRequest == null? 0 : pageInfoDtoRequest.getPageIndex();
        Integer pageSize = pageInfoDtoRequest == null? Integer.MAX_VALUE : pageInfoDtoRequest.getPageSize();
        Pageable pageInfo = PageRequest.of(pageIndex, pageSize).withSort(Sort.Direction.DESC, "messageId");
        Page<Message> messages = messageRepository.getMessageByConversationBetweenDatesValidTo(pageInfo, conversation, dateFrom, dateTo, LocalDateTime.now(), conversationUser);
        //Todo je potreba?
        //getConversationMessagesDtoResponse.setCipheredSymmetricKey(conversationUser.getEncryptedSymmetricKey());
        GetConversationMessagesDtoResponse getConversationMessagesDtoResponse = mapGetConversationMessagesDtoResponse(messages);
        return getConversationMessagesDtoResponse;
    }

    @Transactional(rollbackFor = Exception.class)
    public ConversationNameDto createConversation(CreateConversationDto createConversationDto) {
        Conversation conversation = new Conversation();
        conversation.setConversationName(createConversationDto.getName());
        Conversation updatedConversation = conversationRepository.save(conversation);

        List<ConversationUser> conversationUsers = getConversationUsersFromDto(createConversationDto, updatedConversation);

        updatedConversation.getConversationUsers().addAll(conversationUsers);
        Conversation returnedConversation = conversationRepository.save(updatedConversation);
        return convertConversationToConversationNameDto(returnedConversation);
    }

    public void removeUserFromConversation(LeaveConversationDto leaveConversationDto) {
        //TODO nebo pouzijeme boolean s neaktivnimi usery?
        Conversation conversation = getConversationById(leaveConversationDto.getConversationId());
        int sizeBeforeRemove = conversation.getConversationUsers().size();
        conversation.getConversationUsers().removeIf(conversationUser -> conversationUser.getUser().getUsername().equals(leaveConversationDto.getUsername()));
        saveConversation(conversation);
        if(sizeBeforeRemove != conversation.getConversationUsers().size() + 1){
            throw new RuntimeException("Exactly 1 element was supposed to be deleted");
        }
    }

    public GetConversationResponseDto getConversation(Integer conversationId) {
        Conversation conversation = getConversationById(conversationId);
        return convertConversationToGetConversationResponseDto(conversation);
    }

    private GetConversationMessagesDtoResponse mapGetConversationMessagesDtoResponse(Page<Message> messages) {
        GetConversationMessagesDtoResponse getConversationMessagesDtoResponse = new GetConversationMessagesDtoResponse();
        getConversationMessagesDtoResponse.setItemList(convertMessagesToMessageDtos(messages.getContent()));
        PageInfoDtoResponse pageInfoDtoResponse = new PageInfoDtoResponse();
        pageInfoDtoResponse.setPageIndex(messages.getNumber());
        pageInfoDtoResponse.setPageSize(messages.getSize());
        pageInfoDtoResponse.setTotal(messages.getTotalElements());
        getConversationMessagesDtoResponse.setPageInfo(pageInfoDtoResponse);
        return getConversationMessagesDtoResponse;
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

    private List<MessageDto> convertMessagesToMessageDtos(List<Message> messages){
        return messages.stream().map(this::convertMessageToMessageDto).collect(Collectors.toList());
    }

    private MessageDto convertMessageToMessageDto(Message message) {
        MessageDto messageDto = new MessageDto();
        messageDto.setMessage(message.getContent());
        messageDto.setSender(message.getSender().getUsername());
        messageDto.setDateSend(message.getDateSend());
        messageDto.setValidTo(message.getValidTo());
        return messageDto;
    }

    private GetConversationResponseDto convertConversationToGetConversationResponseDto(Conversation conversation) {
        GetConversationResponseDto getConversationResponseDto = new GetConversationResponseDto();
        getConversationResponseDto.setName(conversation.getConversationName());
        getConversationResponseDto.setConversationId(conversation.getConversationId());
        getConversationResponseDto.setUsers(getListOfUsersFromCreateConversationDto(conversation.getActiveConversationUsers()));
        return getConversationResponseDto;
    }

    private List<CipheredSymmetricKeysDto> getListOfUsersFromCreateConversationDto(List<ConversationUser> conversationUsers) {
        List<CipheredSymmetricKeysDto> cipheredSymmetricKeysDtos = conversationUsers.stream().map(this::convertConversationUserToCipheredSymmetricKeysDto).collect(Collectors.toList());
        return cipheredSymmetricKeysDtos;
    }

    private CipheredSymmetricKeysDto convertConversationUserToCipheredSymmetricKeysDto(ConversationUser conversationUser) {
        CipheredSymmetricKeysDto cipheredSymmetricKeysDto = new CipheredSymmetricKeysDto();
        cipheredSymmetricKeysDto.setUsername(conversationUser.getUser().getUsername());
        cipheredSymmetricKeysDto.setEncryptedSymmetricKey(conversationUser.getEncryptedSymmetricKey());
        try {
            HashMap<String, Integer> ivMap = (HashMap<String, Integer>)objectMapper.readValue(conversationUser.getInitiationVector(), Map.class);
            cipheredSymmetricKeysDto.setIv(ivMap);
            cipheredSymmetricKeysDto.setCipheringPublicKey(objectMapper.readValue(conversationUser.getCipheringPublicKey(), PublicKeyDto.class));
        } catch (Exception e) {
            throw new RuntimeException("Failed to deserialize key");
        }
        return cipheredSymmetricKeysDto;
    }

    private List<ConversationUser> getConversationUsersFromDto(CreateConversationDto createConversationDto, Conversation updatedConversation) {
        List<ConversationUser> conversationUsers = createConversationDto.getUsers().stream()
                .map(cipheredSymmetricKeysDto -> getConversationUserFromCipheredSymmetricKeyDto(updatedConversation, cipheredSymmetricKeysDto)).collect(Collectors.toList());
        return conversationUsers;
    }

    private ConversationUser getConversationUserFromCipheredSymmetricKeyDto(Conversation updatedConversation, CipheredSymmetricKeysDto cipheredSymmetricKeysDto) {
        String publicKeyDtoAsString = null;
        String initiationVectorAsString = null;
        try {
            publicKeyDtoAsString = objectMapper.writeValueAsString(cipheredSymmetricKeysDto.getCipheringPublicKey());
            initiationVectorAsString = objectMapper.writeValueAsString(cipheredSymmetricKeysDto.getIv());
        } catch (JsonProcessingException e) {
            throw new RuntimeException("Failed to parse public key to string");
        }
        return new ConversationUser(
                userService.getUserByUsername(cipheredSymmetricKeysDto.getUsername()),
                updatedConversation, cipheredSymmetricKeysDto.getEncryptedSymmetricKey(),
                publicKeyDtoAsString,
                initiationVectorAsString
        );
    }
}
