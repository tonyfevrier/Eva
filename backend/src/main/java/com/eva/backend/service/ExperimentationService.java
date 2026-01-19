package com.eva.backend.service;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import com.eva.backend.model.Experimentation;
import com.eva.backend.repository.ExperimentationRepository;

@Service
public class ExperimentationService {
    @Autowired
    private ExperimentationRepository experimentationRepository;

    public void save(Experimentation experimentation){
        experimentationRepository.save(experimentation);
    }

}
