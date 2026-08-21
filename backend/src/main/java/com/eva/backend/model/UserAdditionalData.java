package com.eva.backend.model;

import java.time.Year;

import jakarta.persistence.Column;
import jakarta.persistence.Embeddable;
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

    private Year birthYear;

    private String gender;

    private String job;

    @Column(length=1000)
    private String specializedTopics;

    @Column(length=1000)
    private String otherSpecialization;

    private String teacherBehaviour;

    @Column(length=1000)
    private String freeField;
}
