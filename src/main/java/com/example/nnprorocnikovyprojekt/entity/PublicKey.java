package com.example.nnprorocnikovyprojekt.entity;

import jakarta.persistence.*;
import jakarta.validation.constraints.NotNull;

import java.time.Instant;

@Entity
@Table(name = "PUBLIC_KEY")
public class PublicKey {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Integer publicKeyId;

    @Column
    @NotNull
    private String keyValue;

    @Column
    @NotNull
    private Instant creationDate;

    @Column
    @NotNull
    private boolean valid;

    @ManyToOne
    @JoinColumn(name = "owner_id", nullable = false)
    private User owner;

    public PublicKey() {
    }

    public PublicKey(String keyValue, Instant creationDate, boolean valid, User owner) {
        this.keyValue = keyValue;
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

    public String getKeyValue() {
        return keyValue;
    }

    public void setKeyValue(String key) {
        this.keyValue = key;
    }

    public Instant getCreationDate() {
        return creationDate;
    }

    public void setCreationDate(Instant creationDate) {
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
