package com.example.nnprorocnikovyprojekt.dtos.user;

import jakarta.validation.constraints.Min;
import jakarta.validation.constraints.NotNull;
import org.hibernate.validator.constraints.Length;

public class NewPasswordDto {
    @NotNull
    private String token;

    @Length(min = 12)
    @NotNull
    private String password;

    public String getToken() {
        return token;
    }

    public void setToken(String token) {
        this.token = token;
    }

    public String getPassword() {
        return password;
    }

    public void setPassword(String password) {
        this.password = password;
    }
}

