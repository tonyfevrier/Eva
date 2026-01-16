package com.eva.backend.service;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import com.eva.backend.repository.PedagogicalContextRepository;

@Service
public class PedagogicalContextService {
    @Autowired
    private PedagogicalContextRepository contextRepository;
}
