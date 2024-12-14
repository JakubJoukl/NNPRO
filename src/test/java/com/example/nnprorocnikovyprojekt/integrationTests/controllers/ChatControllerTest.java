package com.example.nnprorocnikovyprojekt.integrationTests.controllers;

import com.example.nnprorocnikovyprojekt.config.CommonTestParent;
import com.example.nnprorocnikovyprojekt.config.WithCustomUser;
import com.example.nnprorocnikovyprojekt.entity.*;
import com.example.nnprorocnikovyprojekt.external.CaptchaService;
import com.example.nnprorocnikovyprojekt.services.ConversationService;
import com.example.nnprorocnikovyprojekt.services.UserService;
import com.fasterxml.jackson.databind.ObjectMapper;
import jakarta.persistence.EntityManager;
import org.junit.jupiter.api.AfterEach;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.mockito.Mock;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.AutoConfigureMockMvc;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.test.context.ActiveProfiles;
import org.springframework.test.web.servlet.MockMvc;
import org.springframework.transaction.annotation.Isolation;
import org.springframework.transaction.annotation.Transactional;

import java.time.Instant;
import java.time.LocalDateTime;
import java.time.ZoneOffset;
import java.util.ArrayList;
import java.util.List;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.when;

@SpringBootTest
@AutoConfigureMockMvc
@ActiveProfiles("test")
@WithCustomUser(username = "Franta BU", roles = {"USER"})
@Transactional(rollbackFor = Exception.class, isolation = Isolation.READ_UNCOMMITTED)
class ChatControllerTest {

    User testUser1 = null;
    User testUser2 = null;
    User testUser3 = null;
    Conversation conversation1 = null;

    @Autowired
    private UserService userService;

    @Autowired
    private ConversationService conversationService;

    @Autowired
    private MockMvc mockMvc;

    @Autowired
    private EntityManager entityManager;

    @Autowired
    private ObjectMapper objectMapper;

    @Mock
    private CaptchaService captchaService;

    @BeforeEach
    void setUp() {
        userService.setCaptchaService(captchaService);
        /*testUser = new User("testUser", "veliceSilneHeslo9", "testUserMail@test.cz");
        userService.saveUser(testUser);
        testUser2 = new User("testUser2", "veliceHesloSilne9", "testUser2Mail@test.cz");
        userService.saveUser(testUser2);
        testUser3 = new User("testUser3", "hesloTretihoUzivatele3", "testUser3Mail@test.cz");
        userService.saveUser(testUser3);

        conversation1 = new Conversation();
        conversation1.setConversationName("Skupinova konverzace");*/
        //conversationService.addUserToConversation();
        when(captchaService.validateCaptcha(any())).thenReturn(true);
        List<User> users = getCompleteTestUsers();
        getTestUser3();
        //conversationUser zajistuje sve pridani do konverzaci a useru...

        //TODO
        String encryptedSymmetricKey1 = "esI0rgLrX77V8oBul4M1bK6mr+oMlv1c2NZm0qvGvHNMt01Dppbvi3treixtH2s2";
        String iv1 = "{\"0\":215,\"11\":138,\"1\":67,\"2\":206,\"3\":117,\"4\":65,\"5\":18,\"6\":83,\"7\":136,\"8\":11,\"9\":128,\"10\":159}";
        ConversationUser conversationUser = new ConversationUser(testUser1, conversation1, encryptedSymmetricKey1, testUser1.getActivePublicKey().get().getKeyValue(), iv1);
        conversation1.getConversationUsers().add(conversationUser);

        String encryptedSymmetricKey2 = "eL/CQBPB2821pyX2g1FyDoqufCoC0qHtKAsLbq3CsvEgM6F4CGzoxQD7WpX2h5/i";
        String iv2 = "{\"0\":88,\"11\":135,\"1\":36,\"2\":241,\"3\":32,\"4\":173,\"5\":245,\"6\":18,\"7\":200,\"8\":107,\"9\":227,\"10\":223}";
        ConversationUser conversationUser2 = new ConversationUser(testUser2, conversation1, encryptedSymmetricKey2, testUser2.getActivePublicKey().get().getKeyValue(), iv2);
        conversation1.getConversationUsers().add(conversationUser2);
        conversationService.saveConversation(conversation1);
    }

    @AfterEach
    void tearDown() {

    }

    @Test
    void chat() {

    }

    @Test
    void listUserConversation() {

    }

    @Test
    void addUserToConversation() {

    }

    @Test
    void removeUserFromConversation() {

    }

    @Test
    void listMessages() {

    }

    @Test
    void getConversation() {

    }

    @Test
    void createConversation() {

    }

    @Test
    void deleteMessage() {

    }

    @Test
    void deleteConversations() {

    }

    public User getTestUser3() {
        String username = "Treti uzivatel";
        String password = "$2a$10$byBqYIgMd5UQ9UJYOMOeseo39Z3pvjLBfvFohH4zTTLFpFFuYEcei";
        String email = "treti@uzivatel.cz";
        User user = new User(username, password, email);

        String keyValue = "{\"crv\":\"P-521\",\"ext\":true,\"kty\":\"EC\",\"keyOps\":null,\"x\":\"AUmVOV24lL1TYr5Opok7--_uXwJVf8cLwp0cPrUEUWVQTqji6dioEJN-ejrPJ9-XVOAFhZpYwstHGWL4JR6ybmSp\",\"y\":\"ANx2hgmdAtPgAVt3GzETSH2x0yFHTpVM8K6qeGP0GrStWmRAOQ6EiyO3ZJFnTlypG_Qf6OitmhmIi24bilJ__pyO\"}";
        Instant creationDate = Instant.from(LocalDateTime.of(2024, 11, 22, 0, 0).atOffset(ZoneOffset.UTC));
        boolean valid = true;
        user.getPublicKeys().add(new PublicKey(keyValue, creationDate, valid, user));
        this.testUser3 = userService.saveUser(user);
        return user;
    }

    public List<User> getCompleteTestUsers(){
        List<User> users = new ArrayList<>();
        User user1 = CommonTestParent.getTestUser();
        user1.setUserId(null);
        user1.getContacts().forEach(user -> user.setUserId(null));
        User user2 = CommonTestParent.getTestUser2();
        user2.setUserId(null);
        user2.getContacts().forEach(user -> user.setUserId(null));
        users.add(user1);
        users.add(user2);

        user1.getContacts().add(user2);
        user2.getContacts().add(user1);

        this.testUser1 = userService.saveUser(user1);
        this.testUser2 = userService.saveUser(user2);

        Conversation conversation = CommonTestParent.getTestConversation().get();
        conversation.setConversationId(null);
        conversationService.saveConversation(conversation);
        List<Message> messages = CommonTestParent.createTestMessages(testUser1, conversation);
        messages.forEach(message -> message.setMessageId(null));
        conversation.getMessages().addAll(messages);

        conversation1 = conversationService.saveConversation(conversation);
        return users;
    }
}