package com.eva.backend.controller;

import java.util.ArrayList;
import java.util.Map;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;
import org.springframework.http.ResponseEntity;

import com.eva.backend.model.Institution;
import com.eva.backend.model.User;
import com.eva.backend.repository.InstitutionRepository;
import com.eva.backend.service.UserService;

import jakarta.servlet.http.HttpServletRequest;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.PutMapping;
import org.springframework.web.bind.annotation.PathVariable;



@RestController
@RequestMapping("/institution")
public class InstitutionController {

    @Autowired 
    private UserService userService;

    @Autowired
    private InstitutionRepository institutionRepository;

    @Autowired
    private RequestUtils requestUtils;

    @PostMapping("/create")
    public ResponseEntity<?> registerInstitutionAssociateToUser(@RequestBody Institution institution, HttpServletRequest request){
        String token = requestUtils.getTokenFromRequest(request, "jwt");
        User user = userService.findByToken(token);

        if (user.getInstitutions() == null) {
            user.setInstitutions(new ArrayList<>());
        }

        //L'institution envoyée par react ne contient pas de liste pour l'attribut users
        institution.setUsers(new ArrayList<>());
        institution.getUsers().add(user);
        Institution savedInstitution = institutionRepository.save(institution);
        
        user.getInstitutions().add(savedInstitution);        
        userService.saveUpdatedUser(user);
        
        return ResponseEntity.ok(savedInstitution);
    }

    @GetMapping("/get/{id}")
    public String getInstitution(@RequestParam String param) {
        return new String();
    }

    @PutMapping("/update/{id}")
    public String updateInstitution(@PathVariable String id, @RequestBody String entity) {
        
        return entity;
    }
    
 
}
