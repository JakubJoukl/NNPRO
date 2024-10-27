package com.example.nnprorocnikovyprojekt.entity;

import com.example.nnprorocnikovyprojekt.entity.interfaces.WithExpiration;
import jakarta.persistence.*;

import java.time.LocalDateTime;

@Entity
@Table(name = "RESET_TOKEN")
public class ResetToken implements WithExpiration {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Integer resetTokenId;

    @Column
    private String token;

    @Column
    private LocalDateTime expirationDate;

    @Column
    private boolean valid;

    @ManyToOne
    private User user;

    public ResetToken(User user, String token) {
        this.user = user;
        this.valid = true;
        this.expirationDate = LocalDateTime.now().plusHours(1);
        this.token = token;
    }

    protected ResetToken() {

    }

    public Integer getResetTokenId() {
        return resetTokenId;
    }

    public void setResetTokenId(Integer resetTokenId) {
        this.resetTokenId = resetTokenId;
    }

    public String getToken() {
        return token;
    }

    public void setToken(String token) {
        this.token = token;
    }

    @Override
    public LocalDateTime getExpirationDate() {
        return expirationDate;
    }

    @Override
    public void setExpirationDate(LocalDateTime expirationDate) {
        this.expirationDate = expirationDate;
    }

    public User getUser() {
        return user;
    }

    public void setUser(User user) {
        this.user = user;
    }

    @Override
    public boolean isValid() {
        return valid;
    }

    @Override
    public void setValid(boolean valid) {
        this.valid = valid;
    }
}
