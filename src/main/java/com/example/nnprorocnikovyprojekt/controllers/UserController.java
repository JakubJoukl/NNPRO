package com.example.nnprorocnikovyprojekt.controllers;

import com.example.nnprorocnikovyprojekt.dtos.conversation.UsersDto;
import com.example.nnprorocnikovyprojekt.dtos.general.GeneralResponseDto;
import com.example.nnprorocnikovyprojekt.dtos.pageinfo.PageInfoRequestWrapper;
import com.example.nnprorocnikovyprojekt.dtos.user.*;
import com.example.nnprorocnikovyprojekt.entity.User;
import com.example.nnprorocnikovyprojekt.services.EmailService;
import com.example.nnprorocnikovyprojekt.services.UserService;
import com.fasterxml.jackson.core.JsonProcessingException;
import jakarta.validation.Valid;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.validation.annotation.Validated;
import org.springframework.web.bind.annotation.*;

@RestController
@RequestMapping("/")
public class UserController {

    @Autowired
    private UserService userService;

    @Autowired
    private EmailService emailService;

    @PostMapping("/login")
    public ResponseEntity<?> login(@Valid @RequestBody LoginDto authRequest) {
        ExpirationDateDto expirationDateDto = userService.loginUser(authRequest);
        return ResponseEntity.status(HttpStatus.OK).body(expirationDateDto);
    }

    @PostMapping("/verify2fa")
    public ResponseEntity<?> verify2Fa(@Valid @RequestBody VerificationDto verificationDto) {
        JwtTokenDto jwtTokenDto = userService.verify2FaAndGetJwtToken(verificationDto);
        return ResponseEntity.status(HttpStatus.OK).body(jwtTokenDto);
    }

    @PostMapping("/register")
    public ResponseEntity<?> register(@Valid @RequestBody RegistrationDto registrationRequest){
        boolean userCreated = userService.registerUser(registrationRequest);
        return ResponseEntity.status(HttpStatus.CREATED).body(new GeneralResponseDto("User created"));
    }

    @PostMapping("/banUser")
    public ResponseEntity<?> banUser(@Valid @RequestBody UsernameDto usernameDto) {
        userService.banUser(usernameDto);
        return ResponseEntity.status(HttpStatus.OK).body(new GeneralResponseDto("User banned"));
    }

    @PostMapping("/unbanUser")
    public ResponseEntity<?> unbanUser(@Valid @RequestBody UsernameDto usernameDto) {
        userService.unbanUser(usernameDto);
        return ResponseEntity.status(HttpStatus.OK).body(new GeneralResponseDto("User unbanned"));
    }

    @PostMapping("/addAdmin")
    public ResponseEntity<?> addAdmin(@Valid @RequestBody UsernameDto usernameDto) {
        userService.addAdminToUser(usernameDto);
        return ResponseEntity.status(HttpStatus.OK).body(new GeneralResponseDto("Added ADMIN role to user"));
    }

    @PostMapping("/removeAdmin")
    public ResponseEntity<?> removeAdmin(@Valid @RequestBody UsernameDto usernameDto) {
        userService.removeAdminFromUser(usernameDto);
        return ResponseEntity.status(HttpStatus.OK).body(new GeneralResponseDto("Revoked ADMIN role from user"));
    }

    @PostMapping("/listBannedUsers")
    public ResponseEntity<?> listBannedUsers(@Valid @RequestBody SearchUserDtoRequest searchUserDtoRequest) {
        ListBannedUnbannedUsersDto userPageResponseDto = userService.listBannedUsers(searchUserDtoRequest);
        return ResponseEntity.status(HttpStatus.OK).body(userPageResponseDto);
    }

    @PostMapping("/listNotBannedUsers")
    public ResponseEntity<?> listNotBannedUsers(@Valid @RequestBody SearchUserDtoRequest searchUserDtoRequest) {
        ListBannedUnbannedUsersDto userPageResponseDto = userService.listNotBannedUsers(searchUserDtoRequest);
        return ResponseEntity.status(HttpStatus.OK).body(userPageResponseDto);
    }

    @PostMapping("/resetPassword")
    public ResponseEntity<?> resetPassword(@Valid @RequestBody ResetPasswordDto authRequest) {
        userService.sendResetPassword(authRequest);
        return ResponseEntity.status(HttpStatus.OK).body(new GeneralResponseDto("Reset email send"));
    }

    @PostMapping("/newPassword")
    public ResponseEntity<?> newPassword(@Valid @RequestBody NewPasswordDto newPasswordDto){
        userService.newPassword(newPasswordDto);
        return ResponseEntity.status(HttpStatus.OK).body(new GeneralResponseDto("Password reset"));
    }

    @PutMapping("/updateUser")
    public ResponseEntity<?> updateUser(@Valid @RequestBody UpdateUserDto updateUserDto) throws JsonProcessingException {
        UserDto userDto = userService.updateUser(updateUserDto);
        return ResponseEntity.status(HttpStatus.OK).body(userDto);
    }

    @PostMapping("/addContact")
    public ResponseEntity<?> addContact(@Valid @RequestBody AddRemoveContactDto addRemoveContactDto) {
        userService.addContact(addRemoveContactDto);
        return ResponseEntity.status(HttpStatus.OK).body(new GeneralResponseDto("Contact added"));
    }

    @DeleteMapping("/removeContact")
    public ResponseEntity<?> removeContact(@Valid @RequestBody AddRemoveContactDto addRemoveContactDto) {
        userService.removeContact(addRemoveContactDto);
        return ResponseEntity.status(HttpStatus.OK).body(new GeneralResponseDto("Contact removed"));
    }

    @PostMapping("/listContacts")
    public ResponseEntity<?> listContacts(@Valid @RequestBody SearchUserDtoRequest searchUserDtoRequest) {
        UserPageResponseDto userPageResponseDto = userService.listContacts(searchUserDtoRequest);
        return ResponseEntity.status(HttpStatus.OK).body(userPageResponseDto);
    }

    @GetMapping("/getCurrentUserProfile")
    public ResponseEntity<?> getCurrentUserProfile(){
        UserDto responseUserDto = userService.getUserData();
        return ResponseEntity.status(HttpStatus.OK).body(responseUserDto);
    }

    @PostMapping("/searchUsers")
    public ResponseEntity<?> searchUsers(@Valid @RequestBody SearchUserDtoRequest searchUserDtoRequest){
        ContactPageResponseDto responseUserDto = userService.searchUsers(searchUserDtoRequest);
        return ResponseEntity.status(HttpStatus.OK).body(responseUserDto);
    }
}
