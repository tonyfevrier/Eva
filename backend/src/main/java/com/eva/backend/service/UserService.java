package com.eva.backend.service;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.security.authentication.AuthenticationManager;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.core.Authentication;
import org.springframework.stereotype.Service;

import com.eva.backend.model.User;
import com.eva.backend.repository.UserRepository;

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

    public String verify(User user){
        Authentication authentication = authManager.authenticate(
            new UsernamePasswordAuthenticationToken(user.getUsername(), user.getPassword()));
        System.err.println(authentication.isAuthenticated());
        if (authentication.isAuthenticated()){
            return jwtService.generateCookie(user.getUsername()).toString();
        } 
        return "";
    }

    public Iterable<User> getAllUsers(){
        return userRepository.findAll();
    }
}
