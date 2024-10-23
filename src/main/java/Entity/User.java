package Entity;

import jakarta.persistence.*;

import java.util.ArrayList;
import java.util.List;

@Entity
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
}
