package com.example.nnprorocnikovyprojekt.repositories;

import com.example.nnprorocnikovyprojekt.entity.Authority;
import org.springframework.data.jpa.repository.JpaRepository;

public interface AuthorityRepository extends JpaRepository<Authority, Integer> {
    public Authority getAuthorityByAuthorityName(String authorityName);
}
