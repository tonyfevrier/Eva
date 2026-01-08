package com.eva.backend.controller;

import java.util.Map;

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

import com.eva.backend.model.User;
import com.eva.backend.service.UserService;
import com.eva.backend.records.CookieEssentials;
import com.eva.backend.records.TwoCookies;

import jakarta.mail.MessagingException;
import jakarta.servlet.http.Cookie;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.validation.Valid;

import org.springframework.web.bind.annotation.DeleteMapping;
import org.springframework.web.bind.annotation.GetMapping;



@RestController
@RequestMapping("/auth")
public class UserController {
    @Autowired 
    private UserService userService;

    private BCryptPasswordEncoder encoder = new BCryptPasswordEncoder(12);

    // @Valid valide les contraintes de forme des inputs (mail, pwd) avant d'entrer dans la méthode.
    // Permet de le faire sur le mot de passe original et pas hashé.
    @PostMapping("/register")
    public ResponseEntity<?> register(@Valid @RequestBody User user){
        System.out.println("User: " + user.getMail());
        user.setPassword(encoder.encode(user.getPassword()));
        User savedUser = userService.saveUser(user);
        System.out.println("User saved successfully: " + savedUser.getId());
        return ResponseEntity.ok(savedUser);
    }  
    
    @PostMapping("/login")
    public ResponseEntity<?> login(@RequestBody User user) {
        // Au premier login, user est vérifié et le jwt token est envoyé via un http only cookie pour plus de sécurité        
        TwoCookies<CookieEssentials> twoCookies = userService.verify(user);
 
        return ResponseEntity.ok()
                .header(HttpHeaders.SET_COOKIE, twoCookies.accessCookie().cookie())
                .header(HttpHeaders.SET_COOKIE, twoCookies.refreshCookie().cookie())
                .body(Map.of("message", "Login réussi",
                             "accessExpiresIn", twoCookies.accessCookie().expiresIn(), 
                             "refreshExpiresIn", twoCookies.refreshCookie().expiresIn() 
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
        String refreshToken = getTokenFromRequest(request, "jwt-refresh");
        CookieEssentials accessCookie = userService.refresh(refreshToken);
        return ResponseEntity.ok()
                             .header(HttpHeaders.SET_COOKIE, accessCookie.cookie())
                             .body(Map.of("message", "Token rafraîchi",
                                          "accessExpiresIn", accessCookie.expiresIn()
                             ));
    }

    private String getTokenFromRequest(HttpServletRequest request, String tokenName){
        if (request.getCookies() != null){
            for (Cookie cookie : request.getCookies()) {
                if (tokenName.equals(cookie.getName())) {
                    return cookie.getValue();
                }
            }
        }
        return "";
    }

    @GetMapping("/profile")
    public ResponseEntity<?> getUserInfos(HttpServletRequest request) {
        // User is found thanks to the access Cookie.
        String token = getTokenFromRequest(request, "jwt");
        User user = userService.findByToken(token);
        return ResponseEntity.ok(Map.of("firstname", user.getFirstname(),
                                        "lastname", user.getLastname(),
                                        "mail", user.getUsername()));
        
    }

    @DeleteMapping("/delete")
    public void delete(HttpServletRequest request) {
        String token = getTokenFromRequest(request, "jwt");
        User user = userService.findByToken(token);
        userService.delete(user);
    }

    @PutMapping("/update")
    public ResponseEntity<?> update(@RequestBody User newUser, HttpServletRequest request) {
        String token = getTokenFromRequest(request, "jwt");
        User updatedUser = userService.findByToken(token);
        updateUserInfos(newUser, updatedUser);
        return ResponseEntity.ok(updatedUser);
    }

    private void updateUserInfos(User newUser, User userToUpdate){
        String firstname = newUser.getFirstname();
        if (firstname != null){
            userToUpdate.setFirstname(firstname);
        }

        String lastname = newUser.getLastname();
        if (lastname != null){
            userToUpdate.setLastname(lastname);
        }

        String password = newUser.getPassword();
        if (password != null && password.length() >= 8){
            userToUpdate.setPassword(encoder.encode(password));
        }

        userService.saveUpdatedUser(userToUpdate);
    }

    @PostMapping("/resetMail")
    public ResponseEntity<?> sendPwdRecoveryMail(@RequestBody Map<String, String> body) throws MessagingException {
        String mail = body.get("mail"); // mail = username in the eva app
        User user = userService.sendRecoveryMail(mail);
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
