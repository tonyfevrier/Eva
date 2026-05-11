package com.eva.backend.repository;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import com.eva.backend.model.Interpretation;

@Repository
public interface InterpretationRepository extends JpaRepository<Interpretation, Long> {
    
}
