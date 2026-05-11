package com.eva.backend.service;

import java.util.Map;
import java.util.LinkedHashMap;
import java.util.List;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import com.eva.backend.model.Experimentation;
import com.eva.backend.model.Institution;
import com.eva.backend.model.Interpretation;
import com.eva.backend.model.PedagogicalContext;
import com.eva.backend.model.User;
import com.eva.backend.model.Evaluations;
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
        
        Map<String, Map<String, Object>> data = new LinkedHashMap<>();
        
        // Contact
        Map<String, Object> contact = new LinkedHashMap<>();
        contact.put("Affiliation", institution.getName());
        contact.put("Institution", institution.getContactMail());
        contact.put("Contact enseignant", user.getAdditionalData().isAcceptContact() ? user.getMail() : "");
        data.put("Contact", contact);
        
        // Catégories
        Map<String, Object> categories = new LinkedHashMap<>();
        categories.put("Mots-clés", String.join(", ",experimentation.getKeywords()));
        categories.put("Mots-clés personnels", experimentation.getPersonalKeywords() != null ? experimentation.getPersonalKeywords() : "");
        data.put("Catégories", categories);
        
        // Contexte pédagogique
        Map<String, Object> pedagogicalContext = new LinkedHashMap<>();
        pedagogicalContext.put("Protocole", experimentation.getProtocol());
        pedagogicalContext.put("Difficulté d'apprentissage", context.getLearningDifficulty());
        pedagogicalContext.put("Origine de la difficulté", context.getLearningDifficultyOrigin());
        pedagogicalContext.put("Domaine d'étude", context.getStudyField());
        pedagogicalContext.put("Titre de l'enseignement", context.getTeachingTitle());
        pedagogicalContext.put("Connaissances", context.getKnowledges());
        pedagogicalContext.put("Prérequis", context.getPrerequisite());
        pedagogicalContext.put("Particularités organisationnelles", context.getOrganisationParticularities());
        pedagogicalContext.put("Fréquence des cours", context.getClassesFrequencies());
        pedagogicalContext.put("Dates des cours", context.getClassesDates());
        pedagogicalContext.put("Année d'étude", context.getYearOfStudy());
        pedagogicalContext.put("Spécificités des étudiants", context.getStudentsSpecificities());
        pedagogicalContext.put("Nombre d'étudiants", context.getStudentsNumber());
        pedagogicalContext.put("Ancienne pédagogie", context.getOldPedagogy());
        pedagogicalContext.put("Nouvelle pédagogie", context.getNewPedagogy());
        data.put("Contexte pédagogique", pedagogicalContext);
        
        // Evaluations
        Map<String, Object> evaluations = new LinkedHashMap<>();
        evaluations.put("Evaluations pour l'ancienne pédagogie", convertEvaluationsToMap(context.getOldPedagogyEvaluations()));
        evaluations.put("Evaluations pour la nouvelle pédagogie", convertEvaluationsToMap(context.getNewPedagogyEvaluations()));
        data.put("Evaluations", evaluations);
        
        return data;
    }
    
    private Map<String, Object> convertEvaluationsToMap(Evaluations evaluations) {
        Map<String, Object> evalMap = new LinkedHashMap<>();
        if (evaluations != null) {
            evalMap.put("Evaluation initiale", evaluations.getInitialEvaluation());
            evalMap.put("Evaluation immédiate", evaluations.getImmediateEvaluation());
            evalMap.put("Evaluation différée", evaluations.getDelayedEvaluation());
            if (evaluations.getAccountedEvaluation() != null){
                evalMap.put("Evaluation comptabilisée", evaluations.getAccountedEvaluation());
            }
        }
        return evalMap;
    }

     public Map<String, Object> extractInterpretationsData(Long id){
        Experimentation experimentation = experimentationRepository.findByIdWithInterpretations(id);
        Map<String, Object> data = new LinkedHashMap<>();
        List<Interpretation> interpretations = experimentation.getInterpretations();
        for (Interpretation interpretation : interpretations){
            Map<String, Object> interpretationData = extractInterpretationData(interpretation);
            data.put(interpretation.getId().toString(), interpretationData);
        }
        return data;
    }

    private Map<String, Object> extractInterpretationData(Interpretation interpretation){
        Map<String, Object> interpretationData = new LinkedHashMap<>();
        interpretationData.put("content", interpretation.getContent());
        User user = interpretation.getUser();
        String name = user.getFirstname() + " " + user.getLastname();
        interpretationData.put("name", name);
        return interpretationData;
    }
}
