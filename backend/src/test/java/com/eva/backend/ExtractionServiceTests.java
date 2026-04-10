package com.eva.backend;

import static org.assertj.core.api.Assertions.assertThat;

import java.time.LocalDate;
import java.util.Arrays;
import java.util.Map;

import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.orm.jpa.DataJpaTest;
import org.springframework.context.annotation.Import;
import org.springframework.test.context.ActiveProfiles;

import com.eva.backend.model.Evaluations;
import com.eva.backend.model.Institution;
import com.eva.backend.model.User;
import com.eva.backend.service.DataExtractionService;
import com.eva.backend.utils.JpaDataCreation;

@DataJpaTest
@Import({DataExtractionService.class, JpaDataCreation.class})
@ActiveProfiles("test")
public class ExtractionServiceTests {
    @Autowired
    private JpaDataCreation dataCreator;

    @Autowired
    private DataExtractionService dataExtractionService;

    @Test
    void shouldExtractExperimentationData() {
	
        User user = dataCreator.createAUser();
        Institution institution = dataCreator.createAnInstitution();
        Long experimentationId = dataCreator.createAnExperimentation(user, institution);
        Map<String, Map<String, Object>> extractedData = dataExtractionService.extractExperimentationData(experimentationId);

        assertThat(extractedData).containsKeys("Contact", "Catégories", "Contexte pédagogique", "Evaluations");

        Map<String, Object> contact = extractedData.get("Contact");
        assertThat(contact.get("affiliation")).isEqualTo("Institution Test");
        assertThat(contact.get("contactInstitution")).isEqualTo("contact@test.fr");
        assertThat(contact.get("contactTeacher")).isEqualTo("marie.tremblay@mail.com");

        Map<String, Object> categories = extractedData.get("Catégories");
        assertThat(categories.get("keywords")).isEqualTo(Arrays.asList("mathématiques", "apprentissage actif", "collège"));
        assertThat(categories.get("personalKeywords")).isEqualTo("motivation, collaboration");

        Map<String, Object> pedagogicalContext = extractedData.get("Contexte pédagogique");
        assertThat(pedagogicalContext.get("protocol")).isEqualTo("Protocole 1");
        assertThat(pedagogicalContext.get("learningDifficulty")).isEqualTo("Difficulté d'apprentissage en mathématiques");
        assertThat(pedagogicalContext.get("studyField")).isEqualTo("Mathématiques");
        assertThat(pedagogicalContext.get("teachingTitle")).isEqualTo("Algèbre et géométrie");
        assertThat(pedagogicalContext.get("yearOfStudy")).isEqualTo("5ème A");
        assertThat(pedagogicalContext.get("studentsNumber")).isEqualTo("24");

        Map<String, Object> evaluations = extractedData.get("Evaluations");
        Evaluations oldEvaluations = (Evaluations) evaluations.get("oldPedagogyEvaluations");
        Evaluations newEvaluations = (Evaluations) evaluations.get("newPedagogyEvaluations");
        assertThat(oldEvaluations.getInitialEvaluation()).isEqualTo(LocalDate.of(2026, 1, 15));
        assertThat(oldEvaluations.getImmediateEvaluation()).isEqualTo(LocalDate.of(2026, 2, 15));
        assertThat(oldEvaluations.getDelayedEvaluation()).isEqualTo(LocalDate.of(2026, 3, 15));
        assertThat(newEvaluations.getInitialEvaluation()).isEqualTo(LocalDate.of(2026, 1, 20));
        assertThat(newEvaluations.getImmediateEvaluation()).isEqualTo(LocalDate.of(2026, 2, 20));
        assertThat(newEvaluations.getDelayedEvaluation()).isEqualTo(LocalDate.of(2026, 3, 20));
    }
}
