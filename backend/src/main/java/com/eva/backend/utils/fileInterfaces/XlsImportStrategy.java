package com.eva.backend.utils.fileInterfaces;

import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.StandardCopyOption;
import java.time.LocalDate;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Component;
import org.springframework.web.multipart.MultipartFile;

import com.eva.backend.model.Experimentation;
import com.eva.backend.model.PedagogicalContext;
import com.eva.backend.model.User;
import com.eva.backend.repository.ExperimentationRepository;

@Component
public class XlsImportStrategy implements FileImportStrategy {
    @Value("${app.xls-data-dir}")
    private String importDir;

    @Autowired
    private ExperimentationRepository experimentationRepository;
    
     @Override 
    public boolean supports(String importType){
        return importType.equals("xls");
    }

    @Override
    public String getImportDir() { 
        return importDir; 
    }
    
    @Override
    public String createImportedFileName(Long id, String extension) {
        //Nom du fichier de la forme : Variante_Annee_institution_nom_prenom_discipline.format

        Experimentation experimentation = experimentationRepository.findById(id).orElseThrow();
        User user = experimentation.getUser();
        PedagogicalContext context = experimentation.getPedagogicalContext();

        String protocol = experimentation.getProtocol().split(":")[0];
        String date = LocalDate.now().toString();
        String institution = experimentation.getInstitution().getName();
        String lastName = user.getLastname();
        String firstName = user.getFirstname();
        String studyField = context.getStudyField(); 

        return protocol + "_" + date + "_" + institution + "_" + lastName + "_" + firstName + "_" + studyField + "." + extension;
    }

    @Override
    public void copy(MultipartFile file, Path filePath) throws IOException {
        Files.copy(file.getInputStream(), filePath, StandardCopyOption.REPLACE_EXISTING);
    }
}