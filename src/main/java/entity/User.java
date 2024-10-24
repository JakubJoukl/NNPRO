package entity;

import jakarta.persistence.*;

import java.util.ArrayList;
import java.util.List;

@Entity
@Table(name = "USER")
public class User {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Integer userId;

    @Column
    private String username;

    @Column
    private String email;

    @Column
    private String hashedPassword;

    @Column
    private String salt;

    @OneToMany(mappedBy = "owner", fetch = FetchType.LAZY, orphanRemoval = true, cascade = CascadeType.ALL)
    private List<PublicKey> publicKey = new ArrayList<>();

    @OneToMany(mappedBy = "user", fetch = FetchType.LAZY, orphanRemoval = true, cascade = CascadeType.ALL)
    private List<ResetToken> resetTokens;

    @ManyToMany(cascade = CascadeType.ALL, fetch = FetchType.LAZY)
    @JoinTable(name = "USER_CONTACT", joinColumns =
    @JoinColumn(name = "USER_ID"), inverseJoinColumns = @JoinColumn(name = "CONTACT_USER_ID"))
    private List<User> contacts;

    @OneToMany(mappedBy = "user",cascade = CascadeType.ALL, fetch = FetchType.LAZY)
    private List<ConversationUser> conversationUsers;

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

    public String getHashedPassword() {
        return hashedPassword;
    }

    public void setHashedPassword(String hashedPassword) {
        this.hashedPassword = hashedPassword;
    }

    public String getSalt() {
        return salt;
    }

    public void setSalt(String salt) {
        this.salt = salt;
    }

    public List<PublicKey> getPublicKey() {
        return publicKey;
    }

    public void setPublicKey(List<PublicKey> publicKey) {
        this.publicKey = publicKey;
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
}
