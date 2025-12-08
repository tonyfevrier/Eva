package com.eva.backend.service;

import java.util.Date;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.security.authentication.AuthenticationManager;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.core.Authentication;
import org.springframework.stereotype.Service;
import org.springframework.http.ResponseCookie;

import com.eva.backend.model.User;
import com.eva.backend.repository.UserRepository;
import com.eva.backend.records.CookieEssentials;

@Service
public class UserService {
    
    @Autowired
    private UserRepository userRepository;

    @Autowired
    private AuthenticationManager authManager;

    @Autowired
    private JWTService jwtService; 

    public User saveUser(User user){
        User existingUser = userRepository.findByMail(user.getMail());
        if (existingUser != null){
            throw new IllegalArgumentException("Cet email est déjà associé à un compte");
        }
        return userRepository.save(user);
    }

    public CookieEssentials verify(User user){
        Authentication authentication = authManager.authenticate(
            new UsernamePasswordAuthenticationToken(user.getUsername(), user.getPassword()));
        if (authentication.isAuthenticated()){
            return generateCookie(user);
        } 
        return new CookieEssentials("", 0);
    }

    public ResponseCookie logout() {
        // cookie qui remplace le précédent
        return ResponseCookie.from("jwt", "")
                                .path("/")
                                .maxAge(0)
                                .build();
    }

    private CookieEssentials generateCookie(User user){
        String token = jwtService.generateToken(user.getUsername());
        ResponseCookie cookie = ResponseCookie.from("jwt", token)
                  .httpOnly(true) //empêche les attaques JS
                  .secure(true)   //https
                  .path("/")      //accessible pour tout
                  .maxAge(jwtService.tokenDurationInMilliSec) 
                  .sameSite("Strict") //protection csrf
                  .build();
        return new CookieEssentials(cookie.toString(),
                                    jwtService.tokenDurationInMilliSec);
    }

    public Iterable<User> getAllUsers(){
        return userRepository.findAll();
    }
}
