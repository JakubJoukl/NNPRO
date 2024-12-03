package com.example.nnprorocnikovyprojekt.dtos.user;

import jakarta.validation.constraints.Email;
import jakarta.validation.constraints.Min;
import jakarta.validation.constraints.NotNull;
import org.hibernate.validator.constraints.Length;

public class UpdateUserDto {
    @Length(min = 12)
    @NotNull
    private String confirmationPassword;

    @Length(min = 12)
    @NotNull
    private String password;

    @Email(regexp = "^[a-zA-Z0-9.!#$%&'*+/=?^_`{|}~-]+@[a-zA-Z0-9-]+(?:\\.[a-zA-Z0-9-]+)*$")
    @NotNull
    private String email;

    private PublicKeyDto publicKey;

    public String getPassword() {
        return password;
    }

    public void setPassword(String password) {
        this.password = password;
    }

    public String getEmail() {
        return email;
    }

    public void setEmail(String email) {
        this.email = email;
    }

    public PublicKeyDto getPublicKey() {
        return publicKey;
    }

    public void setPublicKey(PublicKeyDto publicKey) {
        this.publicKey = publicKey;
    }

    public String getConfirmationPassword() {
        return confirmationPassword;
    }

    public void setConfirmationPassword(String confirmationPassword) {
        this.confirmationPassword = confirmationPassword;
    }
}
