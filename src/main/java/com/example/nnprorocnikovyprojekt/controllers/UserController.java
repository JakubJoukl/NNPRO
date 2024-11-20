package com.example.nnprorocnikovyprojekt.controllers;

import com.example.nnprorocnikovyprojekt.dtos.general.GeneralResponseDto;
import com.example.nnprorocnikovyprojekt.dtos.pageinfo.PageInfoRequestWrapper;
import com.example.nnprorocnikovyprojekt.dtos.user.*;
import com.example.nnprorocnikovyprojekt.entity.User;
import com.example.nnprorocnikovyprojekt.services.EmailService;
import com.example.nnprorocnikovyprojekt.services.UserService;
import com.fasterxml.jackson.core.JsonProcessingException;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

@RestController
@RequestMapping("/")
public class UserController {

    @Autowired
    private UserService userService;

    @Autowired
    private EmailService emailService;

    @PostMapping("/login")
    public ResponseEntity<?> login(@RequestBody LoginDto authRequest) {
        ExpirationDateDto expirationDateDto = userService.loginUser(authRequest);
        return ResponseEntity.status(HttpStatus.OK).body(expirationDateDto);
    }

    @PostMapping("/verify2fa")
    public ResponseEntity<?> verify2Fa(@RequestBody VerificationDto verificationDto) {
        JwtTokenDto jwtTokenDto = userService.verify2FaAndGetJwtToken(verificationDto);
        return ResponseEntity.status(HttpStatus.OK).body(jwtTokenDto);
    }

    @PostMapping("/register")
    public ResponseEntity<?> register(@RequestBody RegistrationDto registrationRequest){
        boolean userCreated = userService.registerUser(registrationRequest);
        return ResponseEntity.status(HttpStatus.CREATED).body(new GeneralResponseDto("User created"));
    }

    //TODO bude potreba
    @PostMapping("/resetPassword")
    public ResponseEntity<?> resetPassword(@RequestBody ResetPasswordDto authRequest) {
        User user = userService.getUserByUsername(authRequest.getUsername());
        if(user == null) return ResponseEntity.status(HttpStatus.NOT_FOUND).body(new GeneralResponseDto("User not found"));
        emailService.sendResetTokenEmail(user);
        //if(emailSent) {
        return ResponseEntity.status(HttpStatus.OK).body(new GeneralResponseDto("Reset email send"));
        //} else {
        //    return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR).body(new GeneralResponseDto("Failed to send email"));
        //}
    }

    //TODO bude potreba?
    @PostMapping("/newPassword")
    public ResponseEntity<?> newPassword(@RequestBody NewPasswordDto resetPasswordRequest){
        userService.newPassword(resetPasswordRequest);
        return ResponseEntity.status(HttpStatus.OK).body(new GeneralResponseDto("Password reset"));
    }

    @PutMapping("/updateUser")
    public ResponseEntity<?> updateUser(@RequestBody UpdateUserDto updateUserDto) throws JsonProcessingException {
        UserDto userDto = userService.updateUser(updateUserDto);
        return ResponseEntity.status(HttpStatus.OK).body(userDto);
    }

    @PostMapping("/addContact")
    public ResponseEntity<?> addContact(@RequestBody AddRemoveContactDto addRemoveContactDto) {
        userService.addContact(addRemoveContactDto);
        return ResponseEntity.status(HttpStatus.OK).body(new GeneralResponseDto("Contact added"));
    }

    @PostMapping("/removeContact")
    public ResponseEntity<?> removeContact(@RequestBody AddRemoveContactDto addRemoveContactDto) {
        userService.removeContact(addRemoveContactDto);
        return ResponseEntity.status(HttpStatus.OK).body(new GeneralResponseDto("Contact removed"));
    }

    @PostMapping("/listContacts")
    public ResponseEntity<?> listContacts(@RequestBody SearchUserDtoRequest searchUserDtoRequest) {
        UserPageResponseDto userPageResponseDto = userService.listContacts(searchUserDtoRequest);
        return ResponseEntity.status(HttpStatus.OK).body(userPageResponseDto);
    }

    @GetMapping("/getCurrentUserProfile")
    public ResponseEntity<?> getCurrentUserProfile(){
        UserDto responseUserDto = userService.getUserData();
        return ResponseEntity.status(HttpStatus.OK).body(responseUserDto);
    }

    @PostMapping("/searchUsers")
    public ResponseEntity<?> searchUsers(@RequestBody SearchUserDtoRequest searchUserDtoRequest){
        ContactPageResponseDto responseUserDto = userService.searchUsers(searchUserDtoRequest);
        return ResponseEntity.status(HttpStatus.OK).body(responseUserDto);
    }
}
