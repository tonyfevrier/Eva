package com.eva.backend.controller;

import java.util.Map;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.ResponseEntity;
import org.springframework.security.crypto.bcrypt.BCryptPasswordEncoder;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RestController;

import com.eva.backend.model.User;
import com.eva.backend.service.UserService;

import jakarta.validation.Valid;

import org.springframework.web.bind.annotation.GetMapping;


@RestController
@RequestMapping("/api")
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
        // Faudra surement retourner ResponseEntity pour renvoyer un json
        String generatedToken = userService.verify(user);
        return ResponseEntity.ok(Map.of(
            "token", generatedToken,
            "type", "Bearer",
            "expiresIn", 3600000 // 1 heure en ms
        ));
    }
    
    @GetMapping("/users")
    public Iterable<User> getUsers() {
        return userService.getAllUsers();
    }
    

}
