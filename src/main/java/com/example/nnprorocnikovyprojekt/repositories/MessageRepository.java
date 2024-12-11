package com.example.nnprorocnikovyprojekt.repositories;

import com.example.nnprorocnikovyprojekt.entity.Conversation;
import com.example.nnprorocnikovyprojekt.entity.ConversationUser;
import com.example.nnprorocnikovyprojekt.entity.Message;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;

import java.time.Instant;
import java.util.List;

public interface MessageRepository extends JpaRepository<Message, Integer> {
    @Query("    SELECT m \n" +
            "    FROM Message m \n" +
            "    WHERE m.conversation = :conversation \n" +
            "      AND :conversationUser MEMBER OF m.conversation.conversationUsers\n" +
            "      AND (m.dateSend >= :from AND m.dateSend <= :to)\n" +
            "      AND (m.validTo IS NULL OR m.validTo >= :validTo)")
    Page<Message> getMessageByConversationBetweenDatesValidTo(Pageable pageable, Conversation conversation, Instant from, Instant to, Instant validTo, ConversationUser conversationUser);

    Message getMessageByMessageId(Integer messageId);

    public List<Message> getMessagesByValidToBefore(Instant now);
}
