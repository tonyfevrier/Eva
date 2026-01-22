package com.eva.backend;

import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.post;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.jsonPath;

import java.util.HashMap;
import java.util.Map;

import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.AutoConfigureMockMvc;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.http.MediaType;
import org.springframework.test.annotation.DirtiesContext;
import org.springframework.test.annotation.DirtiesContext.ClassMode;
import org.springframework.test.context.ActiveProfiles;
import org.springframework.test.web.servlet.MockMvc;

import com.eva.backend.model.Institution;
import com.eva.backend.model.User;
import com.eva.backend.model.UserAdditionalData;
import com.eva.backend.repository.UserRepository;
import com.fasterxml.jackson.databind.ObjectMapper;

import jakarta.validation.constraints.Email;

import static org.junit.jupiter.api.Assertions.*;

@SpringBootTest
@AutoConfigureMockMvc
@ActiveProfiles("test") // Utilise application-test.properties
@DirtiesContext(classMode = ClassMode.BEFORE_EACH_TEST_METHOD) // Réinitialise le contexte (et la bdd) à chaque test
public class AdditionalDataTests {
    @Autowired
    private MockMvc mockMvc;

    @Autowired
    private ObjectMapper objectMapper;

    @Autowired
    private UserRepository userRepository;

    @Test
    public void testRegisterAdditionalData() throws Exception {
        String userJson = registerAUser();
        String jwtCookie = login(userJson);        
        registerAdditionalData(jwtCookie);
        
        User savedUser = userRepository.findByMail("marie.tremblay@mail.com");
        UserAdditionalData savedData = savedUser.getAdditionalData();
        assertNotNull(savedData, "Les données additionnelles devraient être enregistrées");
        assertTrue(savedData.isAcceptContact());
        assertFalse(savedData.isAcceptMap());
        assertEquals("1990-05-15", savedData.getBirthday().toString());
        assertEquals("Femme", savedData.getGender());
        assertEquals("Enseignante", savedData.getJob());
        assertEquals("Mathématiques", savedData.getSpecializedTopics());
        assertEquals("Géométrie", savedData.getOtherSpecialization());
        assertEquals("Bienveillant", savedData.getTeacherBehaviour());
        assertEquals("Passionnée par l'enseignement", savedData.getFreeField());
    }

    private String registerAUser() throws Exception{
        User user = User.builder()
                        .firstname("marie")
                        .lastname("tremblay")
                        .mail("marie.tremblay@mail.com")
                        .password("MarieT123!")
                        .build();
        String userJson = objectMapper.writeValueAsString(user);

        mockMvc.perform(post("/auth/register")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(userJson))
                        .andExpect(status().isOk());
        return userJson;
    }

    private String login(String userJson) throws Exception{
        var loginResult = mockMvc.perform(post("/auth/login")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(userJson))
                        .andExpect(status().isOk())
                        .andReturn();

        String jwtCookie = loginResult.getResponse().getCookie("jwt").getValue();
        return jwtCookie;
    }

    private void registerAdditionalData(String jwtCookie) throws Exception{
        Map<String, Object> additionalData = new HashMap<>();
        additionalData.put("acceptContact", true);
        additionalData.put("acceptMap", false);
        additionalData.put("birthday", "1990-05-15");
        additionalData.put("gender", "Femme");
        additionalData.put("job", "Enseignante");
        additionalData.put("specializedTopics", "Mathématiques");
        additionalData.put("otherSpecialization", "Géométrie");
        additionalData.put("teacherBehaviour", "Bienveillant");
        additionalData.put("freeField", "Passionnée par l'enseignement");

        String additionalDataJson = objectMapper.writeValueAsString(additionalData);

        mockMvc.perform(post("/user/addData")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(additionalDataJson)
                        .cookie(new jakarta.servlet.http.Cookie("jwt", jwtCookie)))
                        .andExpect(status().isOk());
    }

    @Test
    public void testRegisterAdditionalDataWithNoAffiliation() throws Exception {
        String userJson = registerAUser();
        String jwtCookie = login(userJson);        
        registerAdditionalDataWithNoAffiliation(jwtCookie);
        
        User savedUser = userRepository.findByMail("marie.tremblay@mail.com");
        UserAdditionalData savedData = savedUser.getAdditionalData();
        assertNull(savedData);
    }

    private void registerAdditionalDataWithNoAffiliation(String jwtCookie) throws Exception{
        Map<String, Object> additionalData = new HashMap<>();
        additionalData.put("affiliation", "");

        String additionalDataJson = objectMapper.writeValueAsString(additionalData);

        mockMvc.perform(post("/user/addData")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(additionalDataJson)
                        .cookie(new jakarta.servlet.http.Cookie("jwt", jwtCookie)))
                        .andExpect(status().isBadRequest());
    }

    @Test
    public void testLoginAfterRegisteringAdditionalData() throws Exception {
        String userJson = registerAUser();
        String jwtCookie = login(userJson);        
        registerAdditionalData(jwtCookie);

        mockMvc.perform(post("/auth/login")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(userJson))
                        .andExpect(status().isOk())
                        .andExpect(jsonPath("$.additionalData").isNotEmpty());
    }

    @Test
    @org.springframework.transaction.annotation.Transactional // Permet de garder la connexion à la table Institution : quand on charge User et qu'il contient une entité d'une autre table, la connection à cette table est fermée a priori.
    public void testAddAnInstitution() throws Exception {
        String userJson = registerAUser();
        String jwtCookie = login(userJson);        
        
        // Créer et associer une institution au user
        createInstitution(jwtCookie);
        
        // Vérifier que l'institution est bien créée et associée au user
        User savedUser = userRepository.findByMail("marie.tremblay@mail.com");
        assertNotNull(savedUser.getInstitutions(), "La liste des institutions ne devrait pas être null");
        assertEquals(1, savedUser.getInstitutions().size(), "Le user devrait avoir 1 institution");
        
        Institution savedInstitution = savedUser.getInstitutions().get(0);
        assertNotNull(savedInstitution.getId(), "L'ID de l'institution devrait être généré");
        assertEquals("Université de Paris", savedInstitution.getName());
        assertEquals("Paris", savedInstitution.getTown());
        assertEquals("contact@univ-paris.fr", savedInstitution.getContactMail());
        assertEquals("Université", savedInstitution.getCategory());
        assertEquals(25000, savedInstitution.getStudentsNumber());
    }

    private void createInstitution(String jwtCookie) throws Exception {
        Institution institution = Institution.builder()
                .name("Université de Paris")
                .town("Paris")
                .contactMail("contact@univ-paris.fr")
                .category("Université")
                .studentsNumber(25000)
                .socialStatus("Public")
                .institutionSpecifities("Formation pluridisciplinaire")
                .studentsSpecificities("Étudiants internationaux")
                .teachersSpecificities("Enseignants-chercheurs")
                .build();

        String institutionJson = objectMapper.writeValueAsString(institution);

        mockMvc.perform(post("/institution/create")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(institutionJson)
                        .cookie(new jakarta.servlet.http.Cookie("jwt", jwtCookie)))
                        .andExpect(status().isOk());
    }
} 