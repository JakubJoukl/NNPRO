package com.example.nnprorocnikovyprojekt.services;

import com.example.nnprorocnikovyprojekt.entity.Conversation;
import com.example.nnprorocnikovyprojekt.entity.ConversationUser;
import com.example.nnprorocnikovyprojekt.entity.Message;
import com.example.nnprorocnikovyprojekt.entity.User;
import com.example.nnprorocnikovyprojekt.repositories.ConversationRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.messaging.simp.SimpMessagingTemplate;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;

@Service
public class ConversationService {

    @Autowired
    private ConversationRepository conversationRepository;

    @Autowired
    private SimpMessagingTemplate simpMessagingTemplate;

    @Autowired
    private MessageService messageService;

    public Conversation getConversationById(Integer conversationId){
        return conversationRepository.getConversationByConversationId(conversationId).orElseThrow(() -> new RuntimeException("Conversation not found"));
    }

    //Neukladame zpravy, ktere nejsme schopni odeslat?
    @Transactional(rollbackFor = Exception.class)
    public void sendMessageToAllSubscribersExceptUser(User user, Conversation conversation, String content) {
        //TODO zde pracovat s klici;
        Message message = new Message(user, conversation, content);
        messageService.saveMessage(message);
        List<ConversationUser> subscriptions = conversation.getActiveConversationUsers()
                .stream()
                .filter(conversationUser -> !conversationUser.getUser().getUsername().equals(user.getUsername()))
                .toList();
        subscriptions.forEach(subscription -> simpMessagingTemplate.convertAndSendToUser(subscription.getUser().getUsername(), "/topic/" + conversation.getConversationId(), content));
    }
}
