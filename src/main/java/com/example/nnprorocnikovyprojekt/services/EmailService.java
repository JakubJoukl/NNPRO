package com.example.nnprorocnikovyprojekt.services;

import com.example.nnprorocnikovyprojekt.entity.ResetToken;
import com.example.nnprorocnikovyprojekt.entity.User;
import com.example.nnprorocnikovyprojekt.entity.VerificationCode;
import com.sun.mail.util.MailSSLSocketFactory;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.scheduling.annotation.Async;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import javax.mail.MessagingException;
import javax.mail.PasswordAuthentication;
import javax.mail.Session;
import javax.mail.internet.MimeMessage;
import java.security.GeneralSecurityException;
import java.time.format.DateTimeFormatter;
import java.util.Properties;

@Service
public class EmailService {

    @Autowired
    private EmailServiceAsync emailServiceAsync;

    @Autowired
    private UserService userService;

    DateTimeFormatter formatter = DateTimeFormatter.ofPattern("dd.MMM.yyyy HH:mm");
    Properties properties = System.getProperties();

    private static final Logger logger = LoggerFactory.getLogger(EmailService.class);

    //Heslo: akxx jzcv whcy zptj

    public EmailService() {
        MailSSLSocketFactory sf = null;
        try {
            sf = new MailSSLSocketFactory();
        } catch (GeneralSecurityException e) {
            throw new RuntimeException(e);
        }
        sf.setTrustAllHosts(true);

        // Setup mail server
        properties.put("mail.smtp.host", "smtp.gmail.com");
        properties.put("mail.smtp.port", "587");
        properties.put("mail.smtp.starttls.enable", "true");
        properties.put("mail.smtp.auth", "true");
        properties.put("mail.smtp.ssl.socketFactory", sf);
    }

    @Transactional(rollbackFor = Exception.class)
    public VerificationCode sendVerificationCodeEmail(User user){
        if(user == null) throw new RuntimeException("User is null");

        Session session = initiateSession();

        VerificationCode verificationCode = userService.generateVerificationCodeForUser(user);

        try {
            MimeMessage message = new MimeMessage(session);
            String recipient = user.getEmail();
            String subject = "Verification code for secure chat";
            String emailHtmlContent = "<p>Your verification code is " + verificationCode.getVerificationCode() + "<p>";

            emailServiceAsync.sendEmail(message, recipient, subject, emailHtmlContent);
        } catch (MessagingException mex) {
            return null;
        }
        return verificationCode;
    }

    private Session initiateSession() {
        Session session = Session.getInstance(properties, new javax.mail.Authenticator() {

            protected PasswordAuthentication getPasswordAuthentication() {
                return new PasswordAuthentication("semestralkaa@gmail.com", "akxx jzcv whcy zptj");

            }

        });

        session.setDebug(true);
        return session;
    }

    @Async
    @Transactional(rollbackFor = Exception.class)
    public void sendResetTokenEmail(User user)  {
        if(user == null) throw new RuntimeException("User is null");

        Session session = initiateSession();

        try {
            MimeMessage message = new MimeMessage(session);

            ResetToken resetToken = userService.generateResetTokenForUser(user);

            String subject = "Test email reset hesla pro " + user.getEmail();
            String emailHtmlContent = "<h1>Reset hesla</h1><p>Token pro reset hesla je " + resetToken.getToken() + " a platí do " + formatter.format(resetToken.getExpirationDate()) + "</p>";

            emailServiceAsync.sendEmail(message, user.getEmail(), subject, emailHtmlContent);
        } catch (MessagingException mex) {
            logger.error("Failed to send email: " + mex.getMessage());
        }
    }
}
