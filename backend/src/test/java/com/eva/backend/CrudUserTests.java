package com.eva.backend;

import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.delete;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.get;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.post;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.put;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;

import java.util.HashMap;
import java.util.Map;

import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.content;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.jsonPath;
import static org.hamcrest.Matchers.hasSize;
import static org.hamcrest.Matchers.is;

import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.AutoConfigureMockMvc;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.http.MediaType;
import org.springframework.security.test.context.support.WithMockUser;
import org.springframework.test.annotation.DirtiesContext;
import org.springframework.test.annotation.DirtiesContext.ClassMode;
import org.springframework.test.context.ActiveProfiles;
import org.springframework.test.web.servlet.MockMvc;

import com.eva.backend.model.User;
import com.fasterxml.jackson.databind.ObjectMapper;

@SpringBootTest
@AutoConfigureMockMvc
@ActiveProfiles("test") // Utilise application-test.properties
@DirtiesContext(classMode = ClassMode.BEFORE_EACH_TEST_METHOD) // Réinitialise le contexte (et la bdd) à chaque test
public class CrudUserTests {

    @Autowired
    private MockMvc mockMvc;

    @Autowired
    private ObjectMapper objectMapper;

    @Test
    @WithMockUser(roles = "ADMIN") // Pour récupérer les users
    public void testRegisterUser() throws Exception {
        User user = User.builder()
                        .firstname("tony")
                        .lastname("fevrier")
                        .mail("tony.fevrier@gmail.com")
                        .password("c!!21Cdq")
                        .build();
        
        String userJson = objectMapper.writeValueAsString(user);

        mockMvc.perform(post("/auth/register")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(userJson))
                        .andExpect(status().isOk());
        
        mockMvc.perform(get("/auth/users"))
                       .andExpect(jsonPath("$[0].firstname", is("tony")))
                       .andExpect(jsonPath("$[0].lastname", is("fevrier")))
                       .andExpect(jsonPath("$[0].mail", is("tony.fevrier@gmail.com")));
    } 

    @Test
    public void testBadMailInput() throws Exception {
        User user = User.builder()
                        .firstname("tony")
                        .lastname("fevrier")
                        .mail("tony")
                        .password("c!!21Cdq")
                        .build();
        
        String userJson = objectMapper.writeValueAsString(user);
        
        mockMvc.perform(post("/auth/register")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(userJson))
                        .andExpect(status().isBadRequest())
                        .andExpect(content().string("Email invalide."));
    }

    @Test
    public void testBadPasswordInput() throws Exception {
        User user = User.builder()
                        .firstname("tony")
                        .lastname("fevrier")
                        .mail("tony.fevrier@gmail.com")
                        .password("test")
                        .build();
        
        String userJson = objectMapper.writeValueAsString(user);
        
        mockMvc.perform(post("/auth/register")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(userJson))
                        .andExpect(status().isBadRequest())
                        .andExpect(content().string("Le mot de passe doit contenir au moins 8 caractères."));
    }

    @Test
    @WithMockUser(roles = "ADMIN")
    public void testDeleteUser() throws Exception {
        String accessCookie = registerLogUserAndGetAccessCookie();

        mockMvc.perform(get("/auth/users"))
                        .andExpect(jsonPath("$",hasSize(1)));

        mockMvc.perform(delete("/auth/delete")
                        .cookie(new jakarta.servlet.http.Cookie("jwt", accessCookie)));

        mockMvc.perform(get("/auth/users"))
                        .andExpect(jsonPath("$",hasSize(0)));
    }

    @Test
    @WithMockUser(roles = "ADMIN")
    public void testUpdateUser() throws Exception {
        String accessCookie = registerLogUserAndGetAccessCookie();

        Map<String, Object> updatedUserData = new HashMap<>();
        updatedUserData.put("firstname", "toto");
        updatedUserData.put("password", "newpassword");
        updatedUserData.put("acceptMap", true);
        updatedUserData.put("acceptContact", false);
        updatedUserData.put("birthday", "1985-03-20");
        updatedUserData.put("gender", "Homme");
        updatedUserData.put("job", "Professeur");
        updatedUserData.put("specializedTopics", "Physique");
        updatedUserData.put("otherSpecialization", "Mécanique quantique");
        updatedUserData.put("teacherBehaviour", "Rigoureux");
        updatedUserData.put("freeField", "Passionné de sciences");

        String userJson = objectMapper.writeValueAsString(updatedUserData);

        mockMvc.perform(put("/auth/update")
                        .cookie(new jakarta.servlet.http.Cookie("jwt", accessCookie))
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(userJson))
                        .andExpect(status().isOk());
        
        mockMvc.perform(get("/auth/users"))
                       .andExpect(jsonPath("$[0].firstname", is("toto")))
                       .andExpect(jsonPath("$[0].lastname", is("fevrier")))
                       .andExpect(jsonPath("$[0].mail", is("tony.fevrier@gmail.com")))
                       .andExpect(jsonPath("$[0].additionalData.acceptMap", is(true)))
                       .andExpect(jsonPath("$[0].additionalData.acceptContact", is(false)))
                       .andExpect(jsonPath("$[0].additionalData.birthday", is("1985-03-20")))
                       .andExpect(jsonPath("$[0].additionalData.gender", is("Homme")))
                       .andExpect(jsonPath("$[0].additionalData.job", is("Professeur")))
                       .andExpect(jsonPath("$[0].additionalData.specializedTopics", is("Physique")))
                       .andExpect(jsonPath("$[0].additionalData.otherSpecialization", is("Mécanique quantique")))
                       .andExpect(jsonPath("$[0].additionalData.teacherBehaviour", is("Rigoureux")))
                       .andExpect(jsonPath("$[0].additionalData.freeField", is("Passionné de sciences")));
        }

    @Test
    public void testGetOneUser() throws Exception {
        // Ma config de spring security exige que toute requête post login ait le cookie d'authentification.
        String accessCookie = registerLogUserAndGetAccessCookie();
        registerAdditionalData(accessCookie);
        createInstitution(accessCookie);
        
        mockMvc.perform(get("/auth/profile")
                        .cookie(new jakarta.servlet.http.Cookie("jwt", accessCookie)))
                        .andExpect(status().isOk())
                        .andExpect(jsonPath("$.firstname", is("tony")))
                        .andExpect(jsonPath("$.lastname", is("fevrier")))
                        .andExpect(jsonPath("$.mail", is("tony.fevrier@gmail.com")))
                        .andExpect(jsonPath("$.acceptContact", is(true)))
                        .andExpect(jsonPath("$.acceptMap", is(false)))
                        .andExpect(jsonPath("$.birthday", is("1990-05-15")))
                        .andExpect(jsonPath("$.gender", is("Homme")))
                        .andExpect(jsonPath("$.job", is("Enseignant")))
                        .andExpect(jsonPath("$.specializedTopics", is("Informatique")))
                        .andExpect(jsonPath("$.otherSpecialization", is("Intelligence Artificielle")))
                        .andExpect(jsonPath("$.teacherBehaviour", is("Pédagogue")))
                        .andExpect(jsonPath("$.freeField", is("Passionné de technologie")))
                        .andExpect(jsonPath("$.institutions", hasSize(1)))
                        .andExpect(jsonPath("$.institutions[0]", is("Université de Test")));
    }

    @Test
    public void testGetOneUserWithNoAdditionalData() throws Exception {
        // Ma config de spring security exige que toute requête post login ait le cookie d'authentification.
        String accessCookie = registerLogUserAndGetAccessCookie();
        
        mockMvc.perform(get("/auth/profile")
                        .cookie(new jakarta.servlet.http.Cookie("jwt", accessCookie)))
                        .andExpect(status().isOk())
                        .andExpect(jsonPath("$.firstname", is("tony")))
                        .andExpect(jsonPath("$.lastname", is("fevrier")))
                        .andExpect(jsonPath("$.mail", is("tony.fevrier@gmail.com")))
                        .andExpect(jsonPath("$.acceptContact", is(false)))
                        .andExpect(jsonPath("$.acceptMap", is(false)))
                        .andExpect(jsonPath("$.birthday", is("")))
                        .andExpect(jsonPath("$.gender", is("")))
                        .andExpect(jsonPath("$.job", is("")))
                        .andExpect(jsonPath("$.specializedTopics", is("")))
                        .andExpect(jsonPath("$.otherSpecialization", is("")))
                        .andExpect(jsonPath("$.teacherBehaviour", is("")))
                        .andExpect(jsonPath("$.freeField", is("")));
    }

    private String registerAUser() throws Exception{
        User user = User.builder()
                        .firstname("tony")
                        .lastname("fevrier")
                        .mail("tony.fevrier@gmail.com")
                        .password("c!!21Cdq")
                        .build();
        
        String userJson = objectMapper.writeValueAsString(user);

        mockMvc.perform(post("/auth/register")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(userJson))
                        .andExpect(status().isOk());
        return userJson;
    }

    private String registerLogUserAndGetAccessCookie() throws Exception{
        registerAUser();
        // Créer les credentials pour le login avec le mot de passe en clair
        User loginUser = User.builder()
                        .mail("tony.fevrier@gmail.com")
                        .password("c!!21Cdq")
                        .build();
        
        String loginJson = objectMapper.writeValueAsString(loginUser);

        var loginResult = mockMvc.perform(post("/auth/login")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(loginJson))
                        .andExpect(status().isOk())
                        .andReturn();
        return loginResult.getResponse().getCookie("jwt").getValue(); 
    }

    private void registerAdditionalData(String jwtCookie) throws Exception{
        Map<String, Object> additionalData = new HashMap<>();
        additionalData.put("acceptContact", true);
        additionalData.put("acceptMap", false);
        additionalData.put("birthday", "1990-05-15");
        additionalData.put("gender", "Homme");
        additionalData.put("job", "Enseignant");
        additionalData.put("specializedTopics", "Informatique");
        additionalData.put("otherSpecialization", "Intelligence Artificielle");
        additionalData.put("teacherBehaviour", "Pédagogue");
        additionalData.put("freeField", "Passionné de technologie");

        String additionalDataJson = objectMapper.writeValueAsString(additionalData);

        mockMvc.perform(post("/user/addData")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(additionalDataJson)
                        .cookie(new jakarta.servlet.http.Cookie("jwt", jwtCookie)))
                        .andExpect(status().isOk());
    }

    private void createInstitution(String jwtCookie) throws Exception {
        Map<String, Object> institutionData = new HashMap<>();
        institutionData.put("name", "Université de Test");
        institutionData.put("town", "Paris");
        institutionData.put("contactMail", "contact@universite-test.fr");
        institutionData.put("category", "Université");
        institutionData.put("studentsNumber", 5000);
        institutionData.put("socialStatus", "Public");
        institutionData.put("institutionSpecifities", "Spécialisée en sciences");

        String institutionJson = objectMapper.writeValueAsString(institutionData);

        mockMvc.perform(post("/institution/create")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(institutionJson)
                        .cookie(new jakarta.servlet.http.Cookie("jwt", jwtCookie)))
                        .andExpect(status().isOk());
    }
}

    
