package com.eva.backend.repository;

import org.springframework.stereotype.Repository;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;

import com.eva.backend.model.Institution;

@Repository
public interface InstitutionRepository extends JpaRepository<Institution, Long> {
    
    @Query("SELECT i FROM Institution i LEFT JOIN FETCH i.experimentations WHERE i.contactMail = :contactMail")
    public Institution findByContactMailWithExperimentations(@Param("contactMail") String contactMail);
}
