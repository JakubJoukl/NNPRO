package com.example.nnprorocnikovyprojekt.repositories;

import com.example.nnprorocnikovyprojekt.entity.Conversation;
import com.example.nnprorocnikovyprojekt.entity.User;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;

import java.util.Optional;

public interface ConversationRepository extends JpaRepository<Conversation, Integer> {

    public Optional<Conversation> getConversationByConversationId(Integer conversationId);

    @Query("SELECT c FROM Conversation c JOIN ConversationUser cu ON cu.conversation = c WHERE cu.user = :user")
    Page<Conversation> getConversationsByUsername(@Param("user") User user, Pageable pageable);
}
