package com.eva.backend.service;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.security.authentication.AuthenticationManager;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.core.Authentication;
import org.springframework.stereotype.Service;
import org.springframework.http.ResponseCookie;

import com.eva.backend.model.User;
import com.eva.backend.repository.UserRepository;
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

    /*public CookieEssentials refresh(User user){
        return;*/

    public Iterable<User> getAllUsers(){
        return userRepository.findAll();
    }
}
