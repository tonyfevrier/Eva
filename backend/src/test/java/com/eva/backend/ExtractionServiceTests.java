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
        assertThat(contact.get("Affiliation")).isEqualTo("Institution Test");
        assertThat(contact.get("Institution")).isEqualTo("contact@test.fr");
        assertThat(contact.get("Contact enseignant")).isEqualTo("marie.tremblay@mail.com");

        Map<String, Object> categories = extractedData.get("Catégories");
        assertThat(categories.get("Mots-clés")).isEqualTo(Arrays.asList("mathématiques", "apprentissage actif", "collège"));
        assertThat(categories.get("Mots-clés personnels")).isEqualTo("motivation, collaboration");

        Map<String, Object> pedagogicalContext = extractedData.get("Contexte pédagogique");
        assertThat(pedagogicalContext.get("Protocole")).isEqualTo("Protocole 1");
        assertThat(pedagogicalContext.get("Difficulté d'apprentissage")).isEqualTo("Difficulté d'apprentissage en mathématiques");
        assertThat(pedagogicalContext.get("Domaine d'étude")).isEqualTo("Mathématiques");
        assertThat(pedagogicalContext.get("Titre de l'enseignement")).isEqualTo("Algèbre et géométrie");
        assertThat(pedagogicalContext.get("Année d'étude")).isEqualTo("5ème A");
        assertThat(pedagogicalContext.get("Nombre d'étudiants")).isEqualTo("24");

        Map<String, Object> evaluations = extractedData.get("Evaluations");
        @SuppressWarnings("unchecked")
        Map<String, Object> oldEvaluations = (Map<String, Object>) evaluations.get("Evaluations pour l'ancienne pédagogie");
        @SuppressWarnings("unchecked")
        Map<String, Object> newEvaluations = (Map<String, Object>) evaluations.get("Evaluations pour la nouvelle pédagogie");
        assertThat(oldEvaluations.get("Evaluation initiale")).isEqualTo(LocalDate.of(2026, 1, 15));
        assertThat(oldEvaluations.get("Evaluation immédiate")).isEqualTo(LocalDate.of(2026, 2, 15));
        assertThat(oldEvaluations.get("Evaluation différée")).isEqualTo(LocalDate.of(2026, 3, 15));
        assertThat(newEvaluations.get("Evaluation initiale")).isEqualTo(LocalDate.of(2026, 1, 20));
        assertThat(newEvaluations.get("Evaluation immédiate")).isEqualTo(LocalDate.of(2026, 2, 20));
        assertThat(newEvaluations.get("Evaluation différée")).isEqualTo(LocalDate.of(2026, 3, 20));
    }
}
