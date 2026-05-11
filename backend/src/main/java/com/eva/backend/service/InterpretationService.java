package com.eva.backend.service;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import com.eva.backend.model.Interpretation;
import com.eva.backend.repository.InterpretationRepository;

@Service
public class InterpretationService {
    @Autowired
    private InterpretationRepository interpretationRepository;

    public Interpretation save(Interpretation interpretation){
        return interpretationRepository.save(interpretation);
    }
}
