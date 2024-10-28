package com.example.nnprorocnikovyprojekt.repositories;

import com.example.nnprorocnikovyprojekt.entity.Conversation;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.Optional;

public interface ConversationRepository extends JpaRepository<Conversation, Integer> {

    public Optional<Conversation> getConversationByConversationId(Integer conversationId);
}
