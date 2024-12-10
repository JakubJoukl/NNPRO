package com.example.nnprorocnikovyprojekt.services;

import com.example.nnprorocnikovyprojekt.config.CommonTestParent;
import com.example.nnprorocnikovyprojekt.dtos.conversation.*;
import com.example.nnprorocnikovyprojekt.dtos.pageinfo.PageInfoDtoRequest;
import com.example.nnprorocnikovyprojekt.dtos.pageinfo.PageInfoRequestWrapper;
import com.example.nnprorocnikovyprojekt.dtos.user.PublicKeyDto;
import com.example.nnprorocnikovyprojekt.entity.*;
import com.example.nnprorocnikovyprojekt.repositories.ConversationRepository;
import com.example.nnprorocnikovyprojekt.repositories.ConversationUserRepository;
import com.example.nnprorocnikovyprojekt.repositories.MessageRepository;
import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.databind.ObjectMapper;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.data.domain.*;
import org.springframework.messaging.simp.SimpMessagingTemplate;
import org.springframework.test.context.ActiveProfiles;

import java.time.Instant;
import java.time.LocalDateTime;
import java.time.ZoneOffset;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Optional;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.Mockito.*;

class ConversationServiceTest extends CommonTestParent {
    @Mock
    private UserService userService;

    @InjectMocks
    private ConversationService conversationService;

    @Mock
    private ConversationRepository conversationRepository;

    @Mock
    private MessageService messageService;

    @Mock
    private ConversationUserRepository conversationUserRepository;

    @Mock
    private MessageRepository messageRepository;

    @Autowired
    private ObjectMapper objectMapper;

    @Mock
    private SimpMessagingTemplate simpMessagingTemplate;

    @Test
    void getConversationById() {
        Optional<Conversation> conversationOptional = getTestConversation();
        Conversation conversation = conversationOptional.get();
        when(conversationRepository.getConversationByConversationId(1)).thenReturn(conversationOptional);
        Conversation returnedConversation = conversationService.getConversationById(conversation.getConversationId());
        assertEquals(conversation.getConversationId(), returnedConversation.getConversationId());
        assertEquals(conversation.getConversationName(), returnedConversation.getConversationName());
    }

    @Test
    void sendMessageToAllSubscribersExceptUser() {
        //TODO Sockety netestujeme?
    }

    @Test
    void addUserToConversation() throws JsonProcessingException {
        conversationService.setObjectMapper(objectMapper);
        Optional<Conversation> conversationOptional = getTestConversation();
        User user = getTestUser();
        AddRemoveUserToConversationDto addRemoveUserToConversationDto = new AddRemoveUserToConversationDto();
        addRemoveUserToConversationDto.setUser(user.getUsername());
        addRemoveUserToConversationDto.setConversationId(conversationOptional.get().getConversationId());

        when(conversationRepository.getConversationByConversationId(1)).thenReturn(conversationOptional);
        when(userService.getUserByUsername(user.getUsername())).thenReturn(user);
        PublicKeyDto expectedResponse = objectMapper.readValue(user.getActivePublicKey().get().getKeyValue(), PublicKeyDto.class);

        AddUserToConversationResponse addUserToConversationResponse = conversationService.addUserToConversation(addRemoveUserToConversationDto);
        assertEquals(expectedResponse.getCrv(), addUserToConversationResponse.getCipheringPublicKey().getCrv());
        assertEquals(expectedResponse.getExt(), addUserToConversationResponse.getCipheringPublicKey().getExt());
        assertNull(addUserToConversationResponse.getCipheringPublicKey().getKeyOps());
        assertEquals(expectedResponse.getKty(), addUserToConversationResponse.getCipheringPublicKey().getKty());
        assertEquals(expectedResponse.getX(), addUserToConversationResponse.getCipheringPublicKey().getX());
        assertEquals(expectedResponse.getY(), addUserToConversationResponse.getCipheringPublicKey().getY());
    }

    @Test
    void getConversationsByPage() {
        User user = getTestUser();
        Conversation conversation = getTestConversation().get();

        PageInfoRequestWrapper pageInfoRequestWrapper = new PageInfoRequestWrapper();
        pageInfoRequestWrapper.setPageIndex(0);
        pageInfoRequestWrapper.setPageSize(50);

        List<Conversation> conversations = new ArrayList<>();
        conversations.add(conversation);

        Page<Conversation> pagedResponse = new PageImpl(conversations);

        when(userService.getUserFromContext()).thenReturn(user);
        when(conversationRepository.getConversationsByUsername(eq(user), any(Pageable.class))).thenReturn(pagedResponse);

        ConversationPageResponseDto conversationPageResponseDto = conversationService.getConversationsByPage(pageInfoRequestWrapper);
        assertEquals(conversationPageResponseDto.getItemList().size(), conversations.size());
        assertEquals(conversationPageResponseDto.getItemList().get(0).getName(), conversations.get(0).getConversationName());
    }

    @Test
    void getConversationMessagesDtoResponse() {
        conversationService.setObjectMapper(objectMapper);
        Optional<Conversation> conversationOptional = getTestConversation();
        Conversation conversation = conversationOptional.get();
        User user = getTestUser();
        ConversationUser conversationUser = new ConversationUser(user, conversation, "x", null, null);
        conversation.getConversationUsers().add(conversationUser);

        GetConversationMessagesDto getConversationMessagesDto = new GetConversationMessagesDto();

        Instant from = Instant.from(LocalDateTime.of(2024, 11, 16, 0, 0).atOffset(ZoneOffset.UTC));
        Instant to = Instant.from(LocalDateTime.of(2024, 11, 17, 0, 0).atOffset(ZoneOffset.UTC));
        Instant validTo = Instant.from(LocalDateTime.of(2024, 11, 18, 0, 0).atOffset(ZoneOffset.UTC));
        PageInfoDtoRequest request = new PageInfoDtoRequest();
        request.setPageIndex(0);
        request.setPageSize(50);

        getConversationMessagesDto.setFrom(from);
        getConversationMessagesDto.setTo(to);
        getConversationMessagesDto.setPageInfo(request);
        getConversationMessagesDto.setConversationId(conversation.getConversationId());

        Pageable pageInfo = PageRequest.of(0, 50).withSort(Sort.Direction.DESC, "messageId");
        List<Message> messages = new ArrayList<>();
        String content = "q";
        String content2 = "qq";
        HashMap<String, Integer> iv = new HashMap<>();
        iv.put("1", 5);
        String initiationVector = null;
        try {
            initiationVector = objectMapper.writeValueAsString(iv);
        } catch (JsonProcessingException e) {
            throw new RuntimeException("OOPS");
        }
        messages.add(new Message(user, conversation, content, null, initiationVector));
        messages.add(new Message(user, conversation, content2, null, initiationVector));
        Page<Message> pagedResponse = new PageImpl(messages);

        when(messageRepository.getMessageByConversationBetweenDatesValidTo(eq(pageInfo), eq(conversation), eq(from), eq(to), any(Instant.class), eq(conversationUser))).thenReturn(pagedResponse);
        when(conversationRepository.getConversationByConversationId(conversation.getConversationId())).thenReturn(conversationOptional);
        when(userService.getUserFromContext()).thenReturn(user);

        conversationService.getConversationMessagesDtoResponse(getConversationMessagesDto);
        //TODO FIX
    }

    @Test
    void createConversation() {
        conversationService.setObjectMapper(objectMapper);
        CreateConversationDto createConversationDto = new CreateConversationDto();
        String name = "Konverzace";
        createConversationDto.setName(name);

        CipheredSymmetricKeysDto user1 = new CipheredSymmetricKeysDto("Alice", "x");
        CipheredSymmetricKeysDto user2 = new CipheredSymmetricKeysDto("Bob", "x");
        List<CipheredSymmetricKeysDto> users = new ArrayList<>();
        users.add(user1);
        users.add(user2);

        User alice = new User(user1.getUsername(), "x", "x");
        alice.setUserId(1);
        User bob = new User(user2.getUsername(), "x", "x");
        bob.setUserId(2);

        createConversationDto.setUsers(users);

        when(conversationRepository.save(any(Conversation.class))).thenAnswer(invocation -> invocation.getArgument(0));
        when(userService.getUserByUsername(user1.getUsername())).thenReturn(alice);
        when(userService.getUserByUsername(user2.getUsername())).thenReturn(bob);

        ConversationNameDto response = conversationService.createConversation(createConversationDto);
        assertEquals(response.getName(), name);
    }

    @Test
    void leaveConversation() {
        User user = getCompleteTestUsers().get(0);
        Optional<Conversation> conversation = Optional.of(user.getConversationUsers().get(0).getConversation());
        ConversationUser conversationUser = conversation.get().getConversationUserByUsername(user.getUsername());

        LeaveConversationDto leaveConversationDto = new LeaveConversationDto();
        leaveConversationDto.setConversationId(conversation.get().getConversationId());

        when(conversationRepository.getConversationByConversationId(conversation.get().getConversationId())).thenReturn(conversation);
        when(conversationRepository.save(any())).then(invocationOnMock -> {
                    conversation.get().getConversationUsers().remove(conversationUser);
                    return conversation.get();
                }
        );
        when(userService.getUserFromContext()).thenReturn(user);
        conversationService.leaveFromConversation(leaveConversationDto);
        assertFalse(conversation.get().getConversationUsers().contains(conversationUser));
    }

    @Test
    void deleteMessage() {
        conversationService.setObjectMapper(objectMapper);
        List<User> users = getCompleteTestUsers();
        User user1 = users.get(0);
        Message message1 = user1.getConversationUsers().get(0).getConversation().getMessages().get(0);
        DeleteMessageDto deleteMessageDto = new DeleteMessageDto();
        deleteMessageDto.setId(1);
        when(userService.getUserFromContext()).thenReturn(user1);
        when(messageService.getMessageById(1)).thenReturn(message1);
        conversationService.deleteMessage(deleteMessageDto);
    }

    @Test
    void testDeleteUserConversation() {
        conversationService.setObjectMapper(objectMapper);
        List<User> users = getCompleteTestUsers();
        ConversationNameDto conversationNameDto = new ConversationNameDto();
        conversationNameDto.setName("Konverzace");
        conversationNameDto.setId(1);
        when(conversationRepository.getConversationByConversationId(1)).thenReturn(Optional.of(users.get(0).getConversationUsers().get(0).getConversation()));
        when(userService.getUserFromContext()).thenReturn(users.get(0));
        conversationService.deleteUserConversation(conversationNameDto);
    }
}