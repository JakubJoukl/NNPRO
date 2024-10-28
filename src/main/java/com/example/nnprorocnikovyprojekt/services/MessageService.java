package com.example.nnprorocnikovyprojekt.services;

import com.example.nnprorocnikovyprojekt.entity.Message;
import com.example.nnprorocnikovyprojekt.repositories.MessageRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

@Service
public class MessageService {

    @Autowired
    private MessageRepository messageRepository;

    @Transactional(rollbackFor = Exception.class)
    public void saveMessage(Message message){
        message.getConversation().getMessages().add(message);
        messageRepository.save(message);
    }
}
