package com.eva.backend;

import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.post;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.jsonPath;
import static org.assertj.core.api.Assertions.assertThat;

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
        assertThat(savedContext.getProblem()).isEqualTo("Difficulté d'apprentissage en mathématiques");
        assertThat(savedContext.getAffiliation()).isEqualTo("Collège Jean Moulin");
        assertThat(savedContext.getClassroom()).isEqualTo("5ème A");
        assertThat(savedContext.getOldPedagogy()).isEqualTo("Cours magistral traditionnel");
        assertThat(savedContext.getNewPedagogy()).isEqualTo("Apprentissage par projet");
        assertThat(savedContext.getGroupsDescription()).isEqualTo("4 groupes de 6 élèves");
        
        // Vérifier la relation bidirectionnelle
        assertThat(savedContext.getId()).isEqualTo(savedExperimentation.getId());
    }

    private String createAnExperimentation() throws JsonProcessingException{
        PedagogicalContext pedagogicalContext = PedagogicalContext.builder()
                .problem("Difficulté d'apprentissage en mathématiques")
                .affiliation("Collège Jean Moulin")
                .classroom("5ème A")
                .oldPedagogy("Cours magistral traditionnel")
                .newPedagogy("Apprentissage par projet")
                .groupsDescription("4 groupes de 6 élèves")
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
