package com.example.nnprorocnikovyprojekt.services;

import org.springframework.scheduling.annotation.Async;
import org.springframework.stereotype.Service;

import javax.mail.MessagingException;
import javax.mail.Transport;
import javax.mail.internet.InternetAddress;
import javax.mail.internet.MimeMessage;

@Service
public class EmailServiceAsync {

    @Async
    public void sendEmail(MimeMessage message, String recipient, String subject, String emailHtmlContent) throws MessagingException {
        message.setFrom(new InternetAddress("semestralkaa@gmail.com"));
        message.setRecipients(MimeMessage.RecipientType.TO, recipient);
        message.setSubject(subject);
        String htmlContent = emailHtmlContent;
        message.setContent(htmlContent, "text/html; charset=utf-8");
        Transport.send(message);
        System.out.println("Sent message successfully....");
    }
}
