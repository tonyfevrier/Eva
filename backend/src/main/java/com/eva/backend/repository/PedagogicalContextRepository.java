package com.eva.backend.repository;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import com.eva.backend.model.PedagogicalContext;

@Repository
public interface PedagogicalContextRepository extends JpaRepository<PedagogicalContext, Long> {
    
}
