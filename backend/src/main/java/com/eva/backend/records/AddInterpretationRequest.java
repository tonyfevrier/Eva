package com.eva.backend.records;

import com.eva.backend.model.Interpretation;

public record AddInterpretationRequest(Interpretation interpretation, boolean expeWorked) {
}