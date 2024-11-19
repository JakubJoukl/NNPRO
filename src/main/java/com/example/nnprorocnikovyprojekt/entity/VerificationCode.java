package com.example.nnprorocnikovyprojekt.entity;

import com.example.nnprorocnikovyprojekt.entity.interfaces.WithExpiration;
import jakarta.persistence.*;
import jakarta.validation.constraints.NotNull;

import java.time.Instant;

@Entity
@Table(name = "VERIFICATION_CODE")
public class VerificationCode implements WithExpiration {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Integer verificationCodeId;

    @Column
    @NotNull
    private String verificationCode;

    @Column
    @NotNull
    private Instant expirationDate;

    @Column
    @NotNull
    private boolean valid;

    @ManyToOne
    @JoinColumn(name="user_id", nullable=false)
    private User user;

    protected VerificationCode(){

    }

    public VerificationCode(String verificationCode, Instant expirationDate, User user){
        this.verificationCode = verificationCode;
        this.valid = true;
        this.expirationDate = expirationDate;
        this.user = user;
    }

    @Override
    public Instant getExpirationDate() {
        return expirationDate;
    }

    @Override
    public void setExpirationDate(Instant expirationDate) {
        this.expirationDate = expirationDate;
    }

    @Override
    public boolean isValid() {
        return valid;
    }

    @Override
    public void setValid(boolean valid) {
        this.valid = valid;
    }

    public Integer getVerificationCodeId() {
        return verificationCodeId;
    }

    public void setVerificationCodeId(Integer verificationCodeId) {
        this.verificationCodeId = verificationCodeId;
    }

    public String getVerificationCode() {
        return verificationCode;
    }

    public void setVerificationCode(String verificationCode) {
        this.verificationCode = verificationCode;
    }

    public User getUser() {
        return user;
    }

    public void setUser(User user) {
        this.user = user;
    }
}
