package com.eva.backend.repository;

import java.util.Optional;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import com.eva.backend.model.Experimentation;


@Repository
public interface ExperimentationRepository extends JpaRepository<Experimentation, Long> {
    Optional<Experimentation> findTopByOrderByIdDesc();
    
}
 
