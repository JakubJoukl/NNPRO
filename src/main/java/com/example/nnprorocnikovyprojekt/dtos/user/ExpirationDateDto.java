package com.example.nnprorocnikovyprojekt.dtos.user;

import java.time.Instant;

public class ExpirationDateDto {
    private Instant expirationDate;

    public Instant getExpirationDate() {
        return expirationDate;
    }

    public void setExpirationDate(Instant expirationDate) {
        this.expirationDate = expirationDate;
    }
}
