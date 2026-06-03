package com.eva.backend.controller;

import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.util.List;
import java.util.Map;
import java.util.Optional;
import java.util.stream.Collectors;
import java.util.stream.Stream;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.http.ResponseEntity;
import org.springframework.security.core.annotation.AuthenticationPrincipal;
import org.springframework.web.bind.annotation.RestController;

import com.eva.backend.model.Experimentation;
import com.eva.backend.model.Institution;
import com.eva.backend.model.Interpretation;
import com.eva.backend.model.User;
import com.eva.backend.records.AddInterpretationRequest;
import com.eva.backend.records.ExperimentationRequest;
import com.eva.backend.service.ExperimentationService;
import com.eva.backend.service.FileService;
import com.eva.backend.service.InstitutionService;
import com.eva.backend.service.UserService;
import com.eva.backend.service.InterpretationService;

import jakarta.servlet.http.HttpServletRequest;

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

    @Value("${app.generated-pdf-dir}")
    private String generatedPdfDir;

    @Value("${app.pdf-dir}")
    private String pdfDir;

    @Value("${app.xls-data-dir}")
    private String xlsDataDir;
    
    @Autowired
    private ExperimentationService experimentationService;

    @Autowired
    private InstitutionService institutionService;

    @Autowired
    private UserService userService;

    @Autowired
    private InterpretationService interpretationService;

    @Autowired
    private RequestUtils requestUtils;

    @Autowired
    private FileService fileService;

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
            .filter(expe -> expe.getIsSharingData())
            .map(expe -> {
                String expeWorked = "En attente";
                if (expe.getExpeWorked() != null){
                    expeWorked = expe.getExpeWorked()? "Oui": "Non";
                };
                return Map.of(
                    "id", (Object) expe.getId(),
                    "keywords", expe.getKeywords(),
                    "personalKeywords", expe.getPersonalKeywords() != null ? expe.getPersonalKeywords() : "",
                    "institutionName", expe.getInstitution().getName(),
                    "teachingTitle", expe.getPedagogicalContext().getTeachingTitle(),
                    "studyField", expe.getPedagogicalContext().getStudyField(),
                    "yearOfStudy", expe.getPedagogicalContext().getYearOfStudy(),
                    "inProgress", expe.getInProgress(),
                    "newPedagogy", expe.getPedagogicalContext().getNewPedagogy(),
                    "expeWorked", expeWorked
                );
            })
            .collect(Collectors.toList());
        
        return ResponseEntity.ok(experimentationsList);
    }

    @DeleteMapping("/delete/{id}") 
    public ResponseEntity<?> deleteExperimentation(@PathVariable Long id, @AuthenticationPrincipal User authenticatedUser) throws IOException {
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
        deleteFilesRelatedToExpe(id);
        
        return ResponseEntity.ok(Map.of("message", "L'expérimentation a bien été supprimée"));
    }

    private void deleteFilesRelatedToExpe(Long id) throws IOException {
        fileService.deleteExistingDataFile(Paths.get(xlsDataDir).toAbsolutePath().normalize(), id);
        fileService.deleteGeneratedExperimentationFile(Paths.get(generatedPdfDir).toAbsolutePath().normalize(), id);
        Path pathPdfDir = Paths.get(pdfDir).toAbsolutePath().normalize();
        try (Stream<Path> files = Files.list(pathPdfDir)){
            List<String> fileNames = files
                                   .filter(Files::isRegularFile)
                                   .map(path -> path.getFileName().toString())
                                   .filter(name -> name.contains("_id" + id))
                                   .toList();
            fileService.deletePdfFiles(pathPdfDir, fileNames);
        }
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
    private ResponseEntity<?> addInterpretation(@RequestBody AddInterpretationRequest request,  @PathVariable Long id){
        /*récupérer les interprétations et y ajouter le nouvel objet, on a le User dans l'expé*/
        Experimentation experimentation = experimentationService.findByIdWithInterpretations(id);
        User user = experimentation.getUser();
        List<Interpretation> interpretations = experimentation.getInterpretations();
        Interpretation interpretation = request.interpretation();
        interpretation.setUser(user);
        interpretation.setExperimentation(experimentation);
        interpretationService.save(interpretation);
        interpretations.add(interpretation);
        experimentation.setInterpretations(interpretations);
        experimentation.setExpeWorked(request.expeWorked());
        experimentationService.save(experimentation);
        return ResponseEntity.ok("L'interprétation a bien été sauvegardée");
    }

    @GetMapping("/endExpe/{id}")
    public ResponseEntity<?> endExperimentation(@PathVariable Long id){
        Experimentation experimentation = experimentationService.findByIdWithInterpretations(id);        
        boolean expeHasGeneratedPdf = experimentation.getDataPath() != null;
        boolean userHasInterpretedData = experimentation.getInterpretations().size() != 0;
        boolean userSaidIfExpeWorked = experimentation.getExpeWorked() != null;
        
        if (!expeHasGeneratedPdf || !userHasInterpretedData || !userSaidIfExpeWorked){
            return ResponseEntity.badRequest().body("Requête refusée : assurez-vous d'avoir soumis vos résultats et généré le pdf final avant de terminer l'expérimentation");
        }
        experimentation.setInProgress(false);
        experimentationService.save(experimentation);
        return ResponseEntity.ok("L'expérimentation est bien marquée comme terminée");
    }
    
}
