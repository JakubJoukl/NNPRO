package com.example.nnprorocnikovyprojekt.entity;

import jakarta.persistence.*;
import jakarta.validation.constraints.Email;
import jakarta.validation.constraints.NotNull;
import jakarta.validation.constraints.Pattern;
import org.hibernate.validator.constraints.Length;
import org.springframework.security.core.GrantedAuthority;
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

    @Column(unique = true)
    @Pattern(regexp = "^[A-Za-z][A-Za-z0-9_ ]{7,29}$")
    @NotNull
    private String username;

    @Column(unique = true)
    @Email(regexp = "^[a-zA-Z0-9.!#$%&'*+/=?^_`{|}~-]+@[a-zA-Z0-9-]+(?:\\.[a-zA-Z0-9-]+)*$")
    @NotNull
    private String email;

    @OneToMany(mappedBy = "user", fetch = FetchType.EAGER, orphanRemoval = true, cascade = CascadeType.ALL)
    private List<AuthToken> authTokens = new ArrayList<>();

    @Column
    @Length(min = 12)
    @NotNull
    private String password;

    @Column
    private boolean banned;

    @OneToMany(mappedBy = "owner", fetch = FetchType.LAZY, orphanRemoval = true, cascade = CascadeType.ALL)
    private List<PublicKey> publicKeys = new ArrayList<>();

    @OneToMany(mappedBy = "user", fetch = FetchType.LAZY, orphanRemoval = true, cascade = CascadeType.ALL)
    private List<ResetToken> resetTokens = new ArrayList<>();

    @OneToMany(mappedBy = "user", fetch = FetchType.LAZY, orphanRemoval = true, cascade = CascadeType.ALL)
    private List<VerificationCode> verificationCodes = new ArrayList<>();

    @ManyToMany(fetch = FetchType.LAZY)
        @JoinTable(name = "USER_CONTACT",
                joinColumns = @JoinColumn(name = "USER_ID"),
                inverseJoinColumns = @JoinColumn(name = "CONTACT_USER_ID")
    )
    private List<User> contacts = new ArrayList<>();

    @OneToMany(mappedBy = "user", cascade = CascadeType.ALL, fetch = FetchType.LAZY)
    private List<ConversationUser> conversationUsers = new ArrayList<>();

    @ManyToMany(mappedBy = "users", fetch = FetchType.EAGER)
    private List<Authority> authorities = new ArrayList<>();

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

    public ResetToken getActiveResetToken() {
        return resetTokens.stream().filter(ResetToken::isValid).findFirst().orElse(null);
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

    public VerificationCode getActiveVerificationCode() {
        return verificationCodes.stream().filter(VerificationCode::isValid).findFirst().orElse(null);
    }

    public void setVerificationCodes(List<VerificationCode> verificationCodes) {
        this.verificationCodes = verificationCodes;
    }

    @Override
    public Collection<? extends GrantedAuthority> getAuthorities() {
        return authorities;
    }

    public boolean containsAuthority(String authorityName) {
        return authorities.stream().anyMatch(authority -> authority.getAuthority().equals(authorityName));
    }

    public void addAuthority(Authority authority) {
        authority.getUsers().add(this);
        this.authorities.add(authority);
    }

    public boolean removeAuthority(String authorityName) {
        Authority authorityToRemove = authorities.stream()
                .filter(authority -> authority.getAuthorityName().equals(authorityName))
                .findFirst().orElse(null);
        if(authorityToRemove == null) return false;
        else {
            authorities.remove(authorityToRemove);
            authorityToRemove.getUsers().remove(this);
            return true;
        }
    }

    public void setAuthorities(List<Authority> authorities) {
        this.authorities = authorities;
    }

    public List<AuthToken> getAuthTokens() {
        return authTokens;
    }

    public void setAuthTokens(List<AuthToken> authTokens) {
        this.authTokens = authTokens;
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
        return !banned;
    }

    public void setBanned(Boolean banned) {
        this.banned = banned;
    }

    public AuthToken getActiveAuthToken() {
        return authTokens.stream().filter(AuthToken::isValid).findFirst().orElse(null);
    }
}
