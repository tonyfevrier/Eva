package com.eva.backend.controller;

import java.util.Map;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.ResponseEntity;
import org.springframework.http.HttpHeaders;
import org.springframework.http.ResponseCookie;
import org.springframework.security.crypto.bcrypt.BCryptPasswordEncoder;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RestController;

import com.eva.backend.model.User;
import com.eva.backend.service.UserService;
import com.eva.backend.records.CookieEssentials;
import com.eva.backend.records.TwoCookies;

import jakarta.servlet.http.Cookie;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.validation.Valid;

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
        String refreshToken = getRefreshTokenFromRequest(request);
        CookieEssentials accessCookie = userService.refresh(refreshToken);
        return ResponseEntity.ok()
                             .header(HttpHeaders.SET_COOKIE, accessCookie.cookie())
                             .body(Map.of("message", "Token rafraîchi",
                                          "accessExpiresIn", accessCookie.expiresIn()
                             ));
    }

    private String getRefreshTokenFromRequest(HttpServletRequest request){
        if (request.getCookies() != null){
            for (Cookie cookie : request.getCookies()) {
                if ("jwt-refresh".equals(cookie.getName())) {
                    return cookie.getValue();
                }
            }
        }
        return "";
    }
    
}
