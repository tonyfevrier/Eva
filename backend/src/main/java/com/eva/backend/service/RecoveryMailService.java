package com.eva.backend.service;

import org.springframework.beans.factory.annotation.Value;
import org.springframework.mail.javamail.JavaMailSender;
import org.springframework.stereotype.Service;

import com.eva.backend.records.MailContent;
import com.eva.backend.repository.UserRepository;

@Service
public class RecoveryMailService extends MailWithLinkService {
    private final JWTService jwtService;

    @Value("${app.frontend-url}")
    private String frontendUrl;

    public RecoveryMailService(UserRepository userRepository, JavaMailSender mailSender, JWTService jwtService){
        super(userRepository, mailSender);
        this.jwtService = jwtService;
    }

    @Override
    protected MailContent generateMailContent(String username){
        String token = jwtService.generateToken(username, 600000);
        String subject = "Récupération de mot de passe pour l'application EVA";
        String link = frontendUrl + "/pwdChange?token=" + token;
        String content = "<h3>Réinitialisation de mot de passe de votre compte EVA</h3>" +
                  "<p>Cliquez sur le lien ci-dessous pour réinitialiser votre mot de passe :</p>" +
                  "<a href=\"" + link + "\">Réinitialiser mon mot de passe</a>";
        return new MailContent(subject, content);
    }
}
