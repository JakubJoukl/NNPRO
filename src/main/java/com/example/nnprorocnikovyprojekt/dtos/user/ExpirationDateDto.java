package com.example.nnprorocnikovyprojekt.dtos.user;

import java.time.LocalDateTime;

public class ExpirationDateDto {
    private LocalDateTime expirationDate;

    public LocalDateTime getExpirationDate() {
        return expirationDate;
    }

    public void setExpirationDate(LocalDateTime expirationDate) {
        this.expirationDate = expirationDate;
    }
}
