package com.eva.backend.service;

import java.net.http.HttpHeaders;
import java.util.Map;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.security.authentication.AuthenticationManager;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.core.Authentication;
import org.springframework.stereotype.Service;
import org.springframework.http.ResponseCookie;
import org.springframework.http.ResponseEntity;

import com.eva.backend.model.User;
import com.eva.backend.repository.UserRepository;

import jakarta.servlet.http.HttpServletRequest;

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

    public User saveUser(User user){
        User existingUser = userRepository.findByMail(user.getMail());
        if (existingUser != null){
            throw new IllegalArgumentException("Cet email est déjà associé à un compte");
        }
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
}
