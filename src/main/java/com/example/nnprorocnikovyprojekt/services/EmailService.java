package com.example.nnprorocnikovyprojekt.services;

import com.example.nnprorocnikovyprojekt.entity.ResetToken;
import com.example.nnprorocnikovyprojekt.entity.User;
import com.sun.mail.util.MailSSLSocketFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import javax.mail.MessagingException;
import javax.mail.PasswordAuthentication;
import javax.mail.Session;
import javax.mail.Transport;
import javax.mail.internet.InternetAddress;
import javax.mail.internet.MimeMessage;
import java.security.GeneralSecurityException;
import java.time.format.DateTimeFormatter;
import java.util.Properties;
import java.util.UUID;

@Service
public class EmailService {

    @Autowired
    private UserService userService;

    DateTimeFormatter formatter = DateTimeFormatter.ofPattern("dd.MMM.yyyy HH:mm");

    Properties properties = System.getProperties();

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
    public void sendResetTokenEmail(User user)  {
        if(user == null) throw new RuntimeException("User is null");

        Session session = Session.getInstance(properties, new javax.mail.Authenticator() {

            protected PasswordAuthentication getPasswordAuthentication() {
                return new PasswordAuthentication("semestralkaa@gmail.com", "akxx jzcv whcy zptj");

            }

        });

        session.setDebug(true);

        try {
            // Create a default MimeMessage object.
            MimeMessage message = new MimeMessage(session);

            UUID uuid = UUID.randomUUID();
            String token = uuid.toString();

            userService.deactivateUserResetTokens(user);

            ResetToken resetToken = new ResetToken(user, token);
            user.getResetTokens().add(resetToken);
            userService.saveResetToken(resetToken);

            message.setFrom(new InternetAddress("semestralkaa@gmail.com"));
            message.setRecipients(MimeMessage.RecipientType.TO, user.getEmail());
            message.setSubject("Test email reset hesla pro " + user.getEmail());

            String htmlContent = "<h1>Reset hesla</h1><p>Token pro reset hesla je " + token +
                    " a platí do " + formatter.format(resetToken.getExpirationDate()) + "</p>";
            message.setContent(htmlContent, "text/html; charset=utf-8");
            Transport.send(message);
            System.out.println("Sent message successfully....");
        } catch (MessagingException mex) {
            mex.printStackTrace();
        }

    }
}
