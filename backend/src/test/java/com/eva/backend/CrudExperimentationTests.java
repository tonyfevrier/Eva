package com.eva.backend;

import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.get;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.post;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.put;
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
import com.eva.backend.model.Institution;
import com.eva.backend.model.Evaluations;
import com.eva.backend.model.PedagogicalContext;
import com.eva.backend.model.User;
import com.eva.backend.records.ExperimentationRequest;
import com.eva.backend.repository.ExperimentationRepository;
import com.eva.backend.repository.InstitutionRepository;
import com.eva.backend.repository.UserRepository;
import com.eva.backend.service.InstitutionService;
import com.eva.backend.utils.UserCreation;
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

    @Autowired
    private InstitutionService institutionService;

    @Autowired
    private InstitutionRepository institutionRepository;

    private String jwtCookie;
    private Institution institution;

    @BeforeEach
    public void setup() throws Exception {
        String userJson = userCreation.registerAUser();        
        jwtCookie = userCreation.login(userJson);
        institution = createInstitution(); 
    }

    @Test
    public void testCreateExperimentation() throws Exception {
        createAnExperimentation();

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
        
        // Vérifier que l'expérimentation contient le bon user et que le user contient bien l'expérimentation
        User savedUser = userRepository.findByMailWithExperimentations("marie.tremblay@mail.com");
        assertThat(savedExperimentation.getUser()).isNotNull();
        assertThat(savedExperimentation.getUser().getId()).isEqualTo(savedUser.getId());
        assertThat(savedExperimentation.getUser().getMail()).isEqualTo("marie.tremblay@mail.com");
        
        List<Experimentation> userExperimentations = savedUser.getExperimentations();
        assertThat(userExperimentations).hasSize(1);
        assertThat(userExperimentations.get(0).getId()).isEqualTo(1);
        
        // Vérifier que l'expérimentation est bien associée à l'institution et que l'institution contient bien l'expérimentation
        assertThat(savedExperimentation.getInstitution()).isNotNull();
        assertThat(savedExperimentation.getInstitution().getId()).isEqualTo(institution.getId());
        assertThat(savedExperimentation.getInstitution().getName()).isEqualTo("Institution Initiale");
        assertThat(savedExperimentation.getInstitution().getTown()).isEqualTo("Marseille");

        Institution institution = institutionRepository.findByContactMailWithExperimentations("contact@initial.fr");
        List<Experimentation> institutionExperimentations = institution.getExperimentations();
        assertThat(institutionExperimentations).hasSize(1);
        assertThat(institutionExperimentations.get(0).getId()).isEqualTo(1);

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

    @Test
    public void testGetExperimentation() throws Exception {
        createAnExperimentation();
        
        mockMvc.perform(get("/expe/get/1")
                        .cookie(new jakarta.servlet.http.Cookie("jwt", jwtCookie)))
                        .andExpect(status().isOk())
                        .andExpect(jsonPath("$.id").value(1))
                        .andExpect(jsonPath("$.keywords[0]").value("mathématiques"))
                        .andExpect(jsonPath("$.keywords[1]").value("apprentissage actif"))
                        .andExpect(jsonPath("$.keywords[2]").value("collège"))
                        .andExpect(jsonPath("$.personalKeywords").value("motivation, collaboration"))
                        .andExpect(jsonPath("$.protocol").value("Protocole 1"))
                        .andExpect(jsonPath("$.institutionName").value("Institution Initiale"))
                        .andExpect(jsonPath("$.isSharingData").value(true))
                        .andExpect(jsonPath("$.pedagogicalContext.learningDifficulty").value("Difficulté d'apprentissage en mathématiques"))
                        .andExpect(jsonPath("$.pedagogicalContext.studyField").value("Mathématiques"))
                        .andExpect(jsonPath("$.pedagogicalContext.teachingTitle").value("Algèbre et géométrie"))
                        .andExpect(jsonPath("$.pedagogicalContext.yearOfStudy").value("5ème A"))
                        .andExpect(jsonPath("$.pedagogicalContext.studentsNumber").value("24"))
                        .andExpect(jsonPath("$.pedagogicalContext.oldPedagogy").value("Cours magistral traditionnel"))
                        .andExpect(jsonPath("$.pedagogicalContext.newPedagogy").value("Apprentissage par projet"))
                        .andExpect(jsonPath("$.pedagogicalContext.oldPedagogyEvaluations.initialEvaluation").value("2026-01-15"))
                        .andExpect(jsonPath("$.pedagogicalContext.oldPedagogyEvaluations.immediateEvaluation").value("2026-02-15"))
                        .andExpect(jsonPath("$.pedagogicalContext.oldPedagogyEvaluations.delayedEvaluation").value("2026-03-15"))
                        .andExpect(jsonPath("$.pedagogicalContext.oldPedagogyEvaluations.accountedEvaluation").value("2026-04-15"))
                        .andExpect(jsonPath("$.pedagogicalContext.newPedagogyEvaluations.initialEvaluation").value("2026-01-20"))
                        .andExpect(jsonPath("$.pedagogicalContext.newPedagogyEvaluations.immediateEvaluation").value("2026-02-20"))
                        .andExpect(jsonPath("$.pedagogicalContext.newPedagogyEvaluations.delayedEvaluation").value("2026-03-20"))
                        .andExpect(jsonPath("$.pedagogicalContext.newPedagogyEvaluations.accountedEvaluation").doesNotExist());
    }

    @Test
    public void testGetExperimentationNotFound() throws Exception {
        mockMvc.perform(get("/expe/get/999")
                        .cookie(new jakarta.servlet.http.Cookie("jwt", jwtCookie)))
                        .andExpect(status().isNotFound());
    }

    @Test
    public void testGetExperimentationListOfOneUser() throws Exception {
        createAnExperimentation();
        
        // Deuxième expérimentation avec des données différentes
        PedagogicalContext pedagogicalContext2 = PedagogicalContext.builder()
                .learningDifficulty("Difficultés en sciences")
                .learningDifficultyOrigin("Manque de méthode")
                .studyField("Physique")
                .teachingTitle("Mécanique et énergie")
                .knowledges("Forces, vitesse, accélération")
                .prerequisite("Mathématiques de base")
                .organisationParticularities("Travaux pratiques")
                .classesFrequencies("3 fois par semaine")
                .classesDates("Mardi, jeudi et vendredi")
                .yearOfStudy("4ème B")
                .studentsSpecificities("Classe standard")
                .studentsNumber("28")
                .oldPedagogy("Cours théorique")
                .newPedagogy("Apprentissage par l'expérimentation")
                .oldPedagogyEvaluations(Evaluations.builder().build())
                .newPedagogyEvaluations(Evaluations.builder().build())
                .build();

        Experimentation experimentation2 = Experimentation.builder()
                .keywords(Arrays.asList("physique", "sciences", "collège"))
                .personalKeywords("expérimentation, pratique")
                .protocol("Protocole 2")
                .isSharingData(false)
                .dataPath("")
                .pedagogicalContext(pedagogicalContext2)
                .build();

        ExperimentationRequest experimentationRequest2 = new ExperimentationRequest(experimentation2, institution.getId());
        String experimentationJson2 = objectMapper.writeValueAsString(experimentationRequest2);

        mockMvc.perform(post("/expe/create")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(experimentationJson2)
                        .cookie(new jakarta.servlet.http.Cookie("jwt", jwtCookie)))
                        .andExpect(status().isOk());

        // Récupérer la liste des expérimentations de l'utilisateur
        mockMvc.perform(get("/expe/getAll")
                        .cookie(new jakarta.servlet.http.Cookie("jwt", jwtCookie)))
                        .andExpect(status().isOk())
                        .andExpect(jsonPath("$.length()").value(2))
                        .andExpect(jsonPath("$[0].id").value(1))
                        .andExpect(jsonPath("$[0].keywords[0]").value("mathématiques"))
                        .andExpect(jsonPath("$[0].keywords[1]").value("apprentissage actif"))
                        .andExpect(jsonPath("$[0].keywords[2]").value("collège"))
                        .andExpect(jsonPath("$[0].personalKeywords").value("motivation, collaboration"))
                        .andExpect(jsonPath("$[0].institutionName").value("Institution Initiale"))
                        .andExpect(jsonPath("$[0].teachingTitle").value("Algèbre et géométrie"))
                        .andExpect(jsonPath("$[0].studyField").value("Mathématiques"))
                        .andExpect(jsonPath("$[0].yearOfStudy").value("5ème A"))
                        .andExpect(jsonPath("$[1].id").value(2))
                        .andExpect(jsonPath("$[1].keywords[0]").value("physique"))
                        .andExpect(jsonPath("$[1].keywords[1]").value("sciences"))
                        .andExpect(jsonPath("$[1].keywords[2]").value("collège"))
                        .andExpect(jsonPath("$[1].personalKeywords").value("expérimentation, pratique"))
                        .andExpect(jsonPath("$[1].institutionName").value("Institution Initiale"))
                        .andExpect(jsonPath("$[1].teachingTitle").value("Mécanique et énergie"))
                        .andExpect(jsonPath("$[1].studyField").value("Physique"))
                        .andExpect(jsonPath("$[1].yearOfStudy").value("4ème B"))
                        .andExpect(jsonPath("$[0].protocol").doesNotExist())
                        .andExpect(jsonPath("$[0].isSharingData").doesNotExist())
                        .andExpect(jsonPath("$[0].pedagogicalContext.learningDifficulty").doesNotExist());
    }

    @Test
    public void testUpdateExperimentation() throws Exception {
        createAnExperimentation();
        
        Evaluations updatedOldPedagogyEvaluations = Evaluations.builder()
                .initialEvaluation(LocalDate.of(2026, 2, 1))
                .immediateEvaluation(LocalDate.of(2026, 3, 1))
                .delayedEvaluation(LocalDate.of(2026, 4, 1))
                .accountedEvaluation(LocalDate.of(2026, 5, 1))
                .build();

        Evaluations updatedNewPedagogyEvaluations = Evaluations.builder()
                .initialEvaluation(LocalDate.of(2026, 2, 5))
                .immediateEvaluation(LocalDate.of(2026, 3, 5))
                .delayedEvaluation(LocalDate.of(2026, 4, 5))
                .accountedEvaluation(LocalDate.of(2026, 5, 5))
                .build();

        PedagogicalContext updatedPedagogicalContext = PedagogicalContext.builder()
                .learningDifficulty("Difficulté en géométrie avancée")
                .learningDifficultyOrigin("Problème de visualisation spatiale")
                .studyField("Géométrie")
                .teachingTitle("Géométrie dans l'espace")
                .knowledges("Volumes, surfaces, théorème de Thalès")
                .prerequisite("Géométrie plane")
                .organisationParticularities("Classe complète")
                .classesFrequencies("3 fois par semaine")
                .classesDates("Lundi, mercredi et vendredi")
                .yearOfStudy("3ème A")
                .studentsSpecificities("Élèves motivés")
                .studentsNumber("30")
                .oldPedagogy("Exercices traditionnels")
                .newPedagogy("Apprentissage par manipulation d'objets 3D")
                .oldPedagogyEvaluations(updatedOldPedagogyEvaluations)
                .newPedagogyEvaluations(updatedNewPedagogyEvaluations)
                .build();

        Experimentation updatedExperimentation = Experimentation.builder()
                .keywords(Arrays.asList("géométrie", "3D", "lycée"))
                .personalKeywords("visualisation, manipulation")
                .protocol("Protocole modifié")
                .isSharingData(false)
                .pedagogicalContext(updatedPedagogicalContext)
                .build();

        ExperimentationRequest updateRequest = new ExperimentationRequest(updatedExperimentation, institution.getId());
        String updateJson = objectMapper.writeValueAsString(updateRequest);

        mockMvc.perform(put("/expe/update/1")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(updateJson)
                        .cookie(new jakarta.servlet.http.Cookie("jwt", jwtCookie)))
                        .andExpect(status().isOk())
                        .andExpect(jsonPath("$.message").value("L'expérimentation a bien été mise à jour"));

        Experimentation savedExperimentation = experimentationRepository.findById(1L).orElseThrow();
        
        assertThat(savedExperimentation.getKeywords()).containsExactly("géométrie", "3D", "lycée");
        assertThat(savedExperimentation.getPersonalKeywords()).isEqualTo("visualisation, manipulation");
        assertThat(savedExperimentation.getProtocol()).isEqualTo("Protocole modifié");
        assertThat(savedExperimentation.getIsSharingData()).isFalse();
        
        PedagogicalContext savedContext = savedExperimentation.getPedagogicalContext();
        assertThat(savedContext.getLearningDifficulty()).isEqualTo("Difficulté en géométrie avancée");
        assertThat(savedContext.getStudyField()).isEqualTo("Géométrie");
        assertThat(savedContext.getTeachingTitle()).isEqualTo("Géométrie dans l'espace");
        assertThat(savedContext.getYearOfStudy()).isEqualTo("3ème A");
        assertThat(savedContext.getStudentsNumber()).isEqualTo("30");
        assertThat(savedContext.getOldPedagogy()).isEqualTo("Exercices traditionnels");
        assertThat(savedContext.getNewPedagogy()).isEqualTo("Apprentissage par manipulation d'objets 3D");
        
        // Vérifier que l'utilisateur n'a pas changé
        assertThat(savedExperimentation.getUser().getMail()).isEqualTo("marie.tremblay@mail.com");
        
        // Vérifier que l'institution n'a pas changé
        assertThat(savedExperimentation.getInstitution().getName()).isEqualTo("Institution Initiale");
    }

    @Test
    public void testUpdateExperimentationNotFound() throws Exception {
        PedagogicalContext pedagogicalContext = PedagogicalContext.builder()
                .learningDifficulty("Test")
                .studyField("Test")
                .teachingTitle("Test")
                .yearOfStudy("Test")
                .studentsNumber("10")
                .oldPedagogy("Test")
                .newPedagogy("Test")
                .oldPedagogyEvaluations(Evaluations.builder().build())
                .newPedagogyEvaluations(Evaluations.builder().build())
                .build();

        Experimentation experimentation = Experimentation.builder()
                .keywords(Arrays.asList("test"))
                .protocol("Test")
                .isSharingData(false)
                .pedagogicalContext(pedagogicalContext)
                .build();

        ExperimentationRequest request = new ExperimentationRequest(experimentation, institution.getId());
        String json = objectMapper.writeValueAsString(request);

        mockMvc.perform(put("/expe/update/999")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(json)
                        .cookie(new jakarta.servlet.http.Cookie("jwt", jwtCookie)))
                        .andExpect(status().isNotFound());
    }

    @Test
    public void testUpdateExperimentationUnauthorized() throws Exception {
        // Créer une expérimentation avec le premier utilisateur
        createAnExperimentation();
        
        // Créer et se connecter avec un deuxième utilisateur
        String secondUserJson = """
                {
                    "lastName": "Dupont",
                    "firstName": "Jean",
                    "birthday": "1985-03-20",
                    "mail": "jean.dupont@mail.com",
                    "password": "password123",
                    "confirmPassword": "password123"
                }
                """;
        
        mockMvc.perform(post("/auth/register")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(secondUserJson))
                        .andExpect(status().isOk());
        
        String secondUserCookie = userCreation.login(secondUserJson);
        
        // Préparer des données de mise à jour
        PedagogicalContext pedagogicalContext = PedagogicalContext.builder()
                .learningDifficulty("Test")
                .studyField("Test")
                .teachingTitle("Test")
                .yearOfStudy("Test")
                .studentsNumber("10")
                .oldPedagogy("Test")
                .newPedagogy("Test")
                .oldPedagogyEvaluations(Evaluations.builder().build())
                .newPedagogyEvaluations(Evaluations.builder().build())
                .build();

        Experimentation experimentation = Experimentation.builder()
                .keywords(Arrays.asList("test"))
                .protocol("Test")
                .isSharingData(false)
                .pedagogicalContext(pedagogicalContext)
                .build();

        ExperimentationRequest request = new ExperimentationRequest(experimentation, institution.getId());
        String json = objectMapper.writeValueAsString(request);
        
        // Tenter de mettre à jour l'expérimentation du premier utilisateur avec le cookie du second
        mockMvc.perform(put("/expe/update/1")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(json)
                        .cookie(new jakarta.servlet.http.Cookie("jwt", secondUserCookie)))
                        .andExpect(status().isForbidden())
                        .andExpect(jsonPath("$.message").value("Vous n'êtes pas autorisé à modifier cette expérimentation"));
    }

    private void createAnExperimentation() throws Exception{
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

        ExperimentationRequest experimentationRequest = new ExperimentationRequest(experimentation, institution.getId());

        String experimentationJson = objectMapper.writeValueAsString(experimentationRequest);

        mockMvc.perform(post("/expe/create")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(experimentationJson)
                        .cookie(new jakarta.servlet.http.Cookie("jwt", jwtCookie)))
                        .andExpect(status().isOk())
                        .andExpect(jsonPath("$.message").value("L'expérimentation a bien été enregistrée"))
                        .andExpect(jsonPath("$.id").value(1));
    }

    private Institution createInstitution(){
        Institution institution = Institution.builder()
                .name("Institution Initiale")
                .town("Marseille")
                .contactMail("contact@initial.fr")
                .category("Collège")
                .studentsNumber(500)
                .socialStatus("Public")
                .institutionSpecifities("Spécialité initiale")
                .studentsSpecificities("Étudiants initiaux")
                .teachersSpecificities("Enseignants initiaux")
                .build();
        
        return institutionService.save(institution);
    }
}
