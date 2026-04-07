package com.eva.backend.service;

import java.util.Map;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import com.eva.backend.model.Experimentation;
import com.eva.backend.model.Institution;
import com.eva.backend.model.PedagogicalContext;
import com.eva.backend.model.User;
import com.eva.backend.repository.ExperimentationRepository;


@Service
public class DataExtractionService {
    /* Extraction des données de l'expérimentation qui seront écrites dans le pdf final */
    @Autowired
    private ExperimentationRepository experimentationRepository;

    public Map<String, Map<String, Object>> extractExperimentationData(Long id){
        Experimentation experimentation = experimentationRepository.findById(id).orElseThrow();
        User user = experimentation.getUser();
        Institution institution = experimentation.getInstitution();
        PedagogicalContext context = experimentation.getPedagogicalContext();
        return Map.of(
            "Contact", Map.of("affiliation", institution.getName(),
                                  "contactInstitution", institution.getContactMail(),
                                  "contactTeacher", user.getAdditionalData().isAcceptContact()?user.getMail():""),
            "Catégories", Map.of("keywords", experimentation.getKeywords(),
                                 "personalKeywords", experimentation.getPersonalKeywords() != null ? experimentation.getPersonalKeywords() : ""),
            "Contexte pédagogique", Map.ofEntries(
                                 Map.entry("protocol", experimentation.getProtocol()),
                                 Map.entry("learningDifficulty", context.getLearningDifficulty()),
                                 Map.entry("learningDifficultyOrigin", context.getLearningDifficultyOrigin()),
                                 Map.entry("studyField", context.getStudyField()),
                                 Map.entry("teachingTitle", context.getTeachingTitle()),
                                 Map.entry("knowledges", context.getKnowledges()),
                                 Map.entry("prerequisite", context.getPrerequisite()),
                                 Map.entry("organisationParticularities", context.getOrganisationParticularities()),
                                 Map.entry("classesFrequencies", context.getClassesFrequencies()),
                                 Map.entry("classesDates", context.getClassesDates()),
                                 Map.entry("yearOfStudy", context.getYearOfStudy()),
                                 Map.entry("studentsSpecificities", context.getStudentsSpecificities()),
                                 Map.entry("studentsNumber", context.getStudentsNumber()),
                                 Map.entry("oldPedagogy", context.getOldPedagogy()),
                                 Map.entry("newPedagogy", context.getNewPedagogy())),
            "Evaluations", Map.of("oldPedagogyEvaluations", context.getOldPedagogyEvaluations(),
                                      "newPedagogyEvaluations", context.getNewPedagogyEvaluations())
        );
    }
}
