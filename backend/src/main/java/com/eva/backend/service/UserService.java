package com.eva.backend.service;

import java.util.Map;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.security.authentication.AuthenticationManager;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.core.Authentication;
import org.springframework.stereotype.Service;
import org.springframework.http.ResponseCookie;
import org.springframework.mail.javamail.JavaMailSender;
import org.springframework.mail.javamail.MimeMessageHelper;

import com.eva.backend.model.User;
import com.eva.backend.repository.UserRepository;

import jakarta.mail.MessagingException;
import jakarta.mail.internet.MimeMessage;

import com.eva.backend.records.CookieEssentials;
import com.eva.backend.records.TwoCookies;


@Service
public class UserService {
    
    @Autowired
    private UserRepository userRepository;

    @Autowired
    private AuthenticationManager authManager;

    @Autowired
    private CookieService cookieService;

    @Autowired
    private JWTService jwtService;

    @Autowired
    private JavaMailSender mailSender;

    public User saveUser(User user){
        User existingUser = userRepository.findByMail(user.getMail());
        if (existingUser != null){
            throw new IllegalArgumentException("Cet email est déjà associé à un compte");
        }
        return userRepository.save(user);
    }

    public User saveUpdatedUser(User user){
        return userRepository.save(user);
    }

    public TwoCookies<CookieEssentials> verify(User user){
        Authentication authentication = authManager.authenticate(
            new UsernamePasswordAuthenticationToken(user.getUsername(), user.getPassword()));
        if (authentication.isAuthenticated()){
            CookieEssentials accessCookie = cookieService.generateAccessCookie(user);
            CookieEssentials refreshCookie = cookieService.generateRefreshCookie(user);
            return new TwoCookies<CookieEssentials>(accessCookie, refreshCookie);
        } 
        return null; 
    }

    public TwoCookies<ResponseCookie> logout() {
        // cookies qui remplacent les précédents
        ResponseCookie delAccessCookie = ResponseCookie.from("jwt", "").path("/").maxAge(0).build();
        ResponseCookie delRefreshCookie = ResponseCookie.from("jwt-refresh", "").path("/").maxAge(0).build();
        return new TwoCookies<ResponseCookie>(delAccessCookie, delRefreshCookie);
    }

    public CookieEssentials refresh(String refreshToken){
        String username = jwtService.extractUsername(refreshToken);
        User user = userRepository.findByMail(username);
        if (jwtService.validateToken(refreshToken, user)){
            return cookieService.generateAccessCookie(user); 
        }
        return null;
    }

    public Iterable<User> getAllUsers(){
        return userRepository.findAll();
    }

    public void delete(User user){
        userRepository.delete(user);
    }

    public User findByToken(String token){
        String username = jwtService.extractUsername(token);
        return userRepository.findByMail(username);
    }

    public User sendRecoveryMail(String username) throws MessagingException {
        /* si le mail username existe, envoie un mail avec un lien comprenant un token */
        User user = userRepository.findByMail(username);
        if (user != null){
            String token = jwtService.generateToken(username, 600000);
            MimeMessage message = configureMail(token, username);
            mailSender.send(message);
            return user;
        }
        return null;
    }

    private MimeMessage configureMail(String token, String username) throws MessagingException{
        MimeMessage message = mailSender.createMimeMessage();                    
        MimeMessageHelper helper = new MimeMessageHelper(message, true, "UTF-8");
        helper.setFrom("noreply.eva.application@gmail.com");
        helper.setTo(username);
        helper.setSubject("Récupération de mot de passe pour l'application EVA");

        String recoveryLink = "http://localhost:5173/pwdChange?token=" + token;

        helper.setText(
            "<h3>Réinitialisation de mot de passe de votre compte EVA</h3>" +
            "<p>Cliquez sur le lien ci-dessous pour réinitialiser votre mot de passe :</p>" +
            "<a href=\"" + recoveryLink + "\">Réinitialiser mon mot de passe</a>",
            true  // true = HTML, false = texte brut
        );
        return message;
    }

    public boolean isTokenExpired(String token){
         return jwtService.isTokenExpired(token);
    }
}
