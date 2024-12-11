package com.example.nnprorocnikovyprojekt.scheduled;

import com.example.nnprorocnikovyprojekt.services.MessageService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.scheduling.annotation.Scheduled;
import org.springframework.stereotype.Component;
import org.springframework.transaction.annotation.Transactional;

@Component
public class Scheduler {
    @Autowired
    private MessageService messageService;

    @Scheduled(fixedDelay = 60 * 1000) //pripadne cron?
    @Transactional(rollbackFor = Exception.class)
    public void scheduleFixedDelayTask() {
        messageService.deleteExpiredMessages();
    }
}
