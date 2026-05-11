package com.eva.backend.repository;

import java.util.Optional;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;

import com.eva.backend.model.Experimentation;


@Repository
public interface ExperimentationRepository extends JpaRepository<Experimentation, Long> {
    Optional<Experimentation> findTopByOrderByIdDesc();
    
    @Query("SELECT e FROM Experimentation e LEFT JOIN FETCH e.interpretations WHERE e.id = :id")
    public Experimentation findByIdWithInterpretations(@Param("id") Long id);
}
 
