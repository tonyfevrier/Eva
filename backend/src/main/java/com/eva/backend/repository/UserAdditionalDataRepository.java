package com.eva.backend.repository;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import com.eva.backend.model.UserAdditionalData;

@Repository
public interface UserAdditionalDataRepository extends JpaRepository<UserAdditionalData, Long>{
    
} 
