package com.example.nnprorocnikovyprojekt.config;

import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.context.annotation.Profile;
import org.springframework.messaging.*;
import org.springframework.messaging.simp.SimpMessagingTemplate;

import java.util.Map;

@Configuration
@Profile("test")
public class TestMessagingConfig {
    @Bean
    public SimpMessagingTemplate simpMessagingTemplate() {
        SubscribableChannel dummyChannel = new SubscribableChannel() {
            @Override
            public boolean subscribe(MessageHandler handler) {
                return true;
            }

            @Override
            public boolean unsubscribe(MessageHandler handler) {
                return true;
            }

            @Override
            public boolean send(Message<?> message, long timeout) {
                return true;
            }

            @Override
            public boolean send(Message<?> message) {
                return true;
            }
        };

        return new SimpMessagingTemplate(dummyChannel) {
            @Override
            public void convertAndSend(String destination, Object payload) {
                // Přepsání, které nic neprovádí
            }
        };
    }
}
