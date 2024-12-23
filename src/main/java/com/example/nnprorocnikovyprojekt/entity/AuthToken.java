package com.example.nnprorocnikovyprojekt.entity;

import jakarta.persistence.*;

@Entity
public class AuthToken {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Integer authTokenId;

    @ManyToOne(fetch = FetchType.LAZY)
    private User user;

    @Column
    private String jwtHash;

    @Column
    private Boolean valid;

    private transient String token;

    public AuthToken() {}

    public AuthToken(User user, String jwtHash, Boolean valid, String token) {
        this.user = user;
        this.jwtHash = jwtHash;
        this.valid = valid;
        this.token = token;
    }

    public Integer getAuthTokenId() {
        return authTokenId;
    }

    public void setAuthTokenId(Integer authTokenId) {
        this.authTokenId = authTokenId;
    }

    public User getUser() {
        return user;
    }

    public void setUser(User user) {
        this.user = user;
    }

    public String getJwtHash() {
        return jwtHash;
    }

    public void setJwtHash(String jwtHash) {
        this.jwtHash = jwtHash;
    }

    public Boolean isValid() {
        return valid;
    }

    public void setValid(Boolean valid) {
        this.valid = valid;
    }

    public String getToken() {
        return token;
    }

    public void setToken(String token) {
        this.token = token;
    }
}
