package com.eva.backend;

import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.post;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.jsonPath;
import static org.assertj.core.api.Assertions.assertThat;

import java.time.LocalDate;
import java.util.Arrays;
import java.util.List;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.AutoConfigureMockMvc;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.http.MediaType;
import org.springframework.test.annotation.DirtiesContext;
import org.springframework.test.annotation.DirtiesContext.ClassMode;
import org.springframework.test.context.ActiveProfiles;
import org.springframework.test.web.servlet.MockMvc;

import com.eva.backend.model.Experimentation;
import com.eva.backend.model.Evaluations;
import com.eva.backend.model.PedagogicalContext;
import com.eva.backend.model.User;
import com.eva.backend.repository.ExperimentationRepository;
import com.eva.backend.repository.UserRepository;
import com.eva.backend.utils.UserCreation;
import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.databind.ObjectMapper;

@SpringBootTest
@AutoConfigureMockMvc
@ActiveProfiles("test") // Utilise application-test.properties
@DirtiesContext(classMode = ClassMode.BEFORE_EACH_TEST_METHOD) // Réinitialise le contexte (et la bdd) à chaque test
public class CrudExperimentationTests {
    @Autowired
    private MockMvc mockMvc;

    @Autowired
    private ObjectMapper objectMapper;

    @Autowired
    private UserCreation userCreation;

    @Autowired
    private ExperimentationRepository experimentationRepository;

    @Autowired
    private UserRepository userRepository;

    private String jwtCookie;

    @BeforeEach
    public void setup() throws Exception {
        String userJson = userCreation.registerAUser();        
        jwtCookie = userCreation.login(userJson); 
    }

    @Test
    public void testCreateExperimentation() throws Exception {
        String experimentationJson = createAnExperimentation();

        // Envoi de la requête et vérification de la réussite de son succès
        mockMvc.perform(post("/expe/create")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(experimentationJson)
                        .cookie(new jakarta.servlet.http.Cookie("jwt", jwtCookie)))
                        .andExpect(status().isOk())
                        .andExpect(jsonPath("$.message").value("L'expérimentation a bien été enregistrée"));

        // Vérification de l'enregistrement en base de données des objets
        List<Experimentation> experimentations = experimentationRepository.findAll();
        assertThat(experimentations).hasSize(1);
        
        Experimentation savedExperimentation = experimentations.get(0);
        
        // Vérifier les données de l'expérimentation
        assertThat(savedExperimentation.getKeywords()).containsExactly("mathématiques", "apprentissage actif", "collège");
        assertThat(savedExperimentation.getPersonalKeywords()).isEqualTo("motivation, collaboration");
        assertThat(savedExperimentation.getProtocol()).isEqualTo("Protocole 1");
        assertThat(savedExperimentation.getIsSharingData()).isTrue();
        assertThat(savedExperimentation.getDataPath()).isEmpty();
        
        // Vérifier que l'expérimentation contient le bon user
        User savedUser = userRepository.findByMail("marie.tremblay@mail.com");
        assertThat(savedExperimentation.getUser()).isNotNull();
        assertThat(savedExperimentation.getUser().getId()).isEqualTo(savedUser.getId());
        assertThat(savedExperimentation.getUser().getMail()).isEqualTo("marie.tremblay@mail.com");
        
        // Vérifier que le PedagogicalContext a bien été enregistré
        PedagogicalContext savedContext = savedExperimentation.getPedagogicalContext();
        assertThat(savedContext).isNotNull();
        assertThat(savedContext.getLearningDifficulty()).isEqualTo("Difficulté d'apprentissage en mathématiques");
        assertThat(savedContext.getLearningDifficultyOrigin()).isEqualTo("Manque de pratique des concepts de base");
        assertThat(savedContext.getStudyField()).isEqualTo("Mathématiques");
        assertThat(savedContext.getTeachingTitle()).isEqualTo("Algèbre et géométrie");
        assertThat(savedContext.getKnowledges()).isEqualTo("Équations du premier degré, théorème de Pythagore");
        assertThat(savedContext.getPrerequisite()).isEqualTo("Opérations de base, fractions");
        assertThat(savedContext.getOrganisationParticularities()).isEqualTo("Classe en demi-groupe");
        assertThat(savedContext.getClassesFrequencies()).isEqualTo("2 fois par semaine");
        assertThat(savedContext.getClassesDates()).isEqualTo("Lundi et jeudi de 10h à 11h");
        assertThat(savedContext.getYearOfStudy()).isEqualTo("5ème A");
        assertThat(savedContext.getStudentsSpecificities()).isEqualTo("Élèves à besoins particuliers");
        assertThat(savedContext.getStudentsNumber()).isEqualTo("24");
        assertThat(savedContext.getOldPedagogy()).isEqualTo("Cours magistral traditionnel");
        assertThat(savedContext.getNewPedagogy()).isEqualTo("Apprentissage par projet");
        
        // Vérifier les évaluations de l'ancienne pédagogie
        Evaluations oldEvaluations = savedContext.getOldPedagogyEvaluations();
        assertThat(oldEvaluations).isNotNull();
        assertThat(oldEvaluations.getInitialEvaluation()).isEqualTo(LocalDate.of(2026, 1, 15));
        assertThat(oldEvaluations.getImmediateEvaluation()).isEqualTo(LocalDate.of(2026, 2, 15));
        assertThat(oldEvaluations.getDelayedEvaluation()).isEqualTo(LocalDate.of(2026, 3, 15));
        assertThat(oldEvaluations.getAccountedEvaluation()).isEqualTo(LocalDate.of(2026, 4, 15));
        
        // Vérifier les évaluations de la nouvelle pédagogie
        Evaluations newEvaluations = savedContext.getNewPedagogyEvaluations();
        assertThat(newEvaluations).isNotNull();
        assertThat(newEvaluations.getInitialEvaluation()).isEqualTo(LocalDate.of(2026, 1, 20));
        assertThat(newEvaluations.getImmediateEvaluation()).isEqualTo(LocalDate.of(2026, 2, 20));
        assertThat(newEvaluations.getDelayedEvaluation()).isEqualTo(LocalDate.of(2026, 3, 20));
        assertThat(newEvaluations.getAccountedEvaluation()).isNull();
    }

    private String createAnExperimentation() throws JsonProcessingException{
        Evaluations oldPedagogyEvaluations = Evaluations.builder()
                .initialEvaluation(LocalDate.of(2026, 1, 15))
                .immediateEvaluation(LocalDate.of(2026, 2, 15))
                .delayedEvaluation(LocalDate.of(2026, 3, 15))
                .accountedEvaluation(LocalDate.of(2026, 4, 15))
                .build();

        Evaluations newPedagogyEvaluations = Evaluations.builder()
                .initialEvaluation(LocalDate.of(2026, 1, 20))
                .immediateEvaluation(LocalDate.of(2026, 2, 20))
                .delayedEvaluation(LocalDate.of(2026, 3, 20))
                .accountedEvaluation(null)
                .build();

        PedagogicalContext pedagogicalContext = PedagogicalContext.builder()
                .learningDifficulty("Difficulté d'apprentissage en mathématiques")
                .learningDifficultyOrigin("Manque de pratique des concepts de base")
                .studyField("Mathématiques")
                .teachingTitle("Algèbre et géométrie")
                .knowledges("Équations du premier degré, théorème de Pythagore")
                .prerequisite("Opérations de base, fractions")
                .organisationParticularities("Classe en demi-groupe")
                .classesFrequencies("2 fois par semaine")
                .classesDates("Lundi et jeudi de 10h à 11h")
                .yearOfStudy("5ème A")
                .studentsSpecificities("Élèves à besoins particuliers")
                .studentsNumber("24")
                .oldPedagogy("Cours magistral traditionnel")
                .newPedagogy("Apprentissage par projet")
                .oldPedagogyEvaluations(oldPedagogyEvaluations)
                .newPedagogyEvaluations(newPedagogyEvaluations)
                .build();

        Experimentation experimentation = Experimentation.builder()
                .keywords(Arrays.asList("mathématiques", "apprentissage actif", "collège"))
                .personalKeywords("motivation, collaboration")
                .protocol("Protocole 1")
                .isSharingData(true)
                .dataPath("")
                .pedagogicalContext(pedagogicalContext)
                .build();

        return objectMapper.writeValueAsString(experimentation);
    }
}
