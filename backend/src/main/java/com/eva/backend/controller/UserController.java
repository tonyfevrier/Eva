package com.eva.backend.controller;


import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.ResponseEntity;
import org.springframework.security.crypto.bcrypt.BCryptPasswordEncoder;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RestController;

import com.eva.backend.model.User;
import com.eva.backend.service.UserService;

@RestController
@RequestMapping("/api")
public class UserController {
    @Autowired 
    private UserService userService;

    private BCryptPasswordEncoder encoder = new BCryptPasswordEncoder(12);

    @PostMapping("/register")
    public ResponseEntity<?> register(@RequestBody User user){
        try {
            System.out.println("User: " + user.getMail());
            user.setPassword(encoder.encode(user.getPassword()));
            User savedUser = userService.saveUser(user);
            System.out.println("User saved successfully: " + savedUser.getId());
            return ResponseEntity.ok(savedUser);
        } catch (Exception e) {
            System.err.println("Registration error: " + e.getMessage());
            e.printStackTrace();
            return ResponseEntity.badRequest().body("Registration failed: " + e.getMessage());
        }
    }  
    
    @PostMapping("/login")
    public String login(@RequestBody User user) {
        return userService.verify(user);
    }
    

}
