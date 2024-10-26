package com.example.nnprorocnikovyprojekt.controllers;

import com.example.nnprorocnikovyprojekt.dtos.user.*;
import com.example.nnprorocnikovyprojekt.entity.ResetToken;
import com.example.nnprorocnikovyprojekt.entity.User;
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
@RequestMapping("/user")
public class UserController {

    @Autowired
    private UserService userService;

    @Autowired
    private JwtService jwtService;

    @Autowired
    private AuthenticationManager authenticationManager;

    @Autowired
    private EmailService emailService;


    @PostMapping("/login")
    public ResponseEntity<String> authenticateAndGetToken(@RequestBody LoginDto authRequest) {
        Authentication authentication = authenticationManager.authenticate(
                new UsernamePasswordAuthenticationToken(authRequest.getUsername(), authRequest.getPassword())
        );
        if (authentication.isAuthenticated()) {
            return ResponseEntity.status(200).body(jwtService.generateToken(authRequest.getUsername()));
        } else {
            return ResponseEntity.status(403).body("User not created");
        }
    }

    @PostMapping("/register")
    public ResponseEntity<String> register(@RequestBody RegistrationDto registrationRequest){
        boolean userCreated = userService.registerUser(registrationRequest);
        if(!userCreated) return ResponseEntity.status(500).body("Failed to create user");
        else return ResponseEntity.status(201).body("User created");
    }

    @PostMapping("/resetPassword")
    public ResponseEntity<String> resetPassword(@RequestBody ResetPasswordDto authRequest) {
        User user = userService.getUserByUsername(authRequest.getUsername());
        if(user == null) return ResponseEntity.status(404).body("User not found");
        emailService.sendResetTokenEmail(user);
        return ResponseEntity.status(200).body("Reset email send");
    }

    @PostMapping("/newPassword")
    public ResponseEntity<String> newPassword(@RequestBody NewPasswordDto resetPasswordRequest){
        ResetToken resetToken = userService.getResetTokenByValue(resetPasswordRequest.getToken());

        if(resetToken == null) return ResponseEntity.status(400).body("Reset token not found");

        boolean resetTokenIsValid = resetToken.isValid() && LocalDateTime.now().isBefore(resetToken.getExpirationDate());
        if(resetTokenIsValid){
            User user = resetToken.getUser();
            userService.changePassword(resetPasswordRequest.getPassword(), user);
            userService.saveUser(user);
        }
        resetToken.setValid(false);
        userService.saveResetToken(resetToken);

        if(resetTokenIsValid) return ResponseEntity.status(200).body("Password reset");
        else return ResponseEntity.status(400).body("Reset token was already used or expired");
    }

    @PutMapping("/changePassword")
    public ResponseEntity<String> changePassword(@RequestBody ChangePasswordDto changePasswordDto){
        User user = userService.getUserFromContext();

        if(user == null) return ResponseEntity.status(404).body("User not found");
        String oldPassword = changePasswordDto.getOldPassword();
        if(!userService.userPasswordMatches(oldPassword, user)) return ResponseEntity.status(400).body("Old password was wrong");

        String newPassword = changePasswordDto.getNewPassword();
        userService.changePassword(newPassword, user);

        return ResponseEntity.status(200).body("Password changed");
    }
}
