package com.example.nnprorocnikovyprojekt.integrationTests.controllers;

import com.example.nnprorocnikovyprojekt.config.CommonTestParent;
import com.example.nnprorocnikovyprojekt.config.WithCustomUser;
import com.example.nnprorocnikovyprojekt.dtos.conversation.AddRemoveUserToConversationDto;
import com.example.nnprorocnikovyprojekt.dtos.conversation.MessageDto;
import com.example.nnprorocnikovyprojekt.dtos.general.GeneralResponseDto;
import com.example.nnprorocnikovyprojekt.dtos.pageinfo.PageInfoRequestWrapper;
import com.example.nnprorocnikovyprojekt.dtos.user.LoginDto;
import com.example.nnprorocnikovyprojekt.entity.*;
import com.example.nnprorocnikovyprojekt.external.CaptchaService;
import com.example.nnprorocnikovyprojekt.services.ConversationService;
import com.example.nnprorocnikovyprojekt.services.UserService;
import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.databind.ObjectMapper;
import jakarta.persistence.EntityManager;
import org.hamcrest.Matchers;
import org.junit.jupiter.api.AfterEach;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.mockito.Mock;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.AutoConfigureMockMvc;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.http.MediaType;
import org.springframework.messaging.converter.MappingJackson2MessageConverter;
import org.springframework.messaging.simp.stomp.StompFrameHandler;
import org.springframework.messaging.simp.stomp.StompHeaders;
import org.springframework.messaging.simp.stomp.StompSession;
import org.springframework.messaging.simp.stomp.StompSessionHandlerAdapter;
import org.springframework.test.context.ActiveProfiles;
import org.springframework.test.web.servlet.MockMvc;
import org.springframework.transaction.annotation.Isolation;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.web.context.WebApplicationContext;
import org.springframework.web.socket.client.standard.StandardWebSocketClient;
import org.springframework.web.socket.messaging.WebSocketStompClient;
import org.springframework.web.socket.sockjs.client.SockJsClient;
import org.springframework.web.socket.sockjs.client.WebSocketTransport;

import java.lang.reflect.Type;
import java.security.Principal;
import java.time.Instant;
import java.time.LocalDateTime;
import java.time.ZoneOffset;
import java.util.ArrayList;
import java.util.Collections;
import java.util.HashMap;
import java.util.List;
import java.util.concurrent.ExecutionException;

import static org.hamcrest.Matchers.is;
import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.ArgumentMatchers.eq;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;
import static org.mockito.internal.verification.VerificationModeFactory.times;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.post;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.jsonPath;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;

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


    @Autowired
    private WebApplicationContext context;

    private WebSocketStompClient stompClient;

    private StompSession stompSession;

    @BeforeEach
    void setUp() throws ExecutionException, InterruptedException {
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

        String encryptedSymmetricKey1 = "esI0rgLrX77V8oBul4M1bK6mr+oMlv1c2NZm0qvGvHNMt01Dppbvi3treixtH2s2";
        String iv1 = "{\"0\":215,\"11\":138,\"1\":67,\"2\":206,\"3\":117,\"4\":65,\"5\":18,\"6\":83,\"7\":136,\"8\":11,\"9\":128,\"10\":159}";
        ConversationUser conversationUser = new ConversationUser(testUser1, conversation1, encryptedSymmetricKey1, testUser1.getActivePublicKey().get().getKeyValue(), iv1);
        conversation1.getConversationUsers().add(conversationUser);

        String encryptedSymmetricKey2 = "eL/CQBPB2821pyX2g1FyDoqufCoC0qHtKAsLbq3CsvEgM6F4CGzoxQD7WpX2h5/i";
        String iv2 = "{\"0\":88,\"11\":135,\"1\":36,\"2\":241,\"3\":32,\"4\":173,\"5\":245,\"6\":18,\"7\":200,\"8\":107,\"9\":227,\"10\":223}";
        ConversationUser conversationUser2 = new ConversationUser(testUser2, conversation1, encryptedSymmetricKey2, testUser2.getActivePublicKey().get().getKeyValue(), iv2);
        conversation1.getConversationUsers().add(conversationUser2);
        conversation1 = conversationService.saveConversation(conversation1);
        testUser1 = userService.saveUser(testUser1);
        testUser2 = userService.saveUser(testUser2);

        /*stompClient = new WebSocketStompClient(new SockJsClient(
                Collections.singletonList(new WebSocketTransport(new StandardWebSocketClient()))));
        stompClient.setMessageConverter(new MappingJackson2MessageConverter());

        stompSession = stompClient.connect("https://localhost:8080/chat", new StompSessionHandlerAdapter() {}).get();*/
    }

    @AfterEach
    void tearDown() {

    }

    @Test
    void chat() throws Exception {
        //TODO server ale nebezi...
        /*MessageDto messageDto = new MessageDto();
        messageDto.setId(null);
        messageDto.setConversationId(conversation1.getConversationId());
        messageDto.setIv(new HashMap<>());
        messageDto.setSender(testUser1.getUsername());
        messageDto.setMessage("VK6tbccZiQAlpbSBHWIBrBCsHA==");

        // Přihlášení k cíli, kam se zprávy odesílají
        stompSession.subscribe("/topic/addMessage/" + conversation1.getConversationId(), new StompFrameHandler() {
            @Override
            public Type getPayloadType(StompHeaders headers) {
                return GeneralResponseDto.class;
            }

            @Override
            public void handleFrame(StompHeaders headers, Object payload) {
                GeneralResponseDto response = (GeneralResponseDto) payload;
                assertEquals("Message processed, receivers notified", response.getContent());
            }
        });

        stompSession.send("/app/sendMessageToConversation", messageDto);

        Thread.sleep(1000);*/
    }

    @Test
    void listUserConversation() throws Exception {
        PageInfoRequestWrapper pageInfoRequestWrapper = new PageInfoRequestWrapper();
        pageInfoRequestWrapper.setPageSize(50);
        pageInfoRequestWrapper.setPageIndex(0);
        //{"username":"SuperSailorMoon","password":"SuperSailorMoon","captchaToken":"03AFcWeA7ACTLMrtgpCcnOURLNxCoFTIDw2xmbUNTRlEz4Mv0dmZHZnCaUUY58xpJqwhSLkHygdYk7gTjJ74ktMIbCmI9Wa6m9Ii1oBK3MZvG7FplaBDKl6yTDDlLoDnzAbVV2k5ucpNsXN7SfBczsB631uxgacC0Sekvh3Z_XC-HOeVmwAt7hK7U3D5KMZDY4LxgA-iGqxGyftiq_jPsU45fF-vanBlEZrt-cSGzGJWJkEK2VFtMSFncAcXML_SH81tRR0LZ8Ib1PZyYnV1Lamm2d8VGJs_I52e43KMrRDJnBeZvjBClt8BfgfOq6WC87PLtrtSXJ9_Y6FVldwmnHaEGZz-9b8SsHxbkCWL-O6bR8Van4hpKGldm77r_Mkd-FyJ01WmoRHbXDVoWUu0AK-ZFapciSrtiFg_bISq6OJCLDSB7Z47thORVVPB1I7IW4QnsvC0bjhKS2hEKuvv_lVpxD13CJz9RzZH_INvbysA5FBRFNEoPuhVJo1lljX0gz76uf3KZdbnB1XH5pOlzOneQ1JqiwT0Z84h1mYIrC8OIjMg9a-e749ytPn-pSf0GX2HB0A4sPb7eQS0KtX1Eydn96Y18Vg3NEbQGc-z092PzygzoNipgCVnsepdwQC79dy7bCnQW9iVmdPTT_jYjg7MMBhA76bSZ_F_McRu3Z4xoGXeOOTg5TVivKHw8mROlUfQR94D6jrq4pAOftxbrUOu013smBBHfOeCy2zW6r71h54b0-R1Eld-V_aVtypZvmUYcXRcHvvzhUBPriezh3ArywxY59zu3mPnyvfhCX2GppujxPS2pvzfosxl2Qz4E5A8XMHiLT6lmCqLoqDSCE4cgUU1JCerxJMdR86RH8Giep6vo6loZFLfYj5Fturdm22dr3BB3tc-otXCwAM3BPCGDvH6Q1ZuFBF30bOFXYta43b2cT0nSeB2M_7bOlY68z3OkaRjzMv0EOyB9LXtZcqpz34BxaAAvrEPbUIZiTdHq_MOqzZpbUP7pAKDALo_A_6ljfm78EwAwn7EzlU_6cYYgggLtFUMUBFQB-B4QXR3SjG55TraqoFgDI9lfP880jBdbyO0FrM-5AgjKLNY3ndaTgDPSOeUqAgtswGGQZaLSmJw2gzTGlO-m9qBLNeJs5tNJHGa86mFdlMofg24m44J8jsL_3gUNbxrw-x43d_EOCKvPAMSc6UNdfEMt34zoj41uBIAHPakYi_9TPY459thkRGOYTbelF_WqntF3k1hRp8B8TWK0H1qMzMCLqXrZPlPqE0EWniWoQRCFQ36yhVDy2DnWyjxMTws0vBQNFG0wvERmbB0yyQb3m77bkG_HTMJ0pcjgHq5OA7TI1fVfrUGt0Mfu9cIzDRMZIgIDY16k-Lkdluk6DV1qFyklTrpsBbx41x5PWI-WksKkUpy8u4OLL-9iGvylRX-dhg0IVtE3D195ik6BdsyqGea3RxSrKz4_bXbgLhsAYMBlkhjXKqnrQqMAc1g_GJmIeuWhd2YNiIvwe-pEAP5udp796s24aB4iEdKNZpV1wf7Jth3uS4El_jznDXC6FJAESeBXVnWi_KAAn-3Ez2yDLvDwHmMQl5jXlruqLjeGm8VhpubSy_DDQuKuAKhTLiXVf8dw8CDZZAUL3ate7UQ77PZPbATsWlREjWI7KKscAGPYAcVM1_uEe_MPpvR7UZiE__q_901586tl6vUdPhhsk0QYVrFFyvEhgZxYM8nN3CS_GxyXi_-GOZXKfazIkQh4OYirlbDVH5dRhBe6fWvhPlFTVpPbtwSxmsjg6SgHeRovPFGhXWZhpCxUAhwTRxpjZjwWsjaEg9pTOuJRfPcYoVGgFsfVU6OboxiLPcg8MhLfcCrKPKiZWuj6YYiCLUFXD7E1K2dLC2n3zE7x-7asa483K481uuFz83xmjqOnAHhSBDi8Wy7fUlrbFzOki5eMg49JVBMaQUP8-X6m3-QStBnbHcnyhN4D1j2cge9nnzvUfuriTKlEMW_JvJulSErgzg0X0FnbigbXG5eXV4oW1WOXmBmJG4xjfXugejpP-9dkKXRZkev5SZt9Woh0SnGyhr3Yil7RHdajtl4H3y5kOmhdI0g7sUrSwd8oGwkcqKdYSPourbzTLW_BD4yzHXSS_s-xH9rGcAraIWL0EB3U"}

        VerificationCode verificationCode = testUser1.getActiveVerificationCode();

        // Serializace do JSON
        String requestBody = objectMapper.writeValueAsString(pageInfoRequestWrapper);

        mockMvc.perform(post("https://localhost:8080/listUserConversation")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(requestBody))
                .andExpect(status().isOk()).andExpect(jsonPath("$.itemList[0].id", is(conversation1.getConversationId())))
                .andExpect(jsonPath("$.itemList[0].name", is(conversation1.getConversationName())));
    }

    @Test
    void addUserToConversation() throws Exception {
        /*AddRemoveUserToConversationDto addRemoveUserToConversationDto = new AddRemoveUserToConversationDto();
        addRemoveUserToConversationDto.setUser(testUser3.getUsername());
        addRemoveUserToConversationDto.setConversationId(conversation1.getConversationId());
        addRemoveUserToConversationDto.setCipheringPublicKey(null);
        addRemoveUserToConversationDto.setEncryptedSymmetricKey(null);

        // Serializace do JSON
        String requestBody = objectMapper.writeValueAsString(addRemoveUserToConversationDto);

        mockMvc.perform(post("https://localhost:8080/addUserToConversation")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(requestBody))
                .andExpect(status().isOk()).andExpect(jsonPath("$.itemList[0].id", is(conversation1.getConversationId())))
                .andExpect(jsonPath("$.itemList[0].name", is(conversation1.getConversationName())));*/
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