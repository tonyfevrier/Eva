package com.eva.backend.model;

import java.time.LocalDate;

import com.fasterxml.jackson.annotation.JsonIgnore;

import jakarta.persistence.Column;
import jakarta.persistence.Entity;
import jakarta.persistence.Id;
import jakarta.persistence.JoinColumn;
import jakarta.persistence.MapsId;
import jakarta.persistence.OneToOne;
import jakarta.persistence.Table;
import jakarta.validation.constraints.Past;
import jakarta.validation.constraints.Size;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;
import lombok.ToString;
import lombok.EqualsAndHashCode;

@Data
@AllArgsConstructor
@NoArgsConstructor
@Table(name = "usersAdditional")
@Entity
public class UserAdditionalData {
    
    // id qui sera celui du user associé grâce à MapsId
    @Id
    private Long id;

    @OneToOne
    @MapsId // colonne qui contiendra l'id du user (qui est l'id de l'objet aussi)
    @JoinColumn(name = "user_id")
    @ToString.Exclude //Evite une boucle infinie (appel de tostring de user qui réappelle celle de cette classe)
    @EqualsAndHashCode.Exclude
    @JsonIgnore // Evite une sérialisation en boucle (User(AddData(User(AddData ...))))
    private User user;

    private boolean acceptMap;

    private boolean acceptContact;

    private String street;

    private String postcode;

    private String town;

    private String phone;

    @Past(message="La date de naissance doit être antérieure à la date actuelle")
    private LocalDate birthday;

    private String gender;
    private String job;
    private String specializedTopics;
    private String otherSpecialization;
    private String teacherBehaviour;
    private String freeField;




}
