package com.eva.backend.records;

import com.eva.backend.model.Experimentation;

public record ExperimentationRequest(Experimentation experimentation, Long affiliationID) {
}
