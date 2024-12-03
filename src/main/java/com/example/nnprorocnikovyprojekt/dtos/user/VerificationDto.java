package com.example.nnprorocnikovyprojekt.dtos.user;

import jakarta.validation.constraints.NotNull;
import jakarta.validation.constraints.Pattern;

public class VerificationDto {
    @NotNull
    @Pattern(regexp = "^[A-Za-z][A-Za-z0-9_ ]{7,29}$")
    private String username;

    @NotNull
    private String verificationCode;

    @NotNull
    private String captchaToken;

    public String getUsername() {
        return username;
    }

    public void setUsername(String username) {
        this.username = username;
    }

    public String getVerificationCode() {
        return verificationCode;
    }

    public void setVerificationCode(String verificationCode) {
        this.verificationCode = verificationCode;
    }

    public String getCaptchaToken() {
        return captchaToken;
    }

    public void setCaptchaToken(String captchaToken) {
        this.captchaToken = captchaToken;
    }
}
