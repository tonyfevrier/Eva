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

import com.eva.backend.model.Institution;
import com.eva.backend.model.User;
import com.eva.backend.service.InstitutionService;
import com.eva.backend.service.UserService;

import jakarta.servlet.http.HttpServletRequest;

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

    @Autowired
    private RequestUtils requestUtils;

    InstitutionController(InstitutionService institutionService) {
        this.institutionService = institutionService;
    }

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
        Institution savedInstitution = institutionService.save(institution);
        
        user.getInstitutions().add(savedInstitution);        
        userService.saveUpdatedUser(user);
        
        return ResponseEntity.ok(Map.of("message", "Institution créée"));
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
        List<Map<Long, String>> institutionMapping = new ArrayList<>();
        for (Institution institution:institutionList){
            institutionMapping.add(Map.of(institution.getId(), institution.getName()));
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
