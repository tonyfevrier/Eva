package com.eva.backend.repository;

import org.springframework.stereotype.Repository;
import org.springframework.data.jpa.repository.JpaRepository;

import com.eva.backend.model.Institution;

@Repository
public interface InstitutionRepository extends JpaRepository<Institution, Long> {
    
}
