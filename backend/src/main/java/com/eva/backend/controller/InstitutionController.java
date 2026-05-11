package com.eva.backend.controller;

import java.util.ArrayList;
import java.util.List;
import java.util.Map;
import java.util.Optional;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;
import org.springframework.http.ResponseEntity;
import org.springframework.security.core.annotation.AuthenticationPrincipal;

import com.eva.backend.model.Institution;
import com.eva.backend.model.User;
import com.eva.backend.service.InstitutionService;
import com.eva.backend.service.UserService;

import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PutMapping;
import org.springframework.web.bind.annotation.PathVariable;


@RestController
@RequestMapping("/institution")
public class InstitutionController {

    @Autowired 
    private UserService userService;

    @Autowired
    private InstitutionService institutionService;

    @PostMapping("/create")
    public ResponseEntity<?> registerInstitutionAssociateToUser(@RequestBody Institution institution, @AuthenticationPrincipal User authenticatedUser){
        User user = userService.findByMailWithInstitutions(authenticatedUser.getMail());
        mapInstitutionToUser(institution, user);  
        return ResponseEntity.ok(Map.of("message", "Institution créée"));
    }

    

    @PostMapping("/associate")
    public ResponseEntity<?> associateExistingInstitutionToUser(@RequestBody Map<String, Long> body, @AuthenticationPrincipal User authenticatedUser){
        // Recharger le user avec ses institutions pour éviter LazyInitializationException
        User user = userService.findByMailWithInstitutions(authenticatedUser.getMail());
        Institution institution = institutionService.findByIdWithUsers(body.get("affiliationId"));
        mapInstitutionToUser(institution, user);
        return ResponseEntity.ok(Map.of("message", "Institution ajoutée"));
    }

    private void mapInstitutionToUser(Institution institution, User user){
        if (institution.getUsers() == null){
            institution.setUsers(new ArrayList<>());
        }
        
        institution.getUsers().add(user);
        Institution savedInstitution = institutionService.save(institution);
        
        if (user.getInstitutions() == null) {
            user.setInstitutions(new ArrayList<>());
        }

        if (!user.getInstitutions().contains(savedInstitution)) {
            user.getInstitutions().add(savedInstitution);
            userService.saveUpdatedUser(user);
        }
    }

    @GetMapping("/get/{id}")
    public ResponseEntity<?> getInstitution(@PathVariable Long id) {
        Optional<Institution> optionalInstitution = institutionService.findById(id);
        if (!optionalInstitution.isEmpty()){
            Institution institution = optionalInstitution.get();
            return ResponseEntity.ok(Map.of("name", institution.getName(),
                                            "town", institution.getTown(),
                                            "contactMail", institution.getContactMail(),
                                            "category", institution.getCategory(),
                                            "studentsNumber", institution.getStudentsNumber(),
                                            "socialStatus", institution.getSocialStatus(),
                                            "institutionSpecifities", institution.getInstitutionSpecifities(),
                                            "studentsSpecificities", institution.getStudentsSpecificities(),
                                            "teachersSpecificities", institution.getTeachersSpecificities()));
        }
        return ResponseEntity.notFound().build();
    }

    @GetMapping("/getAll")
    public ResponseEntity<?> getAllInstitutions() {
        List<Institution> institutionList = institutionService.findAll();
        List<Map<String, Object>> institutionMapping = new ArrayList<>();
        for (Institution institution:institutionList){
            institutionMapping.add(Map.of("id", institution.getId(),
                                          "name", institution.getName()));
        }
        return ResponseEntity.ok(Map.of("institutions", institutionMapping));
    }

    @PutMapping("/update/{id}")
    public ResponseEntity<?> updateInstitution(@PathVariable Long id, @RequestBody Institution institution) {
        Optional<Institution> optionalInstitution = institutionService.findById(id);
        if (!optionalInstitution.isEmpty()){
            Institution institutionToUpdate = optionalInstitution.get();
            institutionService.update(institutionToUpdate, institution);
            return ResponseEntity.ok(Map.of("message","Institution bien mise à jour"));
        } else {
            return ResponseEntity.badRequest().body(Map.of("message", "Institution non trouvée"));
        }
    }
    
 
}
