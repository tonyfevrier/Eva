package com.eva.backend.controller;

import java.time.LocalDate;
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

import jakarta.servlet.http.HttpServletRequest;

import org.springframework.web.bind.annotation.DeleteMapping;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.PutMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;



@RestController
@RequestMapping("/expe")
public class ExperimentationController {
    
    @Autowired
    private ExperimentationService experimentationService;

    @Autowired
    private InstitutionService institutionService;

    @Autowired
    private UserService userService;

    @Autowired
    private RequestUtils requestUtils;

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
    public ResponseEntity<?> getExperimentation(@PathVariable Long id, HttpServletRequest request) {
        Optional<Experimentation> optionalExperimentation = experimentationService.findById(id);
        if (optionalExperimentation.isEmpty()) {
            return ResponseEntity.notFound().build();
        }
        
        Experimentation experimentation = optionalExperimentation.get();

        // Lit le token de manière sécurisée: retourne null si invalide, sans lever d'exception
        String token = requestUtils.getTokenFromRequest(request, "jwt");
        User authenticatedUser = userService.findByTokenSafely(token);
        User user = experimentation.getUser();
        Boolean userOwnsExpe = authenticatedUser != null? authenticatedUser.getId().equals(user.getId()):false;

        Institution institution = experimentation.getInstitution();
        Map<String, Object> response = Map.of(
            "id", experimentation.getId(),
            "keywords", experimentation.getKeywords(),
            "personalKeywords", experimentation.getPersonalKeywords() != null ? experimentation.getPersonalKeywords() : "",
            "protocol", experimentation.getProtocol(),
            "affiliation", Map.of("id", institution.getId(),
                                      "name", institution.getName()),
            "pedagogicalContext", experimentation.getPedagogicalContext(),
            "inProgress", experimentation.getInProgress(),
            "isSharingData", experimentation.getIsSharingData(),
            "userOwnsExpe", userOwnsExpe,
            "contactMail", user.getAdditionalData().isAcceptContact()?user.getMail():""
        );
        
        return ResponseEntity.ok(response);
    }

    @GetMapping("/getAllOfOneUser")
    public ResponseEntity<?> getExperimentationListOfOneUser(@AuthenticationPrincipal User authenticatedUser) {
        User user = userService.findByMailWithExperimentations(authenticatedUser.getMail());

        List<Map<String, Object>> experimentationsList = user.getExperimentations().stream()
            .sorted((e1, e2) -> e2.getId().compareTo(e1.getId()))
            .map(expe -> {
                return Map.of(
                    "id", (Object) expe.getId(),
                    "keywords", expe.getKeywords(),
                    "personalKeywords", expe.getPersonalKeywords() != null ? expe.getPersonalKeywords() : "",
                    "institutionName", expe.getInstitution().getName(),
                    "teachingTitle", expe.getPedagogicalContext().getTeachingTitle(),
                    "studyField", expe.getPedagogicalContext().getStudyField(),
                    "yearOfStudy", expe.getPedagogicalContext().getYearOfStudy(),
                    "inProgress", expe.getInProgress()
                );
            })
            .collect(Collectors.toList());
        
        return ResponseEntity.ok(experimentationsList);
    }

    @GetMapping("/getAll")
    public ResponseEntity<?> getExperimentationList() {
        List<Map<String, Object>> experimentationsList = experimentationService.findExperimentations().stream()
            .sorted((e1, e2) -> e2.getId().compareTo(e1.getId()))
            .map(expe -> {
                return Map.of(
                    "id", (Object) expe.getId(),
                    "keywords", expe.getKeywords(),
                    "personalKeywords", expe.getPersonalKeywords() != null ? expe.getPersonalKeywords() : "",
                    "institutionName", expe.getInstitution().getName(),
                    "teachingTitle", expe.getPedagogicalContext().getTeachingTitle(),
                    "studyField", expe.getPedagogicalContext().getStudyField(),
                    "yearOfStudy", expe.getPedagogicalContext().getYearOfStudy(),
                    "inProgress", expe.getInProgress()
                );
            })
            .collect(Collectors.toList());
        
        return ResponseEntity.ok(experimentationsList);
    }

    @DeleteMapping("/delete/{id}") 
    public ResponseEntity<?> deleteExperimentation(@PathVariable Long id, @AuthenticationPrincipal User authenticatedUser){
        // Vérifier que l'expérimentation existe
        Optional<Experimentation> optionalExperimentation = experimentationService.findById(id);
        if (optionalExperimentation.isEmpty()) {
            return ResponseEntity.notFound().build();
        }
        
        Experimentation experimentation = optionalExperimentation.get();
        
        // Vérifier que l'utilisateur est bien le propriétaire de l'expérimentation
        if (!experimentation.getUser().getMail().equals(authenticatedUser.getMail())) {
            return ResponseEntity.status(403).body(Map.of("message", "Vous n'êtes pas autorisé à supprimer cette expérimentation"));
        }
        
        experimentationService.deleteById(id);
        
        return ResponseEntity.ok(Map.of("message", "L'expérimentation a bien été supprimée"));
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

    @PostMapping("/interpret/{id}")
    private ResponseEntity<?> addInterpretation(@PathVariable Long id, @RequestBody Map<String, String> body){
        Experimentation experimentation = experimentationService.findById(id).orElseThrow();
        experimentation.setInterpretation(body.get("interpretation"));
        experimentationService.save(experimentation);
        return ResponseEntity.ok("L'interprétation a bien été sauvegardée");
    }

    @GetMapping("/endExpe/{id}")
    public ResponseEntity<?> endExperimentation(@PathVariable Long id){
        Experimentation experimentation = experimentationService.findById(id).orElseThrow();
        experimentation.setInProgress(false);
        experimentationService.save(experimentation);
        return ResponseEntity.ok("L'expérimentation est bien marquée comme terminée");
    }
    
}
