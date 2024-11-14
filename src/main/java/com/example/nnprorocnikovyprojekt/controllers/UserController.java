package com.example.nnprorocnikovyprojekt.controllers;

import com.example.nnprorocnikovyprojekt.dtos.general.GeneralResponseDto;
import com.example.nnprorocnikovyprojekt.dtos.pageinfo.PageInfoRequestWrapper;
import com.example.nnprorocnikovyprojekt.dtos.user.*;
import com.example.nnprorocnikovyprojekt.entity.User;
import com.example.nnprorocnikovyprojekt.services.EmailService;
import com.example.nnprorocnikovyprojekt.services.UserService;
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
        try {
            ExpirationDateDto expirationDateDto = userService.loginUser(authRequest);
            return ResponseEntity.status(HttpStatus.OK).body(expirationDateDto);
        } catch (Exception e) {
            return ResponseEntity.status(HttpStatus.FORBIDDEN).body(new GeneralResponseDto("Invalid username password combination"));
        }
    }

    @PostMapping("/verify2fa")
    public ResponseEntity<?> verify2Fa(@RequestBody VerificationDto verificationDto) {
        try{
            JwtTokenDto jwtTokenDto = userService.verify2FaAndGetJwtToken(verificationDto);
            return ResponseEntity.status(HttpStatus.OK).body(jwtTokenDto);
        } catch (Exception e) {
            return ResponseEntity.status(HttpStatus.FORBIDDEN).body(new GeneralResponseDto("Verification token is not valid"));
        }
    }

    @PostMapping("/register")
    public ResponseEntity<?> register(@RequestBody RegistrationDto registrationRequest){
        try {
            boolean userCreated = userService.registerUser(registrationRequest);
            return ResponseEntity.status(HttpStatus.CREATED).body(new GeneralResponseDto("User created"));
        } catch (Exception e) {
            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR).body(new GeneralResponseDto("Failed to create user"));
        }
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
        try {
            userService.newPassword(resetPasswordRequest);
            return ResponseEntity.status(HttpStatus.OK).body(new GeneralResponseDto("Password reset"));
        } catch (Exception e){
            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR).body(new GeneralResponseDto("Failed to get new password"));
        }
    }

    @PutMapping("/updateUser")
    public ResponseEntity<?> updateUser(@RequestBody UpdateUserDto updateUserDto){
        try {
            UserDto userDto = userService.updateUser(updateUserDto);
            return ResponseEntity.status(HttpStatus.OK).body(userDto);
        } catch (Exception e) {
            return ResponseEntity.status(HttpStatus.BAD_REQUEST).body(new GeneralResponseDto("Failed to update user"));
        }
    }

    @PostMapping("/addContact")
    public ResponseEntity<?> addContact(@RequestBody AddContactDto addContactDto) {
        try {
            userService.addContact(addContactDto);
            return ResponseEntity.status(HttpStatus.OK).body(new GeneralResponseDto("Contact added"));
        } catch (Exception e) {
            return ResponseEntity.status(HttpStatus.BAD_REQUEST).body(new GeneralResponseDto("Failed to add contact"));
        }
    }

    @PostMapping("/listContacts")
    public ResponseEntity<?> listContacts(@RequestBody PageInfoRequestWrapper pageInfoRequestWrapper) {
        try {
            UserPageResponseDto userPageResponseDto = userService.listContacts(pageInfoRequestWrapper);
            return ResponseEntity.status(HttpStatus.OK).body(userPageResponseDto);
        } catch (Exception e) {
            return ResponseEntity.status(HttpStatus.BAD_REQUEST).body(new GeneralResponseDto("Failed to add contact"));
        }
    }

    @GetMapping("/getCurrentUserProfile")
    public ResponseEntity<?> getCurrentUserProfile(){
        try {
            UserDto responseUserDto = userService.getUserData();
            return ResponseEntity.status(HttpStatus.OK).body(responseUserDto);
        } catch (Exception e) {
            return ResponseEntity.status(HttpStatus.BAD_REQUEST).body(new GeneralResponseDto("Failed to retrieve user details"));
        }
    }

    @PostMapping("/searchUsers")
    public ResponseEntity<?> searchUsers(@RequestBody SearchUserDtoRequest searchUserDtoRequest){
        try {
            ContactPageResponseDto responseUserDto = userService.searchUsers(searchUserDtoRequest);
            return ResponseEntity.status(HttpStatus.OK).body(responseUserDto);
        } catch (Exception e) {
            return ResponseEntity.status(HttpStatus.BAD_REQUEST).body(new GeneralResponseDto("Failed to retrieve users"));
        }
    }
}
