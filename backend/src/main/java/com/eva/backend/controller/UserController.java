package com.eva.backend.controller;

import java.time.LocalDate;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.Optional;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.ResponseEntity;
import org.springframework.http.HttpHeaders;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseCookie;
import org.springframework.security.crypto.bcrypt.BCryptPasswordEncoder;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.PutMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RestController;

import com.eva.backend.model.Institution;
import com.eva.backend.model.User;
import com.eva.backend.model.UserAdditionalData;
import com.eva.backend.service.UserService;
import com.eva.backend.records.CookieEssentials;
import com.eva.backend.records.TwoCookies;

import jakarta.mail.MessagingException;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.validation.Valid;

import org.springframework.web.bind.annotation.DeleteMapping;
import org.springframework.web.bind.annotation.GetMapping;



@RestController
@RequestMapping("/auth")
public class UserController {
    @Autowired 
    private UserService userService;

    @Autowired
    private RequestUtils requestUtils;

    private BCryptPasswordEncoder encoder = new BCryptPasswordEncoder(12);

    // @Valid valide les contraintes de forme des inputs (mail, pwd) avant d'entrer dans la méthode.
    // Permet de le faire sur le mot de passe original et pas hashé.
    @PostMapping("/register")
    public ResponseEntity<?> register(@Valid @RequestBody User user) throws MessagingException{
        System.out.println("User: " + user.getMail());
        user.setPassword(encoder.encode(user.getPassword()));
        user.setEmailVerified(false);
        User savedUser = userService.saveUser(user);
        System.out.println("User saved successfully: " + savedUser.getId());

        userService.sendRecoveryMail(savedUser.getMail(), "verifyMail");
        return ResponseEntity.ok(savedUser);
    }  


    @PostMapping("/confirm")
    public ResponseEntity<?> confirmRegistration(@RequestBody Map<String, String> body) {
        /* Vérifie si le cookie du lien est toujours valide et change emailVerified pour l'utilisateur */
        String token = body.get("token");
        
        if (token == null || token.isEmpty() || userService.isTokenExpired(token)){
            return ResponseEntity.status(HttpStatus.BAD_REQUEST)
                                .body(Map.of("message", "Le lien fourni a expiré, relancer la procédure"));
        }

        User user = userService.findByToken(token);
        if (user != null){
            user.setEmailVerified(true);;
            userService.saveUpdatedUser(user);
            return ResponseEntity.ok(Map.of("message", "Compte bien créé"));
        }    

        return ResponseEntity.status(HttpStatus.BAD_REQUEST)
                             .body(Map.of("message", "Utilisateur introuvable"));          
    }
    
    @PostMapping("/login")
    public ResponseEntity<?> login(@RequestBody User user) {
        /* Au premier login, user est vérifié et le jwt token est envoyé via un http only cookie pour plus de sécurité.
        On envoie aussi l'objet UserAdditionalData pour déterminer si le profil est complété. */ 
        TwoCookies<CookieEssentials> twoCookies = userService.verify(user);
        
        User userInDatabase= userService.findByMail(user.getMail());
        UserAdditionalData additionalData = userInDatabase.getAdditionalData();

        return ResponseEntity.ok()
                .header(HttpHeaders.SET_COOKIE, twoCookies.accessCookie().cookie())
                .header(HttpHeaders.SET_COOKIE, twoCookies.refreshCookie().cookie())
                .body(Map.of("message", "Login réussi",
                             "accessExpiresIn", twoCookies.accessCookie().expiresIn(), 
                             "refreshExpiresIn", twoCookies.refreshCookie().expiresIn(), 
                             "additionalData", additionalData != null? additionalData : "null"
                ));
    }
    
    @GetMapping("/users")
    public Iterable<User> getUsers() {
        return userService.getAllUsers();
    }

    @GetMapping("/logout")
    public ResponseEntity<?> logout() {
        TwoCookies<ResponseCookie> twoCookies = userService.logout();
        return ResponseEntity.ok()
               .header(HttpHeaders.SET_COOKIE, twoCookies.accessCookie().toString())
               .header(HttpHeaders.SET_COOKIE, twoCookies.refreshCookie().toString())
               .body(Map.of("message", "Le logout est réussi"));
    }

    @GetMapping("/refresh")
    public ResponseEntity<?> refresh(HttpServletRequest request) {
        /* Si le refreshToken n'est pas expiré, envoie un nouvel accessToken */
        String refreshToken = requestUtils.getTokenFromRequest(request, "jwt-refresh");
        CookieEssentials accessCookie = userService.refresh(refreshToken);
        return ResponseEntity.ok()
                             .header(HttpHeaders.SET_COOKIE, accessCookie.cookie())
                             .body(Map.of("message", "Token rafraîchi",
                                          "accessExpiresIn", accessCookie.expiresIn()
                             ));
    }

    @GetMapping("/profile")
    public ResponseEntity<?> getUserInfos(HttpServletRequest request) {
        // User is found thanks to the access Cookie.
        String token = requestUtils.getTokenFromRequest(request, "jwt");
        User user = userService.findByToken(token);
        UserAdditionalData additionalData = user.getAdditionalData();
        List<Institution> institutions = user.getInstitutions();
        
        Map<String, Object> response = new HashMap<>();
        response.put("firstname", user.getFirstname());
        response.put("lastname", user.getLastname());
        response.put("mail", user.getUsername());
        
        if (additionalData != null){
            response.put("acceptContact", additionalData.isAcceptContact());
            response.put("acceptMap", additionalData.isAcceptMap());
            response.put("birthday", additionalData.getBirthday());
            response.put("gender", additionalData.getGender());
            response.put("job", additionalData.getJob());
            response.put("specializedTopics", additionalData.getSpecializedTopics());
            response.put("otherSpecialization", additionalData.getOtherSpecialization());
            response.put("teacherBehaviour", additionalData.getTeacherBehaviour());
            response.put("freeField", additionalData.getFreeField());
        } else {
            response.put("acceptContact", false);
            response.put("acceptMap", false);
            response.put("birthday", "");
            response.put("gender", "");
            response.put("job", "");
            response.put("specializedTopics", "");
            response.put("otherSpecialization", "");
            response.put("teacherBehaviour", "");
            response.put("freeField", "");
        }

        if (institutions != null){
            List<Map<String, Object>> institutionsList = new ArrayList<>();
            for (Institution institution : institutions) {
                institutionsList.add(Map.of(
                    "id", institution.getId(),
                    "name", institution.getName()
                ));
            }
            response.put("institutions", institutionsList);
        }
        
        return ResponseEntity.ok(response);
    }


    @DeleteMapping("/delete")
    public void delete(HttpServletRequest request) {
        String token = requestUtils.getTokenFromRequest(request, "jwt");
        User user = userService.findByToken(token);
        userService.delete(user);
    }

    @PutMapping("/update")
    public ResponseEntity<?> update(@RequestBody Map<String, Object> body, HttpServletRequest request) {
        String token = requestUtils.getTokenFromRequest(request, "jwt");
        User updatedUser = userService.findByToken(token);
        userService.update(body, updatedUser, encoder);
        return ResponseEntity.ok(updatedUser);
    }

    @PostMapping("/resetMail")
    public ResponseEntity<?> sendPwdRecoveryMail(@RequestBody Map<String, String> body) throws MessagingException {
        String mail = body.get("mail"); // mail = username in the eva app
        User user = userService.sendRecoveryMail(mail, "pwdChange");
        if (user == null){
            return ResponseEntity
                    .status(HttpStatus.BAD_REQUEST)
                    .body("Aucun compte n'est associé à ce courriel.");
        }
        return ResponseEntity.ok()
                             .body(Map.of("message", "Un courriel vous a été envoyé, veuillez consulter votre messagerie."));
    }

    @PostMapping("/recoverPwd")
    public ResponseEntity<?> registerNewPassword(@RequestBody Map<String, String> body) {
        /* Vérifie si le cookie du lien est toujours valide et change le mot de passe si c'est le cas */
        String token = body.get("token");
        String newPassword = body.get("password");
                
        if (newPassword == null || newPassword.length() < 8) {
            return ResponseEntity.status(HttpStatus.BAD_REQUEST)
                                .body(Map.of("message", "Le mot de passe ne peut pas être vide"));
        }
        
        if (token == null || token.isEmpty() || userService.isTokenExpired(token)){
            return ResponseEntity.status(HttpStatus.BAD_REQUEST)
                                .body(Map.of("message", "Le lien fourni a expiré, relancer la procédure"));
        }

        User user = userService.findByToken(token);
        if (user != null){
            user.setPassword(encoder.encode(newPassword));
            userService.saveUpdatedUser(user);
            return ResponseEntity.ok(Map.of("message", "Le mot de passe a été modifié"));
        }    

        return ResponseEntity.status(HttpStatus.BAD_REQUEST)
                             .body(Map.of("message", "Utilisateur introuvable"));          
    }
}
