package com.eva.backend.service;

import org.springframework.mail.javamail.JavaMailSender;
import org.springframework.mail.javamail.MimeMessageHelper;

import com.eva.backend.model.User;
import com.eva.backend.records.MailContent;
import com.eva.backend.repository.UserRepository;

import jakarta.mail.MessagingException;
import jakarta.mail.internet.MimeMessage;

public abstract class MailWithLinkService {
    private final UserRepository userRepository;
    private final JavaMailSender mailSender;

    public MailWithLinkService(UserRepository userRepository, JavaMailSender mailSender){
        this.userRepository = userRepository;
        this.mailSender = mailSender;
    }

    public User sendMail(String username, String endPoint) throws MessagingException {
        /* si le mail username existe, envoie un mail avec un lien comprenant un token */
        User user = userRepository.findByMail(username);
        if (user != null){
            MailContent mailContent =  generateMailContent(username, endPoint);
            MimeMessage message = configureMail(username, mailContent);
            mailSender.send(message);
            return user;
        }
        return null;
    }

    private MimeMessage configureMail(String username, MailContent mailContent) throws MessagingException{
        MimeMessage message = mailSender.createMimeMessage();                    
        MimeMessageHelper helper = new MimeMessageHelper(message, true, "UTF-8");
        helper.setFrom("noreply.eva.application@gmail.com");
        helper.setTo(username);
        helper.setSubject(mailContent.subject());
        helper.setText(mailContent.content(), true);
        return message;
    }

    protected abstract MailContent generateMailContent(String username, String endPoint);
    
}
