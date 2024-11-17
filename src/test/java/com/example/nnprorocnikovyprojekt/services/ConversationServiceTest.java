package com.example.nnprorocnikovyprojekt.services;

import com.example.nnprorocnikovyprojekt.dtos.conversation.*;
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
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageImpl;
import org.springframework.data.domain.Pageable;

import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.List;
import java.util.Optional;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.Mockito.*;

@ExtendWith(MockitoExtension.class)
@SpringBootTest
class ConversationServiceTest {
    @Mock
    private UserService userService;

    @InjectMocks
    private ConversationService conversationService;

    @Mock
    private ConversationRepository conversationRepository;

    @Mock
    private ConversationUserRepository conversationUserRepository;

    @Mock
    private MessageRepository messageRepository;

    private ObjectMapper objectMapper = new ObjectMapper();

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
        //TODO
    }

    @Test
    void addUserToConversation() throws JsonProcessingException {
        Optional<Conversation> conversationOptional = getTestConversation();
        User user = getTestUser();
        AddRemoveUserToConversationDto addRemoveUserToConversationDto = new AddRemoveUserToConversationDto();
        CipheredSymmetricKeysDto cipheredSymmetricKeysDto = new CipheredSymmetricKeysDto();
        cipheredSymmetricKeysDto.setUsername(user.getUsername());
        addRemoveUserToConversationDto.setUser(cipheredSymmetricKeysDto);
        addRemoveUserToConversationDto.setConversationId(conversationOptional.get().getConversationId());

        when(conversationRepository.getConversationByConversationId(1)).thenReturn(conversationOptional);
        when(userService.getUserByUsername(user.getUsername())).thenReturn(user);
        PublicKeyDto expectedResponse = objectMapper.readValue(user.getActivePublicKey().get().getKeyValue(), PublicKeyDto.class);

        AddUserToConversationResponse addUserToConversationResponse = conversationService.addUserToConversation(addRemoveUserToConversationDto);
        assertEquals(expectedResponse.getCrv(), addUserToConversationResponse.getPublicKey().getCrv());
        assertEquals(expectedResponse.getExt(), addUserToConversationResponse.getPublicKey().getExt());
        assertNull(addUserToConversationResponse.getPublicKey().getKeyOps());
        assertEquals(expectedResponse.getKty(), addUserToConversationResponse.getPublicKey().getKty());
        assertEquals(expectedResponse.getX(), addUserToConversationResponse.getPublicKey().getX());
        assertEquals(expectedResponse.getY(), addUserToConversationResponse.getPublicKey().getY());
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
        Optional<Conversation> conversationOptional = getTestConversation();
        Conversation conversation = conversationOptional.get();
        User user = getTestUser();
        ConversationUser conversationUser = new ConversationUser(user, conversation, "x", null, null);
        conversation.getConversationUsers().add(conversationUser);

        GetConversationMessagesDto getConversationMessagesDto = new GetConversationMessagesDto();

        LocalDateTime from = LocalDateTime.of(2024, 11, 16, 0, 0);
        LocalDateTime to = LocalDateTime.of(2024, 11, 17, 0, 0);

        getConversationMessagesDto.setFrom(from);
        getConversationMessagesDto.setTo(to);
        getConversationMessagesDto.setConversationId(conversation.getConversationId());

        List<Message> messages = new ArrayList<>();
        String content = "q";
        String content2 = "qq";
        messages.add(new Message(user, conversation, content));
        messages.add(new Message(user, conversation, content2));

        when(messageRepository.getMessageByConversationAndDateSendIsBetween(conversation, from, to)).thenReturn(messages);
        when(conversationRepository.getConversationByConversationId(conversation.getConversationId())).thenReturn(conversationOptional);
        when(userService.getUserFromContext()).thenReturn(user);

        conversationService.getConversationMessagesDtoResponse(getConversationMessagesDto);
    }

    @Test
    void createConversation() {
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
    void removeUserFromConversation() {
        User user = getTestUser();
        Optional<Conversation> conversation = getTestConversation();
        ConversationUser conversationUser = new ConversationUser(user, conversation.get(), "klic", null, null);
        conversation.get().getConversationUsers().add(conversationUser);

        LeaveConversationDto leaveConversationDto = new LeaveConversationDto();
        leaveConversationDto.setUsername(user.getUsername());
        leaveConversationDto.setConversationId(conversation.get().getConversationId());

        when(conversationRepository.getConversationByConversationId(conversation.get().getConversationId())).thenReturn(conversation);

        conversationService.removeUserFromConversation(leaveConversationDto);
    }

    private User getTestUser() {
        String username = "Franta BU";
        String password = "Zasifrovane heslo";
        String email = "franta@bu.cz";
        User user = new User(username, password, email);

        String keyValue = "{\"crv\":\"P-256\",\"ext\":true,\"kty\":\"EC\",\"keyOps\":null,\"x\":\"FOHS2BTBzSBnu7V0LDJYXt30rR08B1UGYR_O5fhAcnM\",\"y\":\"Wb0ZaxmFYct3vm61zkAGyk4JPXPc3bPp1-uAEEJbxBM\"}";
        LocalDateTime creationDate = LocalDateTime.of(2024, 11, 16, 0, 0);
        boolean valid = true;
        user.getPublicKeys().add(new PublicKey(keyValue, creationDate, valid, user));
        return user;
    }

    private Optional<Conversation> getTestConversation() {
        Conversation conversation = new Conversation();
        String conversationName = "Konverzace";
        Integer conversationId = 1;

        conversation.setConversationName(conversationName);
        conversation.setConversationId(conversationId);
        Optional<Conversation> conversationOptional = Optional.of(conversation);
        return conversationOptional;
    }
}