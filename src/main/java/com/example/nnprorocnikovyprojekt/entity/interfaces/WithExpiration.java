package com.example.nnprorocnikovyprojekt.entity.interfaces;

import java.time.LocalDateTime;

public interface WithExpiration {
    LocalDateTime getExpirationDate();

    void setExpirationDate(LocalDateTime expirationDate);

    boolean isValid();

    void setValid(boolean valid);
}
