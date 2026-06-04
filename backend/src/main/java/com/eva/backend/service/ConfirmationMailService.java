package com.eva.backend.service;

import org.springframework.beans.factory.annotation.Value;
import org.springframework.mail.javamail.JavaMailSender;
import org.springframework.stereotype.Service;

import com.eva.backend.records.MailContent;
import com.eva.backend.repository.UserRepository;

@Service
public class ConfirmationMailService extends MailWithLinkService {
    private final JWTService jwtService;

    @Value("${app.frontend-url}")
    private String frontendUrl;

    public ConfirmationMailService(UserRepository userRepository, JavaMailSender mailSender, JWTService jwtService){
        super(userRepository, mailSender);
        this.jwtService = jwtService;
    }

    @Override
    protected MailContent generateMailContent(String username){
        String token = jwtService.generateToken(username, 600000);
        String link = frontendUrl + "/verifyMail?token=" + token;
        String subject = "Création de compte sur EVA";
        String content = "<h3>Confirmation de la création de votre compte EVA</h3>" +
                  "<p>Cliquez sur le lien ci-dessous pour confirmer votre inscription :</p>" +
                  "<a href=\"" + link + "\">Confirmer l'inscription</a>";
        return new MailContent(subject, content);
    }
}
