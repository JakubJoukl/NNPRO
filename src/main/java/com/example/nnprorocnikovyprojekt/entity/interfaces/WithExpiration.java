package com.example.nnprorocnikovyprojekt.entity.interfaces;

import java.time.Instant;

public interface WithExpiration {
    Instant getExpirationDate();

    void setExpirationDate(Instant expirationDate);

    boolean isValid();

    void setValid(boolean valid);
}
