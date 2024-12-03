package com.example.nnprorocnikovyprojekt.entity;

import jakarta.persistence.*;
import jakarta.validation.constraints.Email;
import jakarta.validation.constraints.Min;
import jakarta.validation.constraints.NotNull;
import jakarta.validation.constraints.Pattern;
import org.hibernate.validator.constraints.Length;
import org.springframework.security.core.GrantedAuthority;
import org.springframework.security.core.authority.SimpleGrantedAuthority;
import org.springframework.security.core.userdetails.UserDetails;

import java.util.ArrayList;
import java.util.Collection;
import java.util.List;
import java.util.Optional;

@Entity
@Table(name = "USER")
public class User implements UserDetails {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Integer userId;

    @Column
    @Pattern(regexp = "^[A-Za-z][A-Za-z0-9_ ]{7,29}$")
    @NotNull
    private String username;

    @Column
    @Email(regexp = "^[a-zA-Z0-9.!#$%&'*+/=?^_`{|}~-]+@[a-zA-Z0-9-]+(?:\\.[a-zA-Z0-9-]+)*$")
    @NotNull
    private String email;

    @Column
    @Length(min = 12)
    @NotNull
    private String password;

    @OneToMany(mappedBy = "owner", fetch = FetchType.LAZY, orphanRemoval = true, cascade = CascadeType.ALL)
    private List<PublicKey> publicKeys = new ArrayList<>();

    @OneToMany(mappedBy = "user", fetch = FetchType.LAZY, orphanRemoval = true, cascade = CascadeType.ALL)
    private List<ResetToken> resetTokens = new ArrayList<>();

    @OneToMany(mappedBy = "user", fetch = FetchType.LAZY, orphanRemoval = true, cascade = CascadeType.ALL)
    private List<VerificationCode> verificationCodes = new ArrayList<>();

    @ManyToMany(cascade = CascadeType.ALL, fetch = FetchType.LAZY)
    @JoinTable(name = "USER_CONTACT", joinColumns =
    @JoinColumn(name = "USER_ID"), inverseJoinColumns = @JoinColumn(name = "CONTACT_USER_ID"))
    private List<User> contacts = new ArrayList<>();

    @OneToMany(mappedBy = "user",cascade = CascadeType.ALL, fetch = FetchType.LAZY)
    private List<ConversationUser> conversationUsers = new ArrayList<>();

    protected User() {

    }

    public User(String username, String password, String email) {
        this.username = username;
        this.password = password;
        this.email = email;
    }

    public Integer getUserId() {
        return userId;
    }

    public void setUserId(Integer userId) {
        this.userId = userId;
    }

    public String getUsername() {
        return username;
    }

    public void setUsername(String username) {
        this.username = username;
    }

    public String getEmail() {
        return email;
    }

    public void setEmail(String email) {
        this.email = email;
    }

    public String getPassword() {
        return password;
    }

    public void setPassword(String password) {
        this.password = password;
    }

    public List<PublicKey> getPublicKeys() {
        return publicKeys;
    }

    public Optional<PublicKey> getActivePublicKey() {
        return publicKeys.stream().filter(PublicKey::isValid).findFirst();
    }

    public void setPublicKeys(List<PublicKey> publicKey) {
        this.publicKeys = publicKey;
    }

    public List<ResetToken> getResetTokens() {
        return resetTokens;
    }

    public void setResetTokens(List<ResetToken> resetTokens) {
        this.resetTokens = resetTokens;
    }

    public List<User> getContacts() {
        return contacts;
    }

    public void setContacts(List<User> contacts) {
        this.contacts = contacts;
    }

    public List<ConversationUser> getConversationUsers() {
        return conversationUsers;
    }

    public void setConversationUsers(List<ConversationUser> conversationUsers) {
        this.conversationUsers = conversationUsers;
    }

    public List<VerificationCode> getVerificationCodes() {
        return verificationCodes;
    }

    public void setVerificationCodes(List<VerificationCode> verificationCodes) {
        this.verificationCodes = verificationCodes;
    }

    /** implemented methods **/
    //TODO pokud bude třeba tak přesunout do DB
    @Override
    public Collection<? extends GrantedAuthority> getAuthorities() {
        List<GrantedAuthority> authorities = new ArrayList<>();
        authorities.add(new SimpleGrantedAuthority("USER"));
        return authorities;
    }

    @Override
    public boolean isAccountNonExpired() {
        return UserDetails.super.isAccountNonExpired();
    }

    @Override
    public boolean isAccountNonLocked() {
        return UserDetails.super.isAccountNonLocked();
    }

    @Override
    public boolean isCredentialsNonExpired() {
        return UserDetails.super.isCredentialsNonExpired();
    }

    @Override
    public boolean isEnabled() {
        return UserDetails.super.isEnabled();
    }
}
