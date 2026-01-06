package com.eva.backend;

import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.delete;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.get;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.post;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.put;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;


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

        User updatedUser = User.builder()
                               .firstname("toto")
                               .password("newpassword")
                               .build();

        String userJson = objectMapper.writeValueAsString(updatedUser);

        mockMvc.perform(put("/auth/update")
                        .cookie(new jakarta.servlet.http.Cookie("jwt", accessCookie))
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(userJson))
                        .andExpect(status().isOk());
        
        mockMvc.perform(get("/auth/users"))
                       .andExpect(jsonPath("$[0].firstname", is("toto")))
                       .andExpect(jsonPath("$[0].lastname", is("fevrier")))
                       .andExpect(jsonPath("$[0].mail", is("tony.fevrier@gmail.com")));
        }

    @Test
    public void testGetOneUser() throws Exception {
        // Ma config de spring security exige que toute requête post login ait le cookie d'authentification.
        String accessCookie = registerLogUserAndGetAccessCookie();                        
        
        mockMvc.perform(get("/auth/profile")
                        .cookie(new jakarta.servlet.http.Cookie("jwt", accessCookie)))
                        .andExpect(status().isOk())
                        .andExpect(jsonPath("$.firstname", is("tony")))
                        .andExpect(jsonPath("$.lastname", is("fevrier")))
                        .andExpect(jsonPath("$.mail", is("tony.fevrier@gmail.com")));
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
}

    
