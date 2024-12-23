package com.example.nnprorocnikovyprojekt.entity;

import jakarta.persistence.*;
import org.springframework.security.core.GrantedAuthority;

import java.util.List;
import java.util.Objects;

@Entity
public class Authority implements GrantedAuthority {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Integer authorityId;

    @Column(unique = true)
    private String authorityName;

    @ManyToMany(fetch = FetchType.LAZY)
    @JoinTable(name = "USER_AUTHORITIES",
            joinColumns = @JoinColumn(name = "AUTHORITY_ID"),
            inverseJoinColumns = @JoinColumn(name = "USER_ID")
    )
    private List<User> users;

    public Authority() {
    }

    public Authority(String authorityName) {
        this.authorityName = authorityName;
    }

    @Override
    public String getAuthority() {
        return authorityName;
    }

    public Integer getAuthorityId() {
        return authorityId;
    }

    public void setAuthorityId(Integer authorityId) {
        this.authorityId = authorityId;
    }

    public String getAuthorityName() {
        return authorityName;
    }

    public void setAuthorityName(String authorityName) {
        this.authorityName = authorityName;
    }

    public List<User> getUsers() {
        return users;
    }

    public void setUsers(List<User> users) {
        this.users = users;
    }

    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (o == null || getClass() != o.getClass()) return false;
        Authority authority = (Authority) o;
        return authorityName.equals(authority.authorityName);
    }

    @Override
    public int hashCode() {
        return Objects.hash(authorityName);
    }
}
