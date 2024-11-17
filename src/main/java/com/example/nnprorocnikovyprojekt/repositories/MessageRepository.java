package com.example.nnprorocnikovyprojekt.repositories;

import com.example.nnprorocnikovyprojekt.entity.Conversation;
import com.example.nnprorocnikovyprojekt.entity.ConversationUser;
import com.example.nnprorocnikovyprojekt.entity.Message;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;

import java.time.LocalDateTime;
import java.util.List;

public interface MessageRepository extends JpaRepository<Message, Integer> {
    @Query("SELECT m FROM Message m WHERE m.conversation = :conversation and (:conversationUser in m.conversation.conversationUsers) and m.dateSend >= :from and m.dateSend <= :to and (:validTo is null or m.validTo < :validTo)")
    List<Message> getMessageByConversationBetweenDatesValidTo(Conversation conversation, LocalDateTime from, LocalDateTime to, LocalDateTime validTo, ConversationUser conversationUser);
}
