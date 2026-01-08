package com.eva.backend.repository;

import org.springframework.data.jpa.repository.JpaRepository; 

import com.eva.backend.model.UserAdditionalData;


public interface UserAdditionalDataRepository extends JpaRepository<UserAdditionalData, Long>{
    
} 
