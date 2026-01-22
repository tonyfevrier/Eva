package com.eva.backend.model;

import java.time.LocalDate;

import jakarta.persistence.Embeddable;
import jakarta.validation.constraints.Past;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@AllArgsConstructor
@NoArgsConstructor
@Embeddable
public class UserAdditionalData {

    private boolean acceptMap;

    private boolean acceptContact;

    @Past(message="La date de naissance doit être antérieure à la date actuelle")
    private LocalDate birthday;

    private String gender;

    private String job;

    private String specializedTopics;

    private String otherSpecialization;

    private String teacherBehaviour;

    private String freeField;
}
