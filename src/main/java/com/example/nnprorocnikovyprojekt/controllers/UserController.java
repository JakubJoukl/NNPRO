package com.example.nnprorocnikovyprojekt.controllers;

import com.example.nnprorocnikovyprojekt.dtos.general.GeneralResponseDto;
import com.example.nnprorocnikovyprojekt.dtos.pageinfo.PageInfoRequestWrapper;
import com.example.nnprorocnikovyprojekt.dtos.user.*;
import com.example.nnprorocnikovyprojekt.entity.ResetToken;
import com.example.nnprorocnikovyprojekt.entity.User;
import com.example.nnprorocnikovyprojekt.entity.VerificationCode;
import com.example.nnprorocnikovyprojekt.external.CaptchaService;
import com.example.nnprorocnikovyprojekt.security.JwtService;
import com.example.nnprorocnikovyprojekt.services.EmailService;
import com.example.nnprorocnikovyprojekt.services.UserService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.ResponseEntity;
import org.springframework.security.authentication.AuthenticationManager;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.core.Authentication;
import org.springframework.web.bind.annotation.*;

import java.time.LocalDateTime;

@RestController
@RequestMapping("/")
public class UserController {

    @Autowired
    private UserService userService;

    @Autowired
    private JwtService jwtService;

    @Autowired
    private AuthenticationManager authenticationManager;

    @Autowired
    private EmailService emailService;

    @Autowired
    private CaptchaService captchaService;

    @PostMapping("/login")
    public ResponseEntity<?> login(@RequestBody LoginDto authRequest) {
        boolean captchaIsValid = captchaService.validateCaptcha(authRequest.getCaptchaToken());
        if(!captchaIsValid) {
            return ResponseEntity.status(403).body(new GeneralResponseDto("Captcha is not valid"));
        }

        Authentication authentication = authenticationManager.authenticate(
                new UsernamePasswordAuthenticationToken(authRequest.getUsername(), authRequest.getPassword())
        );

        if (authentication.isAuthenticated()) {
            User user = userService.getUserByUsername(authRequest.getUsername());
            VerificationCode verificationCode = emailService.sendVerificationCodeEmail(user);
            if(verificationCode != null) {
                ExpirationDateDto expirationDateDto = new ExpirationDateDto();
                expirationDateDto.setExpirationDate(verificationCode.getExpirationDate());
                return ResponseEntity.status(200).body(expirationDateDto);
            } else {
                return ResponseEntity.status(500).body(new GeneralResponseDto("Failed to send verification code"));
            }
            //return ResponseEntity.status(200).body(jwtService.generateToken(authRequest.getUsername()));
        } else {
            return ResponseEntity.status(403).body(new GeneralResponseDto("Invalid username password combination"));
        }
    }

    @PostMapping("/verify2fa")
    public ResponseEntity<?> verify2Fa(@RequestBody VerificationDto verificationDto) {
        boolean captchaIsValid = captchaService.validateCaptcha(verificationDto.getCaptchaToken());
        if(!captchaIsValid) {
            return ResponseEntity.status(403).body(new GeneralResponseDto("Captcha is not valid"));
        }

        boolean verificationCodeMatches = userService.verifyVerificationCode(verificationDto.getUsername(), verificationDto.getVerificationCode());

        if(verificationCodeMatches){
            String jwtToken = jwtService.generateToken(verificationDto.getUsername());
            JwtTokenDto jwtTokenDto = new JwtTokenDto();
            jwtTokenDto.setJwtToken(jwtToken);
            return ResponseEntity.status(200).body(jwtTokenDto);
        } else {
            return ResponseEntity.status(403).body(new GeneralResponseDto("Verification token is not valid"));
        }
    }

    @PostMapping("/register")
    public ResponseEntity<?> register(@RequestBody RegistrationDto registrationRequest){
        boolean captchaIsValid = captchaService.validateCaptcha(registrationRequest.getCaptchaToken());
        if(!captchaIsValid) {
            return ResponseEntity.status(403).body(new GeneralResponseDto("Captcha is not valid"));
        }

        boolean userCreated = userService.registerUser(registrationRequest);
        if(!userCreated) return ResponseEntity.status(500).body(new GeneralResponseDto("Failed to create user"));
        else return ResponseEntity.status(201).body(new GeneralResponseDto("User created"));
    }

    //TODO bude potreba
    @PostMapping("/resetPassword")
    public ResponseEntity<?> resetPassword(@RequestBody ResetPasswordDto authRequest) {
        User user = userService.getUserByUsername(authRequest.getUsername());
        if(user == null) return ResponseEntity.status(404).body(new GeneralResponseDto("User not found"));
        emailService.sendResetTokenEmail(user);
        //if(emailSent) {
        return ResponseEntity.status(200).body(new GeneralResponseDto("Reset email send"));
        //} else {
        //    return ResponseEntity.status(500).body(new GeneralResponseDto("Failed to send email"));
        //}
    }

    //TODO bude potreba?
    @PostMapping("/newPassword")
    public ResponseEntity<?> newPassword(@RequestBody NewPasswordDto resetPasswordRequest){
        ResetToken resetToken = userService.getResetTokenByValue(resetPasswordRequest.getToken());

        if(resetToken == null) return ResponseEntity.status(400).body(new GeneralResponseDto("Reset token not found"));

        boolean resetTokenIsValid = resetToken.isValid() && LocalDateTime.now().isBefore(resetToken.getExpirationDate());
        if(resetTokenIsValid){
            User user = resetToken.getUser();
            userService.changePassword(resetPasswordRequest.getPassword(), user);
            userService.saveUser(user);
        }
        resetToken.setValid(false);
        userService.saveResetToken(resetToken);

        if(resetTokenIsValid) return ResponseEntity.status(200).body(new GeneralResponseDto("Password reset"));
        else return ResponseEntity.status(400).body(new GeneralResponseDto("Reset token was already used or expired"));
    }

    @PutMapping("/updateUser")
    public ResponseEntity<?> updateUser(@RequestBody UpdateUserDto updateUserDto){
        try {
            userService.updateUser(updateUserDto);
            return ResponseEntity.status(200).body(new GeneralResponseDto("User updated"));
        } catch (Exception e) {
            return ResponseEntity.status(400).body(new GeneralResponseDto("Failed to update user"));
        }
    }

    @PostMapping("/addContact")
    public ResponseEntity<?> addContact(@RequestBody AddContactDto addContactDto) {
        try {
            userService.addContact(addContactDto);
            return ResponseEntity.status(200).body(new GeneralResponseDto("Contact added"));
        } catch (Exception e) {
            return ResponseEntity.status(400).body(new GeneralResponseDto("Failed to add contact"));
        }
    }

    @PostMapping("/listContacts")
    public ResponseEntity<?> listContacts(@RequestBody PageInfoRequestWrapper pageInfoRequestWrapper) {
        try {
            ContactsPageResponseDto contactsPageResponseDto = userService.listContacts(pageInfoRequestWrapper);
            return ResponseEntity.status(200).body(contactsPageResponseDto);
        } catch (Exception e) {
            return ResponseEntity.status(400).body(new GeneralResponseDto("Failed to add contact"));
        }
    }
}
