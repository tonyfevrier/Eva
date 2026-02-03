package com.eva.backend.repository;

import org.springframework.stereotype.Repository;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import com.eva.backend.model.User;

@Repository
public interface UserRepository extends JpaRepository<User, Long> {
    
    /* Méthode abstraite à partir de laquelle JpaRepository va 
     * générer automatiquement du sql en se basant sur le nom, ici
     * il va chercher l'attribut mail de User et écrit un SELECT
     */
    public User findByMail(String mail);
    
    @Query("SELECT u FROM User u LEFT JOIN FETCH u.experimentations WHERE u.mail = :mail")
    public User findByMailWithExperimentations(@Param("mail") String mail);
}
