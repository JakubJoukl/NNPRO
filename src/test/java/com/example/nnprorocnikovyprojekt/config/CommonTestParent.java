package com.example.nnprorocnikovyprojekt.config;

import com.example.nnprorocnikovyprojekt.entity.*;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.junit.jupiter.MockitoExtension;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.test.context.ActiveProfiles;

import java.time.Instant;
import java.time.LocalDateTime;
import java.time.ZoneOffset;
import java.util.ArrayList;
import java.util.List;
import java.util.Optional;

@ExtendWith(MockitoExtension.class)
@ActiveProfiles("test")
@SpringBootTest
public class CommonTestParent {

    public static User getTestUser() {
        String username = "Franta BU";
        String password = "$2a$10$byBqYIgMd5UQ9UJYOMOeseo39Z3pvjLBfvFohH4zTTLFpFFuYEcei"; //FrantaBuFrantaBu
        String email = "frantaaaaaaaaaaaaa@bu.cz";
        User user = new User(username, password, email);
        user.setUserId(1);

        String keyValue = "{\"crv\":\"P-256\",\"ext\":true,\"kty\":\"EC\",\"keyOps\":null,\"x\":\"FOHS2BTBzSBnu7V0LDJYXt30rR08B1UGYR_O5fhAcnM\",\"y\":\"Wb0ZaxmFYct3vm61zkAGyk4JPXPc3bPp1-uAEEJbxBM\"}";
        Instant creationDate = Instant.from(LocalDateTime.of(2024, 11, 16, 0, 0).atOffset(ZoneOffset.UTC));
        boolean valid = true;
        user.getPublicKeys().add(new PublicKey(keyValue, creationDate, valid, user));

        Instant expirationDate = Instant.now().plusSeconds(3600 * 5);
        ResetToken resetToken = new ResetToken(user, "tokennn");
        resetToken.setExpirationDate(expirationDate);
        user.getResetTokens().add(resetToken);

        VerificationCode verificationCode = new VerificationCode("123456", expirationDate, user);
        user.getVerificationCodes().add(verificationCode);

        return user;
    }

    public static User getTestUser2() {
        String username = "Druhy uzivatel";
        String password = "$2a$10$byBqYIgMd5UQ9UJYOMOeseo39Z3pvjLBfvFohH4zTTLFpFFuYEcei"; //FrantaBuFrantaBu
        String email = "druhyadsdsaadsda@uzivatel.cz";
        User user = new User(username, password, email);
        user.setUserId(2);

        String keyValue = "{\"crv\":\"P-521\",\"ext\":true,\"kty\":\"EC\",\"keyOps\":null,\"x\":\"AUmVOV24lL1TYr5Opok7--_uXwJVf8cLwp0cPrUEUWVQTqji6dioEJN-ejrPJ9-XVOAFhZpYwstHGWL4JR6ybmSp\",\"y\":\"ANx2hgmdAtPgAVt3GzETSH2x0yFHTpVM8K6qeGP0GrStWmRAOQ6EiyO3ZJFnTlypG_Qf6OitmhmIi24bilJ__pyO\"}";
        Instant creationDate = Instant.from(LocalDateTime.of(2024, 11, 22, 0, 0).atOffset(ZoneOffset.UTC));
        boolean valid = true;
        user.getPublicKeys().add(new PublicKey(keyValue, creationDate, valid, user));
        return user;
    }

    public static User getTestUser3() {
        String username = "Treti uzivatel";
        String password = "Sifra2";
        String email = "tretiadsdsadsa@uzivatel.cz";
        User user = new User(username, password, email);
        user.setUserId(3);

        String keyValue = "{\"crv\":\"P-521\",\"ext\":true,\"kty\":\"EC\",\"keyOps\":null,\"x\":\"AUmVOV24lL1TYr5Opok7--_uXwJVf8cLwp0cPrUEUWVQTqji6dioEJN-ejrPJ9-XVOAFhZpYwstHGWL4JR6ybmSp\",\"y\":\"ANx2hgmdAtPgAVt3GzETSH2x0yFHTpVM8K6qeGP0GrStWmRAOQ6EiyO3ZJFnTlypG_Qf6OitmhmIi24bilJ__pyO\"}";
        Instant creationDate = Instant.from(LocalDateTime.of(2024, 11, 22, 0, 0).atOffset(ZoneOffset.UTC));
        boolean valid = true;
        user.getPublicKeys().add(new PublicKey(keyValue, creationDate, valid, user));
        return user;
    }

    public static Optional<Conversation> getTestConversation() {
        Conversation conversation = new Conversation();
        String conversationName = "Konverzace";
        Integer conversationId = 1;

        conversation.setConversationName(conversationName);
        conversation.setConversationId(conversationId);
        Optional<Conversation> conversationOptional = Optional.of(conversation);
        return conversationOptional;
    }

    public static List<User> getCompleteTestUsers(){
        List<User> users = new ArrayList<>();
        User user1 = getTestUser();
        User user2 = getTestUser2();
        users.add(user1);
        users.add(user2);

        Conversation conversation = getTestConversation().get();
        List<Message> messages = createTestMessages(user1, conversation);
        conversation.getMessages().addAll(messages);

        //conversationUser zajistuje sve pridani do konverzaci a useru...
        String encryptedSymmetricKey1 = "esI0rgLrX77V8oBul4M1bK6mr+oMlv1c2NZm0qvGvHNMt01Dppbvi3treixtH2s2";
        String iv1 = "{\"0\":215,\"11\":138,\"1\":67,\"2\":206,\"3\":117,\"4\":65,\"5\":18,\"6\":83,\"7\":136,\"8\":11,\"9\":128,\"10\":159}";
        ConversationUser conversationUser = new ConversationUser(user1, conversation, encryptedSymmetricKey1, user1.getActivePublicKey().get().getKeyValue(), iv1);

        String encryptedSymmetricKey2 = "eL/CQBPB2821pyX2g1FyDoqufCoC0qHtKAsLbq3CsvEgM6F4CGzoxQD7WpX2h5/i";
        String iv2 = "{\"0\":88,\"11\":135,\"1\":36,\"2\":241,\"3\":32,\"4\":173,\"5\":245,\"6\":18,\"7\":200,\"8\":107,\"9\":227,\"10\":223}";
        ConversationUser conversationUser2 = new ConversationUser(user2, conversation, encryptedSymmetricKey2, user2.getActivePublicKey().get().getKeyValue(), iv2);
        return users;
    }

    public static List<User> getCompleteTestUsersWithoutIds(){
        List<User> users = new ArrayList<>();
        User user1 = getTestUser();
        user1.setUserId(null);
        user1.getContacts().forEach(user -> user.setUserId(null));
        User user2 = getTestUser2();
        user2.setUserId(null);
        user2.getContacts().forEach(user -> user.setUserId(null));
        users.add(user1);
        users.add(user2);

        Conversation conversation = getTestConversation().get();
        conversation.setConversationId(null);
        List<Message> messages = createTestMessages(user1, conversation);
        messages.forEach(message -> message.setMessageId(null));
        conversation.getMessages().addAll(messages);

        //conversationUser zajistuje sve pridani do konverzaci a useru...
        String encryptedSymmetricKey1 = "esI0rgLrX77V8oBul4M1bK6mr+oMlv1c2NZm0qvGvHNMt01Dppbvi3treixtH2s2";
        String iv1 = "{\"0\":215,\"11\":138,\"1\":67,\"2\":206,\"3\":117,\"4\":65,\"5\":18,\"6\":83,\"7\":136,\"8\":11,\"9\":128,\"10\":159}";
        ConversationUser conversationUser = new ConversationUser(user1, conversation, encryptedSymmetricKey1, user1.getActivePublicKey().get().getKeyValue(), iv1);

        String encryptedSymmetricKey2 = "eL/CQBPB2821pyX2g1FyDoqufCoC0qHtKAsLbq3CsvEgM6F4CGzoxQD7WpX2h5/i";
        String iv2 = "{\"0\":88,\"11\":135,\"1\":36,\"2\":241,\"3\":32,\"4\":173,\"5\":245,\"6\":18,\"7\":200,\"8\":107,\"9\":227,\"10\":223}";
        ConversationUser conversationUser2 = new ConversationUser(user2, conversation, encryptedSymmetricKey2, user2.getActivePublicKey().get().getKeyValue(), iv2);
        return users;
    }

    public static List<Message> createTestMessages(User user, Conversation conversation) {
        Message message1 = getMessage(user, conversation);

        Message message2 = getMessage2(user, conversation);

        Message message3 = getMessage3(user, conversation);

        Message message4 = getMessage4(user, conversation);

        List<Message> messages = new ArrayList<>();
        messages.add(message1);
        messages.add(message2);
        messages.add(message3);
        messages.add(message4);
        return messages;
    }

    public static Message getMessage4(User user, Conversation conversation) {
        Instant validTo = Instant.from(LocalDateTime.of(2024, 11, 16, 0, 0).atOffset(ZoneOffset.UTC));
        String iv4 = "{\"0\":10,\"11\":10,\"1\":104,\"2\":124,\"3\":12,\"4\":177,\"5\":50,\"6\":167,\"7\":1,\"8\":6,\"9\":52,\"10\":56}";
        Message message4 = new Message(user, conversation, "Message4", validTo, iv4);
        message4.setMessageId(4);
        return message4;
    }

    public static Message getMessage3(User user, Conversation conversation) {
        String iv3 = "{\"0\":86,\"11\":176,\"1\":52,\"2\":106,\"3\":181,\"4\":242,\"5\":114,\"6\":99,\"7\":242,\"8\":162,\"9\":208,\"10\":149}";
        Message message3 = new Message(user, conversation, "Message3", null, iv3);
        message3.setMessageId(3);
        return message3;
    }

    public static Message getMessage2(User user, Conversation conversation) {
        String iv2 = "{\"0\":214,\"11\":166,\"1\":235,\"2\":235,\"3\":45,\"4\":236,\"5\":192,\"6\":175,\"7\":74,\"8\":234,\"9\":230,\"10\":32}";
        Message message2 = new Message(user, conversation, "Message2", null, iv2);
        message2.setMessageId(2);
        return message2;
    }

    public static Message getMessage(User user, Conversation conversation) {
        String iv1 = "{\"0\":244,\"11\":227,\"1\":117,\"2\":98,\"3\":164,\"4\":191,\"5\":223,\"6\":44,\"7\":254,\"8\":1,\"9\":83,\"10\":159}";
        Message message1 = new Message(user, conversation, "Message1", null, iv1);
        message1.setMessageId(1);
        return message1;
    }
}
