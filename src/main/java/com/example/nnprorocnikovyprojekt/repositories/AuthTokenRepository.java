package com.example.nnprorocnikovyprojekt.repositories;

import com.example.nnprorocnikovyprojekt.entity.AuthToken;
import com.example.nnprorocnikovyprojekt.entity.User;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.Optional;

public interface AuthTokenRepository extends JpaRepository<AuthToken, Integer> {
    public Optional<AuthToken> getAuthTokenByUserAndJwtHash(User user, String jwtHash);
}
