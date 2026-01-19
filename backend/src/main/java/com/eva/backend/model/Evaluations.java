package com.eva.backend.model;

import java.time.LocalDate;

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
    private LocalDate initialEvaluation;
    private LocalDate immediateEvaluation;
    private LocalDate delayedEvaluation;
    private LocalDate accountedEvaluation;
}
