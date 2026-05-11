package com.eva.backend.service;

import java.time.LocalDate;
import java.util.Map;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.security.authentication.AuthenticationManager;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.core.Authentication;
import org.springframework.security.crypto.bcrypt.BCryptPasswordEncoder;
import org.springframework.stereotype.Service;
import org.springframework.http.ResponseCookie;
import org.springframework.mail.javamail.JavaMailSender;

import com.eva.backend.model.User;
import com.eva.backend.model.UserAdditionalData;
import com.eva.backend.repository.UserRepository;

import jakarta.mail.MessagingException;

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

    /**
     * Tente de lire un token JWT de manière sécurisée (sans lever d'exception si invalide).
     * Retourne null si le token est vide, expiré, ou sa signature ne correspond pas.
     * Utile pour les routes publiques qui acceptent un utilisateur authentifié optionnel.
     */
    public User findByTokenSafely(String token){
        if (token == null || token.isEmpty()) {
            return null;
        }
        try {
            String username = jwtService.extractUsername(token);
            return userRepository.findByMail(username);
        } catch (Exception e) {
            // Token invalide, expiré, ou signature ne correspond pas
            return null;
        }
    }

    public User findByMail(String mail){ 
        return userRepository.findByMail(mail);
    }

    public User findByMailWithInstitutions(String mail){
        return userRepository.findByMailWithInstitutions(mail);
    }

    public User findByMailWithExperimentations(String mail){
        return userRepository.findByMailWithExperimentations(mail);
    }

    public User sendConfirmationMail(String username) throws MessagingException {
        MailWithLinkService mailService = new ConfirmationMailService(userRepository, mailSender, jwtService);
        return mailService.sendMail(username);
    }

    public User sendRecoveryMail(String username) throws MessagingException {
        MailWithLinkService mailService = new RecoveryMailService(userRepository, mailSender, jwtService);
        return mailService.sendMail(username);
    }

    public boolean isTokenExpired(String token){
         return jwtService.isTokenExpired(token);
    }

    public void update(Map<String, Object> body, User userToUpdate, BCryptPasswordEncoder encoder){
        String firstname = (String) body.get("firstname");    
        if (firstname != null){
            userToUpdate.setFirstname(firstname);
        }

        String lastname = (String) body.get("lastname");
        if (lastname != null){
            userToUpdate.setLastname(lastname);
        }

        String password = (String) body.get("password");
        if (password != null && password.length() >= 8){
            userToUpdate.setPassword(encoder.encode(password));
        }

        userToUpdate = updateAdditionalData(body, userToUpdate);

        saveUpdatedUser(userToUpdate);
    }

    private User updateAdditionalData(Map<String, Object> body, User userToUpdate){
        UserAdditionalData additionalData = userToUpdate.getAdditionalData();

        if (additionalData == null) {
            additionalData = new UserAdditionalData();
        }

        Boolean acceptMap = (Boolean) body.get("acceptMap");
        if (acceptMap != null){
            additionalData.setAcceptMap(acceptMap);
        }

        Boolean acceptContact = (Boolean) body.get("acceptContact");
        if (acceptContact != null){
            additionalData.setAcceptContact(acceptContact);
        }

        String birthday = (String) body.get("birthday");
        if (birthday != null){
            additionalData.setBirthday(LocalDate.parse(birthday));
        }

        String gender = (String) body.get("gender");
        if (gender != null){
            additionalData.setGender(gender);
        }

        String job = (String) body.get("job");
        if (job != null){
            additionalData.setJob(job);
        }

        String specializedTopics = (String) body.get("specializedTopics");
        if (specializedTopics != null){
            additionalData.setSpecializedTopics(specializedTopics);
        }

        String otherSpecialization = (String) body.get("otherSpecialization");
        if (otherSpecialization != null){
            additionalData.setOtherSpecialization(otherSpecialization);
        }

        String teacherBehaviour = (String) body.get("teacherBehaviour");
        if (teacherBehaviour != null){
            additionalData.setTeacherBehaviour(teacherBehaviour);
        }

        String freeField = (String) body.get("freeField");
        if (freeField != null){
            additionalData.setFreeField(freeField);
        }
        
        userToUpdate.setAdditionalData(additionalData);
        return userToUpdate;
    }
}
