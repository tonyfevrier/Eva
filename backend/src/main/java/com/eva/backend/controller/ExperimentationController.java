package com.eva.backend.controller;

import java.util.List;
import java.util.Map;
import java.util.Optional;
import java.util.stream.Collectors;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.ResponseEntity;
import org.springframework.security.core.annotation.AuthenticationPrincipal;
import org.springframework.web.bind.annotation.RestController;

import com.eva.backend.model.Experimentation;
import com.eva.backend.model.Institution;
import com.eva.backend.model.User;
import com.eva.backend.records.ExperimentationRequest;
import com.eva.backend.service.ExperimentationService;
import com.eva.backend.service.InstitutionService;
import com.eva.backend.service.UserService;

import org.springframework.web.bind.annotation.DeleteMapping;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
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
    private InstitutionService institutionService;

    @Autowired
    private UserService userService;

    @PostMapping("/create") 
    //@Transactional //Important pour que ma ligne 42 permette aussi d'enregistrer l'expérimentation dans user pour maintenir la relation birectionnelle   
    public ResponseEntity<?> createExperimentation(@RequestBody ExperimentationRequest experimentationRequest, @AuthenticationPrincipal User authenticatedUser){
        Experimentation experimentation = experimentationRequest.experimentation();
        User user = userService.findByMail(authenticatedUser.getMail());
        experimentation.setUser(user); // le jwt filter extrait du cookie le User actuel. Il ne reste qu'à l'associer
       
        Optional<Institution> optionalInstitution = institutionService.findById(experimentationRequest.affiliationID());
        if (!optionalInstitution.isEmpty()){
            experimentation.setInstitution(optionalInstitution.get());            
        } 
        experimentationService.save(experimentation);
        Experimentation lastExperimentation = experimentationService.findLast().orElseThrow();
        return  ResponseEntity.ok(Map.of("message", "L'expérimentation a bien été enregistrée",
                                         "id", lastExperimentation.getId()));
    }

    @GetMapping("/get/{id}")
    public ResponseEntity<?> getExperimentation(@PathVariable Long id) {
        Optional<Experimentation> optionalExperimentation = experimentationService.findById(id);
        if (optionalExperimentation.isEmpty()) {
            return ResponseEntity.notFound().build();
        }
        
        Experimentation experimentation = optionalExperimentation.get();
        Institution institution = experimentation.getInstitution();
        Map<String, Object> response = Map.of(
            "id", experimentation.getId(),
            "keywords", experimentation.getKeywords(),
            "personalKeywords", experimentation.getPersonalKeywords() != null ? experimentation.getPersonalKeywords() : "",
            "protocol", experimentation.getProtocol(),
            "affiliation", Map.of("id", institution.getId(),
                                      "name", institution.getName()),
            "pedagogicalContext", experimentation.getPedagogicalContext(),
            "isSharingData", experimentation.getIsSharingData()
        );
        
        return ResponseEntity.ok(response);
    }

    @GetMapping("/getAll")
    public ResponseEntity<?> getExperimentationListOfOneUser(@AuthenticationPrincipal User authenticatedUser) {
        User user = userService.findByMailWithExperimentations(authenticatedUser.getMail());
        
        List<Map<String, Object>> experimentationsList = user.getExperimentations().stream()
            .map(expe -> Map.of(
                "id", (Object) expe.getId(),
                "keywords", expe.getKeywords(),
                "personalKeywords", expe.getPersonalKeywords() != null ? expe.getPersonalKeywords() : "",
                "institutionName", expe.getInstitution().getName(),
                "teachingTitle", expe.getPedagogicalContext().getTeachingTitle(),
                "studyField", expe.getPedagogicalContext().getStudyField(),
                "yearOfStudy", expe.getPedagogicalContext().getYearOfStudy()
            ))
            .collect(Collectors.toList());
        
        return ResponseEntity.ok(experimentationsList);
    }

    @DeleteMapping("/delete/{id}") 
    public void deleteExperimentation(){

    }

    @PutMapping("/update/{id}")
    public ResponseEntity<?> updateExperimentation(@PathVariable Long id, @RequestBody ExperimentationRequest experimentationRequest, @AuthenticationPrincipal User authenticatedUser) {
        // Vérifier que l'expérimentation existe
        Optional<Experimentation> optionalExperimentation = experimentationService.findById(id);
        if (optionalExperimentation.isEmpty()) {
            return ResponseEntity.notFound().build();
        }
        
        Experimentation existingExperimentation = optionalExperimentation.get();
        
        // Vérifier que l'utilisateur est bien le propriétaire de l'expérimentation
        if (!existingExperimentation.getUser().getMail().equals(authenticatedUser.getMail())) {
            return ResponseEntity.status(403).body(Map.of("message", "Vous n'êtes pas autorisé à modifier cette expérimentation"));
        }
        
        Experimentation updatedExperimentation = experimentationRequest.experimentation();
        existingExperimentation.setKeywords(updatedExperimentation.getKeywords());
        existingExperimentation.setPersonalKeywords(updatedExperimentation.getPersonalKeywords());
        existingExperimentation.setProtocol(updatedExperimentation.getProtocol());
        existingExperimentation.setPedagogicalContext(updatedExperimentation.getPedagogicalContext());
        existingExperimentation.setIsSharingData(updatedExperimentation.getIsSharingData());
        
        // Mettre à jour l'institution si nécessaire
        Optional<Institution> optionalInstitution = institutionService.findById(experimentationRequest.affiliationID());
        if (!optionalInstitution.isEmpty()) {
            existingExperimentation.setInstitution(optionalInstitution.get());
        }
        
        experimentationService.save(existingExperimentation);
        
        return ResponseEntity.ok(Map.of("message", "L'expérimentation a bien été mise à jour"));
    }
}
