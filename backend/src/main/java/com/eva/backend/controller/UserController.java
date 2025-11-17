package com.eva.backend.controller;


import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.security.crypto.bcrypt.BCryptPasswordEncoder;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RestController;

import com.eva.backend.model.User;
import com.eva.backend.service.UserService;

@RestController
public class UserController {
    @Autowired 
    private UserService userService;

    private BCryptPasswordEncoder encoder = new BCryptPasswordEncoder(12);

    @PostMapping("/register")
    public void register(@RequestBody User user){
        user.setPassword(encoder.encode(user.getPassword()));
        userService.saveUser(user); 
    }   

}
