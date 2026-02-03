package com.eva.backend.service;

import java.util.Optional;

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

    public Optional<Experimentation> findLast(){
        return experimentationRepository.findTopByOrderByIdDesc();
    }

    public Optional<Experimentation> findById(Long id){
        return experimentationRepository.findById(id);
    }

}
