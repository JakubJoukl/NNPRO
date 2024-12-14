package com.example.nnprorocnikovyprojekt.services;

import com.example.nnprorocnikovyprojekt.advice.exceptions.NotFoundException;
import com.example.nnprorocnikovyprojekt.advice.exceptions.UnauthorizedException;
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
import java.time.Instant;
import java.time.LocalDateTime;
import java.time.ZoneOffset;
import java.util.ArrayList;
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

    @Autowired
    public ObjectMapper objectMapper;

    public Conversation getConversationById(Integer conversationId) {
        return conversationRepository.getConversationByConversationId(conversationId).orElseThrow(() -> new RuntimeException("Conversation not found"));
    }

    //pro testy
    public void setObjectMapper(ObjectMapper objectMapper) {
        this.objectMapper = objectMapper;
    }

    //Neukladame zpravy, ktere nejsme schopni odeslat?
    @Transactional(rollbackFor = Exception.class)
    public void sendMessageToAllSubscribers(Principal principal, MessageDto messageDto) {
        User user = userService.getUserByUsername(principal.getName());

        if (user == null) throw new UnauthorizedException("User not found");

        Conversation conversation = getConversationById(messageDto.getConversationId());

        String initiationVectorAsString = null;
        try {
            initiationVectorAsString = objectMapper.writeValueAsString(messageDto.getIv());
        } catch (JsonProcessingException e) {
            throw new RuntimeException("Failed to parse dto to message");
        }

        Message message = new Message(user, conversation, messageDto.getMessage(), messageDto.getValidTo(), initiationVectorAsString);
        messageService.saveMessage(message);
        simpMessagingTemplate.convertAndSend("/topic/addMessage/" + conversation.getConversationId(), convertMessageToMessageDto(message));
    }

    public void notifyUsersAboutNewConversationExceptUser(Conversation conversation, User creator) {
        List<User> usersToNotify = getUsersExceptUser(conversation, creator);

        usersToNotify.forEach(user -> notifyUserAboutNewConversation(conversation, user));
    }

    public void notifyUsersAboutNewMemberExceptUser(Conversation conversation, User exceptWhom, AddRemoveUserToConversationDto addRemoveUserToConversationDto) {
        List<User> usersToNotify = getUsersExceptUser(conversation, exceptWhom);

        usersToNotify.forEach(user -> notifyUserAboutNewUser(conversation, user, addRemoveUserToConversationDto));
    }

    public void notifyUserAboutNewConversation(Conversation conversation, User user){
        simpMessagingTemplate.convertAndSend("/topic/newConversation/" + user.getUsername(), convertConversationToConversationNameDto(conversation));
    }

    public void notifyUserAboutNewUser(Conversation conversation, User user, AddRemoveUserToConversationDto addRemoveUserToConversationDto){
        simpMessagingTemplate.convertAndSend("/topic/newUser/" + user.getUsername(), addRemoveUserToConversationDto);
    }

    private List<User> getUsersExceptUser(Conversation conversation, User exceptWhom) {
        List<User> usersToNotify = conversation.getConversationUsers().stream()
                .map(ConversationUser::getUser)
                .filter(user -> !user.equals(exceptWhom))
                .collect(Collectors.toList());
        return usersToNotify;
    }

    public void deleteMessage(DeleteMessageDto deleteMessageDto){
        User sender = userService.getUserFromContext();
        Message message = messageService.getMessageById(deleteMessageDto.getId());
        if(message.getSender() != sender) throw new UnauthorizedException("User is not the sender of this message");
        messageService.deleteMessage(message);
        simpMessagingTemplate.convertAndSend("/topic/deleteMessage/" + message.getConversation().getConversationId(), convertMessageToMessageDto(message));
    }


    //realne jen pro druheho uzivatele
    public void notifyOtherConversationPartiesAboutDelete(User originator, Conversation conversation){
        List<User> usersToNotify = getUsersExceptUser(conversation, originator);
        usersToNotify.forEach(user -> simpMessagingTemplate.convertAndSend("/topic/deleteConversation/" + user.getUsername(), convertConversationToConversationNameDto(conversation)));
    }

    //realne jen pro
    public void notifyOtherConversationPartiesAboutLeave(User originator, Conversation conversation){
        List<User> usersToNotify = getUsersExceptUser(conversation, originator);
        usersToNotify.forEach(user -> simpMessagingTemplate.convertAndSend("/topic/leaveConversation/" + user.getUsername(), convertConversationToConversationNameDto(conversation)));
    }

    public AddUserToConversationResponse addUserToConversation(AddRemoveUserToConversationDto addRemoveUserToConversationDto) throws JsonProcessingException {
        User user = userService.getUserByUsername(addRemoveUserToConversationDto.getUser());
        if (user == null) throw new NotFoundException("User is null");

        Conversation conversation = getConversationById(addRemoveUserToConversationDto.getConversationId());
        if (conversation == null) throw new NotFoundException("Conversation is null");

        if (conversation.getConversationUsers().stream().map(ConversationUser::getUser).anyMatch(user1 -> user1.equals(user))) {
            throw new RuntimeException("User is already a member of this conversation");
        }

        ConversationUser conversationUser = getConversationUserFromCipheredSymmetricKeyDto(conversation, addRemoveUserToConversationDto);
        conversation.getConversationUsers().add(conversationUser);
        saveConversation(conversation);
        notifyUserAboutNewConversation(conversation, user);
        notifyUsersAboutNewMemberExceptUser(conversation, user, addRemoveUserToConversationDto);
        if (user.getActivePublicKey().orElse(null) == null) return new AddUserToConversationResponse();
        else
            return new AddUserToConversationResponse(objectMapper.readValue(user.getActivePublicKey().get().getKeyValue(), PublicKeyDto.class), user.getUsername());
    }

    //Nefunguje a ani nemůže :(
    @Transactional(rollbackFor = Exception.class)
    public ConversationUser saveConversationUser(ConversationUser conversationUser) {
        return conversationUserRepository.save(conversationUser);
    }

    @Transactional(rollbackFor = Exception.class)
    public Conversation saveConversation(Conversation conversation) {
        conversation = conversationRepository.save(conversation);
        conversation.getConversationUsers().forEach(conversationUser -> {
            userService.saveUser(conversationUser.getUser());
        });
        return conversation;
    }

    public ConversationPageResponseDto getConversationsByPage(PageInfoRequestWrapper conversationPageinfoRequestDto) {
        User user = userService.getUserFromContext();
        Pageable pageInfo = PageRequest.of(conversationPageinfoRequestDto.getPageIndex(), conversationPageinfoRequestDto.getPageSize()).withSort(Sort.Direction.DESC, "conversationId");
        return conversationsToConversationNameDtos(conversationRepository.getConversationsByUsername(user, pageInfo));
    }

    public GetConversationMessagesDtoResponse getConversationMessagesDtoResponse(GetConversationMessagesDto getConversationMessagesDto) {
        User user = userService.getUserFromContext();
        Conversation conversation = getConversationById(getConversationMessagesDto.getConversationId());
        Instant dateFrom = getConversationMessagesDto.getFrom();
        Instant dateTo = getConversationMessagesDto.getTo();
        if (dateFrom == null) dateFrom = Instant.from(LocalDateTime.of(2000, 1, 1, 1, 1).atOffset(ZoneOffset.UTC));
        if (dateTo == null) dateTo = Instant.MAX;
        if (conversation == null) throw new NotFoundException("Conversation is null");

        ConversationUser conversationUser = conversation.getConversationUserByUsername(user.getUsername());
        PageInfoDtoRequest pageInfoDtoRequest = getConversationMessagesDto.getPageInfo();
        Integer pageIndex = pageInfoDtoRequest == null ? 0 : pageInfoDtoRequest.getPageIndex();
        Integer pageSize = pageInfoDtoRequest == null ? Integer.MAX_VALUE : pageInfoDtoRequest.getPageSize();
        Pageable pageInfo = PageRequest.of(pageIndex, pageSize).withSort(Sort.Direction.DESC, "messageId");
        Page<Message> messages = messageRepository.getMessageByConversationBetweenDatesValidTo(pageInfo, conversation, dateFrom, dateTo, Instant.now(), conversationUser);
        //Todo je potreba?
        //getConversationMessagesDtoResponse.setCipheredSymmetricKey(conversationUser.getEncryptedSymmetricKey());
        GetConversationMessagesDtoResponse getConversationMessagesDtoResponse = mapGetConversationMessagesDtoResponse(messages);
        return getConversationMessagesDtoResponse;
    }

    @Transactional(rollbackFor = Exception.class)
    public ConversationNameDto createConversation(CreateConversationDto createConversationDto) {
        User creator = userService.getUserFromContext();
        Conversation conversation = new Conversation();
        conversation.setConversationName(createConversationDto.getName());
        Conversation updatedConversation = saveConversation(conversation);

        List<ConversationUser> conversationUsers = getConversationUsersFromDto(createConversationDto, updatedConversation);
        updatedConversation.getConversationUsers().addAll(conversationUsers);
        Conversation returnedConversation = saveConversation(updatedConversation);

        notifyUsersAboutNewConversationExceptUser(conversation, creator);
        return convertConversationToConversationNameDto(returnedConversation);
    }

    public void leaveFromConversation(LeaveConversationDto leaveConversationDto) {
        //TODO nebo pouzijeme boolean s neaktivnimi usery?
        User user = userService.getUserFromContext();
        Conversation conversation = getConversationById(leaveConversationDto.getConversationId());
        int sizeBeforeRemove = conversation.getConversationUsers().size();
        List<ConversationUser> conversationUsersToRemove = new ArrayList<>();
        conversation.getConversationUsers().removeIf(conversationUser -> {
            boolean remove = conversationUser.getUser().getUsername().equals(user.getUsername());
            if(remove) conversationUsersToRemove.add(conversationUser);
            return remove;
        });
        doRemoveUserAndCloseOrphanConversation(user, conversation, conversationUsersToRemove, sizeBeforeRemove);
    }

    @Transactional(rollbackFor = Exception.class)
    private void doRemoveUserAndCloseOrphanConversation(User user, Conversation conversation, List<ConversationUser> conversationUsersToRemove, int sizeBeforeRemove) {
        conversationUserRepository.deleteAll(conversationUsersToRemove);
        saveConversation(conversation);
        if (sizeBeforeRemove != conversation.getConversationUsers().size() + 1) {
            throw new RuntimeException("Exactly 1 element was supposed to be deleted");
        }
        notifyOtherConversationPartiesAboutLeave(user, conversation);
        if(conversation.getConversationUsers().size() <= 1) {
            deleteConversation(conversation);
            notifyOtherConversationPartiesAboutDelete(user, conversation);
        }
    }

    public void deleteUserConversation(ConversationNameDto conversationNameDto){
        User user = userService.getUserFromContext();
        Conversation conversation = getConversationById(conversationNameDto.getId());
        if(conversation.getConversationUsers().size() > 2) throw new UnauthorizedException("Can not delete group conversation");
        if(conversation.getConversationUsers().stream().noneMatch(conversationUser -> conversationUser.getUser().equals(user))){
            throw new UnauthorizedException("User is not a part of this conversation");
        }
        deleteConversation(conversation);
        notifyOtherConversationPartiesAboutDelete(user, conversation);
    }

    @Transactional(rollbackFor = Exception.class)
    private void deleteConversation(Conversation conversation) {
        conversationRepository.delete(conversation);
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

    private ConversationPageResponseDto conversationsToConversationNameDtos(Page<Conversation> page) {
        if (page == null) return null;
        List<ConversationNameDto> conversationDtos = page.getContent().stream().map(this::convertConversationToConversationNameDto).collect(Collectors.toList());

        ConversationPageResponseDto conversationPageResponseDto = new ConversationPageResponseDto();
        conversationPageResponseDto.setItemList(conversationDtos);
        conversationPageResponseDto.setPageInfoDto(new PageInfoDtoResponse(page.getSize(), page.getNumber(), page.getTotalElements()));
        return conversationPageResponseDto;
    }

    private ConversationNameDto convertConversationToConversationNameDto(Conversation conversation) {
        return new ConversationNameDto(conversation.getConversationId(), conversation.getConversationName());
    }

    private List<Conversation> conversationNameDtosToConversations(List<ConversationNameDto> conversations) {
        if (conversations == null) return null;
        return conversations.stream().map(user -> getConversationById(user.getId())).collect(Collectors.toList());
    }

    private List<MessageDto> convertMessagesToMessageDtos(List<Message> messages) {
        return messages.stream().map(this::convertMessageToMessageDto).collect(Collectors.toList());
    }

    private MessageDto convertMessageToMessageDto(Message message) {
        MessageDto messageDto = new MessageDto();
        messageDto.setConversationId(message.getConversation().getConversationId());
        messageDto.setId(message.getMessageId());
        messageDto.setMessage(message.getContent());
        messageDto.setSender(message.getSender().getUsername());
        messageDto.setDateSend(message.getDateSend());
        messageDto.setValidTo(message.getValidTo());
        try {
            HashMap<String, Integer> ivMap = (HashMap<String, Integer>) objectMapper.readValue(message.getInitiationVector(), Map.class);
            messageDto.setIv(ivMap);
        } catch (Exception e) {
            throw new RuntimeException("Failed to parse initiation vector");
        }
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
            HashMap<String, Integer> ivMap = (HashMap<String, Integer>) objectMapper.readValue(conversationUser.getInitiationVector(), Map.class);
            cipheredSymmetricKeysDto.setIv(ivMap);
            cipheredSymmetricKeysDto.setCipheringPublicKey(objectMapper.readValue(conversationUser.getCipheringPublicKey(), PublicKeyDto.class));
        } catch (Exception e) {
            throw new RuntimeException("Failed to deserialize key");
        }
        return cipheredSymmetricKeysDto;
    }

    private List<ConversationUser> getConversationUsersFromDto(CreateConversationDto createConversationDto, Conversation updatedConversation) {
        List<ConversationUser> conversationUsers = createConversationDto.getUsers().stream().map(cipheredSymmetricKeysDto -> getConversationUserFromCipheredSymmetricKeyDto(updatedConversation, cipheredSymmetricKeysDto)).collect(Collectors.toList());
        return conversationUsers;
    }

    private ConversationUser getConversationUserFromCipheredSymmetricKeyDto(Conversation updatedConversation, CipheredSymmetricKeysDto cipheredSymmetricKeysDto) {
        String publicKeyDtoAsString = null;
        String initiationVectorAsString = null;
        try {
            publicKeyDtoAsString = objectMapper.writeValueAsString(cipheredSymmetricKeysDto.getCipheringPublicKey());
            initiationVectorAsString = objectMapper.writeValueAsString(cipheredSymmetricKeysDto.getIv());
        } catch (JsonProcessingException e) {
            throw new RuntimeException("Failed to parse public key as string");
        }
        return new ConversationUser(userService.getUserByUsername(cipheredSymmetricKeysDto.getUsername()), updatedConversation, cipheredSymmetricKeysDto.getEncryptedSymmetricKey(), publicKeyDtoAsString, initiationVectorAsString);
    }
}
