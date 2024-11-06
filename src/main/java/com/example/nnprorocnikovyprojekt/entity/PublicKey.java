package com.example.nnprorocnikovyprojekt.entity;

import jakarta.persistence.*;
import jakarta.validation.constraints.NotNull;

import java.time.LocalDateTime;

@Entity
@Table(name = "PUBLIC_KEY")
public class PublicKey {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Integer publicKeyId;

    @Column
    @NotNull
    private String key;

    @Column
    @NotNull
    private LocalDateTime creationDate;

    @Column
    @NotNull
    private boolean valid;

    @ManyToOne
    @JoinColumn(name = "owner_id", nullable = false)
    private User owner;

    public PublicKey() {
    }

    public PublicKey(String key, LocalDateTime creationDate, boolean valid, User owner) {
        this.key = key;
        this.creationDate = creationDate;
        this.valid = valid;
        this.owner = owner;
    }

    public Integer getPublicKeyId() {
        return publicKeyId;
    }

    public void setPublicKeyId(Integer publicKeyId) {
        this.publicKeyId = publicKeyId;
    }

    public String getKey() {
        return key;
    }

    public void setKey(String key) {
        this.key = key;
    }

    public LocalDateTime getCreationDate() {
        return creationDate;
    }

    public void setCreationDate(LocalDateTime creationDate) {
        this.creationDate = creationDate;
    }

    public boolean isValid() {
        return valid;
    }

    public void setValid(boolean valid) {
        this.valid = valid;
    }

    public User getOwner() {
        return owner;
    }

    public void setOwner(User owner) {
        this.owner = owner;
    }
}
