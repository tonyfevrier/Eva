package com.eva.backend.model;

import jakarta.persistence.AttributeOverride;
import jakarta.persistence.AttributeOverrides;
import jakarta.persistence.Column;
import jakarta.persistence.Embeddable;
import jakarta.persistence.Embedded;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

@NoArgsConstructor
@AllArgsConstructor
@Builder
@Data
@Embeddable
public class PedagogicalContext {

    @Column(nullable = false)
    private String learningDifficulty;

    private String studyField;
    private String teachingTitle;
    private String knowledges;
    private String prerequisite;

    private String organisationParticularities;
    private String classesNumber;
    private String classesDuration;
    private String classesFrequency;
    private String classesDates;

    @Column(nullable = false)
    private String yearOfStudy;

    private String studentsSpecificities;
    private String studentsNumber;

    @Column(nullable = false)
    private String oldPedagogy;

    @Column(nullable = false)
    private String newPedagogy;

    @Column(nullable = false)
    private String groupsDescription;

    @Embedded
    @AttributeOverrides({
        @AttributeOverride(name = "initialEvaluation", column = @Column(name = "old_initialEvaluation")),
        @AttributeOverride(name = "immediateEvaluation", column = @Column(name = "old_immediateEvaluation")),
        @AttributeOverride(name = "delayedEvaluation", column = @Column(name = "old_delayedEvaluation")),
        @AttributeOverride(name = "accountedEvaluation", column = @Column(name = "old_accountedEvaluation"))
    })
    private Evaluations oldPedagogyEvaluations;

    @Embedded
    @AttributeOverrides({
        @AttributeOverride(name = "initialEvaluation", column = @Column(name = "new_initialEvaluation")),
        @AttributeOverride(name = "immediateEvaluation", column = @Column(name = "new_immediateEvaluation")),
        @AttributeOverride(name = "delayedEvaluation", column = @Column(name = "new_delayedEvaluation")),
        @AttributeOverride(name = "accountedEvaluation", column = @Column(name = "new_accountedEvaluation"))
    })
    private Evaluations newPedagogyEvaluations;
}
