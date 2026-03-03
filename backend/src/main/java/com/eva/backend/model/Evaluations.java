package com.eva.backend.model;

import java.time.LocalDate;

import jakarta.persistence.Column;
import jakarta.persistence.Embeddable;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

@NoArgsConstructor
@AllArgsConstructor
@Builder
@Data
@Embeddable
public class Evaluations {

    @Column(nullable = false)
    private LocalDate initialEvaluation;
    
    @Column(nullable = false)
    private LocalDate immediateEvaluation;
    
    @Column(nullable = false)
    private LocalDate delayedEvaluation;

    private LocalDate accountedEvaluation;
}
