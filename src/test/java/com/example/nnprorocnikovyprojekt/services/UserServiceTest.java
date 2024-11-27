package com.example.nnprorocnikovyprojekt.services;

import com.example.nnprorocnikovyprojekt.config.CommonTestParent;
import com.example.nnprorocnikovyprojekt.config.WithCustomUser;
import com.example.nnprorocnikovyprojekt.dtos.pageinfo.PageInfoDtoRequest;
import com.example.nnprorocnikovyprojekt.dtos.user.*;
import com.example.nnprorocnikovyprojekt.entity.*;
import com.example.nnprorocnikovyprojekt.external.CaptchaService;
import com.example.nnprorocnikovyprojekt.repositories.*;
import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.databind.ObjectMapper;
import org.junit.jupiter.api.Test;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageImpl;
import org.springframework.messaging.simp.SimpMessagingTemplate;
import org.springframework.security.core.context.SecurityContext;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.security.crypto.password.PasswordEncoder;

import java.util.ArrayList;
import java.util.List;
import java.util.Optional;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.Mockito.*;

@WithCustomUser(username = "Franta BU", roles = {"USER"})
class UserServiceTest extends CommonTestParent {

    @InjectMocks
    private UserService userService;

    @Mock
    private UserRepository userRepository;

    @Mock
    private ConversationService conversationService;

    @Mock
    private ConversationRepository conversationRepository;

    @Mock
    private MessageService messageService;

    @Mock
    private ConversationUserRepository conversationUserRepository;

    @Mock
    private MessageRepository messageRepository;

    @Mock
    private ObjectMapper objectMapper;

    @Mock
    private VerificationCodeRepository verificationCodeRepository;

    @Mock
    private ResetTokenRepository resetTokenRepository;

    @Mock
    private PasswordEncoder passwordEncoder;

    @Mock
    private SimpMessagingTemplate simpMessagingTemplate;

    @Mock
    private CaptchaService captchaService;

    @Test
    void loadUserByUsername() {
        User user = getTestUser();
        when(userRepository.getUserByUsername(user.getUsername())).thenReturn(Optional.of(user));
        User returnedUser = userService.getUserByUsername(user.getUsername());
        assertEquals(user, returnedUser);
    }

    @Test
    void getUserByUsername() {
        User user = getTestUser();
        when(userRepository.getUserByUsername(user.getUsername())).thenReturn(Optional.of(user));
        User returnedUser = userService.getUserByUsername(user.getUsername());
        assertEquals(user, returnedUser);
    }

    @Test
    void getResetTokenByValue() {
        User user = getTestUser();
        ResetToken resetToken = user.getResetTokens().get(0);
        when(resetTokenRepository.getResetTokenByToken(resetToken.getToken())).thenReturn(Optional.of(resetToken));
        ResetToken returnedToken = userService.getResetTokenByValue(resetToken.getToken());
        assertEquals(resetToken, returnedToken);
    }

    @Test
    void newPassword() {
        NewPasswordDto newPasswordDto = new NewPasswordDto();
        User user = getTestUser();
        ResetToken resetToken = user.getResetTokens().get(0);
        String newEncyptedPassword = "Cute little kitten";

        newPasswordDto.setToken(resetToken.getToken());
        newPasswordDto.setPassword(user.getPassword());
        when(resetTokenRepository.getResetTokenByToken(resetToken.getToken())).thenReturn(Optional.of(resetToken));
        when(passwordEncoder.encode(user.getPassword())).thenReturn(newEncyptedPassword);
        userService.newPassword(newPasswordDto);
        assertEquals(newEncyptedPassword, user.getPassword());
    }

    @Test
    void deactivateUserResetTokens() {
        User user = getTestUser();
        userService.deactivateUserResetTokens(user);
        assertFalse(user.getResetTokens().get(0).isValid());
        assertEquals(0, user.getResetTokens().stream().filter(ResetToken::isValid).count());
    }

    @Test
    void deactivateUserVerificationTokens() {
        User user = getTestUser();
        userService.deactivateUserVerificationTokens(user);
        assertFalse(user.getVerificationCodes().get(0).isValid());
        assertEquals(0, user.getVerificationCodes().stream().filter(VerificationCode::isValid).count());
    }

    @Test
    void registerUser() {
        RegistrationDto registrationDto = new RegistrationDto();
        String username = "new";
        String password = "user";
        String email = "email@email.cz";
        String captchaToken = "token";
        registrationDto.setUsername(username);
        registrationDto.setPassword(password);
        registrationDto.setEmail(email);
        registrationDto.setCaptchaToken(captchaToken);
        when(captchaService.validateCaptcha(registrationDto.getCaptchaToken())).thenReturn(true);
        when(userRepository.getUserByUsername(username)).thenReturn(Optional.empty());
        when(userRepository.save(any(User.class))).thenReturn(new User(username, password, email));
        userService.registerUser(registrationDto);
        verify(userRepository).save(any(User.class));
    }

    @Test
    void changePassword() {
        User user = getTestUser();
        String newPassword = "pawns";
        when(passwordEncoder.encode(newPassword)).thenReturn("mittens");
        userService.changePassword(newPassword, user);
        assertEquals("mittens", user.getPassword());
    }

    @Test
    void userPasswordMatches() {
        User user = getTestUser();
        String encodedPassword = "shadow";
        when(passwordEncoder.matches(encodedPassword, user.getPassword())).thenReturn(true);
        boolean passwordMatches = userService.userPasswordMatches(encodedPassword, user);
        assertTrue(passwordMatches);
    }

    @Test
    void generateVerificationCodeForUser() {
        User user = getTestUser();
        VerificationCode verificationCode = userService.generateVerificationCodeForUser(user);
        assertTrue(user.getVerificationCodes().contains(verificationCode));
    }

    @Test
    void generateResetTokenForUser() {
        User user = getTestUser();
        ResetToken resetToken = userService.generateResetTokenForUser(user);
        assertTrue(user.getResetTokens().contains(resetToken));
    }

    @Test
    void verifyVerificationCode() {
        User user = getTestUser();
        when(userRepository.getUserByUsername(user.getUsername())).thenReturn(Optional.of(user));
        String verificationCode = user.getVerificationCodes().get(0).getVerificationCode();
        List<VerificationCode> verificationCodes = new ArrayList<>();
        verificationCodes.add(user.getVerificationCodes().get(0));
        when(verificationCodeRepository.findValidVerificationCodes(user, verificationCode)).thenReturn(verificationCodes);
        boolean verified = userService.verifyVerificationCode(user.getUsername(), verificationCode);
        assertTrue(verified);
        assertFalse(user.getVerificationCodes().get(0).isValid());
    }

    @Test
    void updateUser() throws JsonProcessingException {
        User user = getTestUser();
        String confirmationPassword = user.getPassword();
        String email = "newEmail";
        String password = "newPassword";
        PublicKeyDto publicKeyDto = new PublicKeyDto();
        publicKeyDto.setCrv("crv");
        publicKeyDto.setExt(true);
        publicKeyDto.setKty("kty");
        publicKeyDto.setKeyOps(null);
        publicKeyDto.setX("x");
        publicKeyDto.setY("y");

        UpdateUserDto updateUserDto = new UpdateUserDto();
        updateUserDto.setEmail(email);
        updateUserDto.setPassword(password);
        updateUserDto.setConfirmationPassword(confirmationPassword);
        updateUserDto.setPublicKey(publicKeyDto);
        List<PublicKey> previousUserPublicKeys = new ArrayList<>();
        previousUserPublicKeys.addAll(user.getPublicKeys());
        Integer previousSize = previousUserPublicKeys.size();

        String newEncryptedPassword = "hawk";
        when(passwordEncoder.matches(user.getPassword(), confirmationPassword)).thenReturn(true);
        when(passwordEncoder.encode(password)).thenReturn(newEncryptedPassword);
        when(userRepository.getUserByUsername(user.getUsername())).thenReturn(Optional.of(user));
        when(objectMapper.writeValueAsString(any())).thenReturn("kitty");
        when(userRepository.save(user)).thenReturn(user);

        try {
            userService.updateUser(updateUserDto);
        } catch (JsonProcessingException e) {
            fail(e);
        }
        assertEquals(newEncryptedPassword, user.getPassword());
        assertEquals(email, user.getEmail());
        assertEquals(previousSize + 1, user.getPublicKeys().size());
        assertTrue(previousUserPublicKeys.stream().noneMatch(PublicKey::isValid));
        assertTrue(user.getActivePublicKey().isPresent());
    }

    @Test
    void addContact() {
        AddRemoveContactDto addRemoveContactDto = new AddRemoveContactDto();
        User user = getTestUser();
        User user3 = getTestUser3();
        addRemoveContactDto.setUsername(user3.getUsername());
        when(userRepository.getUserByUsername(user.getUsername())).thenReturn(Optional.of(user));
        when(userRepository.getUserByUsername(user3.getUsername())).thenReturn(Optional.of(user3));
        userService.addContact(addRemoveContactDto);
        assertTrue(user.getContacts().contains(user3));
    }

    @Test
    void listContacts() {
        User user = getTestUser();
        User user2 = getTestUser2();

        SearchUserDtoRequest searchUserDtoRequest = new SearchUserDtoRequest();
        PageInfoDtoRequest pageInfoDtoRequest = new PageInfoDtoRequest(50, 0);
        searchUserDtoRequest.setUsername(user2.getUsername());
        searchUserDtoRequest.setPageInfo(pageInfoDtoRequest);

        when(userRepository.getUserByUsername(user.getUsername())).thenReturn(Optional.of(user));

        UserPageResponseDto userPageResponseDto = userService.listContacts(searchUserDtoRequest);
        assertEquals(1, userPageResponseDto.getItemList().size());
        assertEquals(userPageResponseDto.getItemList().get(0).getUsername(), user2.getUsername());
        assertEquals(userPageResponseDto.getItemList().get(0).getEmail(), user2.getEmail());
        assertEquals(userPageResponseDto.getItemList().get(0).getEmail(), user2.getEmail());
    }

    @Test
    void searchUsers() {
        User user = getTestUser();
        User user2 = getTestUser2();

        ArrayList<User> contacts = new ArrayList<>();
        contacts.add(user2);
        Page<User> pagedResponse = new PageImpl(contacts);

        SearchUserDtoRequest searchUserDtoRequest = new SearchUserDtoRequest();
        searchUserDtoRequest.setUsername(user2.getUsername());
        PageInfoDtoRequest pageInfoDtoRequest = new PageInfoDtoRequest(50, 0);
        searchUserDtoRequest.setPageInfo(pageInfoDtoRequest);

        when(userRepository.findUsersByUsernameStartingWithAndUsernameNot(eq(user2.getUsername()), eq(user.getUsername()), any())).thenReturn(pagedResponse);
        when(userRepository.getUserByUsername(user.getUsername())).thenReturn(Optional.of(user));
        UserPageResponseDto userPageResponseDto = userService.listContacts(searchUserDtoRequest);
        ContactPageResponseDto contactPageResponseDto = userService.searchUsers(searchUserDtoRequest);
        assertEquals(1, contactPageResponseDto.getItemList().size());
        assertEquals(userPageResponseDto.getItemList().get(0).getUsername(), user2.getUsername());
        assertEquals(userPageResponseDto.getItemList().get(0).getEmail(), user2.getEmail());
        assertEquals(userPageResponseDto.getItemList().get(0).getEmail(), user2.getEmail());
    }

    @Test
    void verify2FaAndGetJwtToken() {
    }

    @Test
    void loginUser() {
    }

    @Test
    void removeContact() {
    }
}