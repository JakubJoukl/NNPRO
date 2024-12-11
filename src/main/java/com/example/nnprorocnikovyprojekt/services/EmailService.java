package com.example.nnprorocnikovyprojekt.services;

import com.example.nnprorocnikovyprojekt.advice.exceptions.UnauthorizedException;
import com.example.nnprorocnikovyprojekt.entity.ResetToken;
import com.example.nnprorocnikovyprojekt.entity.User;
import com.example.nnprorocnikovyprojekt.entity.VerificationCode;
import com.sun.mail.util.MailSSLSocketFactory;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.scheduling.annotation.Async;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import javax.mail.MessagingException;
import javax.mail.PasswordAuthentication;
import javax.mail.Session;
import javax.mail.internet.MimeMessage;
import java.security.GeneralSecurityException;
import java.time.ZoneOffset;
import java.time.format.DateTimeFormatter;
import java.util.Properties;

@Service
public class EmailService {
    private String smtpHost;

    private String smtpPort;

    private String smtpStartTlsEnable;

    private String smtpAuth;

    private String mailUsername;

    private String mailPassword;

    @Autowired
    private EmailServiceAsync emailServiceAsync;

    @Autowired
    private UserService userService;

    DateTimeFormatter formatter = DateTimeFormatter.ofPattern("dd.MM.yyyy HH:mm");
    Properties properties = System.getProperties();

    private static final Logger logger = LoggerFactory.getLogger(EmailService.class);

    //Heslo: akxx jzcv whcy zptj

    public EmailService(@Value("${mail.smtp.host}") String smtpHost, @Value("${mail.smtp.port}") String smtpPort, @Value("${mail.smtp.starttls.enable}") String smtpStartTlsEnable,
                        @Value("${mail.smtp.auth}") String smtpAuth, @Value("${mail.username}") String mailUsername, @Value("${mail.password}") String mailPassword) {
        this.smtpHost = smtpHost;
        this.smtpPort = smtpPort;
        this.smtpStartTlsEnable = smtpStartTlsEnable;
        this.smtpAuth = smtpAuth;
        this.mailUsername = mailUsername;
        this.mailPassword = mailPassword;


        MailSSLSocketFactory sf = null;
        try {
            sf = new MailSSLSocketFactory();
        } catch (GeneralSecurityException e) {
            throw new RuntimeException(e);
        }
        sf.setTrustAllHosts(true);

        // Setup mail server
        System.out.println("mailUsername = " + smtpHost);
        properties.put("mail.smtp.host", smtpHost);
        properties.put("mail.smtp.port", smtpPort);
        properties.put("mail.smtp.starttls.enable", smtpStartTlsEnable);
        properties.put("mail.smtp.auth", smtpAuth);
        properties.put("mail.smtp.ssl.socketFactory", sf);
    }

    @Transactional(rollbackFor = Exception.class)
    public VerificationCode sendVerificationCodeEmail(User user){
        if(user == null) throw new UnauthorizedException("User is null");

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
                return new PasswordAuthentication(mailUsername, mailPassword);

            }

        });

        session.setDebug(true);
        return session;
    }

    @Transactional(rollbackFor = Exception.class)
    public void sendResetTokenEmail(User user)  {
        if(user == null) throw new RuntimeException("User is null");

        Session session = initiateSession();

        try {
            MimeMessage message = new MimeMessage(session);

            ResetToken resetToken = userService.generateResetTokenForUser(user);

            String subject = "Test email reset hesla pro " + user.getEmail();
            String emailHtmlContent = "<h1>Reset hesla</h1><p>Token pro reset hesla je " + resetToken.getToken() + " a platí do " + formatter.format(resetToken.getExpirationDate().atOffset(ZoneOffset.UTC)) + "</p>";

            emailServiceAsync.sendEmail(message, user.getEmail(), subject, emailHtmlContent);
        } catch (MessagingException mex) {
            logger.error("Failed to send email: " + mex.getMessage());
        }
    }
}
