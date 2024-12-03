package com.example.nnprorocnikovyprojekt.dtos.user;

import jakarta.validation.constraints.Min;
import jakarta.validation.constraints.NotNull;
import jakarta.validation.constraints.Pattern;
import org.hibernate.validator.constraints.Length;

public class LoginDto {

    @NotNull
    @Pattern(regexp = "^[A-Za-z][A-Za-z0-9_ ]{7,29}$")
    private String username;

    @Length(min = 12)
    @NotNull
    private String password;

    @NotNull
    private String captchaToken;

    public String getUsername() {
        return username;
    }

    public void setUsername(String username) {
        this.username = username;
    }

    public String getPassword() {
        return password;
    }

    public void setPassword(String password) {
        this.password = password;
    }

    public String getCaptchaToken() {
        return captchaToken;
    }

    public void setCaptchaToken(String captchaToken) {
        this.captchaToken = captchaToken;
    }
}
