package com.eva.backend.model;

import jakarta.persistence.Entity;
import lombok.AllArgsConstructor;
import lombok.Data;

@AllArgsConstructor
@Data
@Entity
public class ExperimentationRequest {
    private Experimentation experimentation;
    private Long affiliationID;
}
