package com.example.nnprorocnikovyprojekt.repositories;

import com.example.nnprorocnikovyprojekt.entity.User;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.stereotype.Repository;

import java.util.Optional;

@Repository
public interface UserRepository extends JpaRepository<User, Integer> {

    Optional<User> getUserByUsername(String username);

    Page<User> findUsersByUsernameStartingWithAndUsernameNot(String searchTerm, String not, Pageable pageable);

    @Query("SELECT u  " +
            " FROM User u  " +
            " WHERE u.username LIKE :searchTerm% " +
            "  AND (u.banned = TRUE)")
    Page<User> getBannedUsers(String searchTerm, Pageable pageable);

    @Query("SELECT u  " +
            " FROM User u  " +
            " WHERE u.username LIKE :searchTerm% " +
            "  AND (u.banned IS NULL OR u.banned = FALSE)")
    Page<User> getNotBannedUsers(String searchTerm, Pageable pageable);
}
