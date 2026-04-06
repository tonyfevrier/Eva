package com.eva.backend.service;

import java.util.Map;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import com.eva.backend.model.Experimentation;
import com.eva.backend.model.User;
import com.eva.backend.repository.ExperimentationRepository;


@Service
public class DataExtractionService {
    /* Extraction des données de l'expérimentation qui seront écrites dans le pdf final */
    @Autowired
    private ExperimentationRepository experimentationRepository;

    public Map<String, Object> serialize(Long id){
        Experimentation experimentation = experimentationRepository.findById(id).orElseThrow();
        User user = experimentation.getUser();
        return Map.of(
            "keywords", experimentation.getKeywords(),
            "personalKeywords", experimentation.getPersonalKeywords() != null ? experimentation.getPersonalKeywords() : "",
            "protocol", experimentation.getProtocol(),
            "affiliation", experimentation.getInstitution().getName(),
            "pedagogicalContext", experimentation.getPedagogicalContext(),
            "contactMail", user.getAdditionalData().isAcceptContact()?user.getMail():""
        );
    }
}
