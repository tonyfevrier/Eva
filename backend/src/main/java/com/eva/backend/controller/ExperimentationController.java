package com.eva.backend.controller;

import java.util.Map;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.ResponseEntity;
import org.springframework.security.core.annotation.AuthenticationPrincipal;
import org.springframework.web.bind.annotation.RestController;

import com.eva.backend.model.Experimentation;
import com.eva.backend.model.User;
import com.eva.backend.service.ExperimentationService;
import com.eva.backend.service.PedagogicalContextService;

import org.springframework.web.bind.annotation.DeleteMapping;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.PutMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;


@RestController
@RequestMapping("/expe")
public class ExperimentationController {
    
    @Autowired
    private ExperimentationService experimentationService;

    @Autowired 
    private PedagogicalContextService contextService;

    @PostMapping("/create")    
    public ResponseEntity<?> createExperimentation(@RequestBody Experimentation experimentation, @AuthenticationPrincipal User user){
        experimentation.setUser(user); // le jwt filter extrait du cookie le User actuel. Il ne reste qu'à l'associer
        experimentationService.save(experimentation);
        return  ResponseEntity.ok(Map.of("message", "L'expérimentation a bien été enregistrée"));
    }

    
    @DeleteMapping("/delete/{id}") 
    public void deleteExperimentation(){

    }

    
    @PutMapping("/update/{id}")
    public void updateExperimentation() {        
    }

    @GetMapping("/get/{id}")
    public void getExperimentation() {
    }

    @GetMapping("/getAll")
    public void getExperimentationList() {
    }
    
}
