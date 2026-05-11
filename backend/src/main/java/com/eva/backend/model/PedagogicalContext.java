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

    @Column(nullable = false, length = 1000)
    private String learningDifficulty;

    @Column(nullable = false, length = 1000)
    private String learningDifficultyOrigin;

    @Column(nullable = false)
    private String studyField;

    @Column(nullable = false)
    private String teachingTitle;

    @Column(nullable = false, length = 1000)
    private String knowledges;

    @Column(nullable = false, length = 1000)
    private String prerequisite;

    @Column(nullable = false, length = 1000)
    private String organisationParticularities;
    
    @Column(nullable = false, length = 1000)
    private String classesFrequencies;
    
    @Column(nullable = false, length = 1000)
    private String classesDates;

    @Column(nullable = false)
    private String yearOfStudy;

    @Column(nullable = false, length = 1000)
    private String studentsSpecificities;

    @Column(nullable = false)
    private String studentsNumber;

    @Column(nullable = false, length = 1000)
    private String oldPedagogy;

    @Column(nullable = false, length = 1000)
    private String newPedagogy;

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
