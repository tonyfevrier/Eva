package com.eva.backend;

import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.get;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.post;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;

import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.content;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.jsonPath;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.header;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.cookie;

import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.AutoConfigureMockMvc;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.http.MediaType;
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
public class AuthenticationTests {

    @Autowired
    private MockMvc mockMvc;

    @Autowired
    private ObjectMapper objectMapper;

    @Test
    @DirtiesContext
    public void testLogin() throws Exception {
        String userJson = registerAUser(); 

        mockMvc.perform(post("/api/login")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(userJson))
                        .andExpect(status().isOk())
                        .andExpect(content().contentType(MediaType.APPLICATION_JSON))
                        .andExpect(header().exists("Set-Cookie"))
                        .andExpect(cookie().exists("jwt"))
                        .andExpect(cookie().exists("jwt-refresh"))
                        .andExpect(cookie().httpOnly("jwt", true))
                        .andExpect(cookie().httpOnly("jwt-refresh", true))
                        .andExpect(jsonPath("$.message").value("Login réussi"))
                        .andExpect(jsonPath("$.accessExpiresIn").exists())
                        .andExpect(jsonPath("$.refreshExpiresIn").exists());
    }

    @Test
    @DirtiesContext
    public void testLogout() throws Exception {
        mockMvc.perform(get("/api/logout"))
                        .andExpect(status().isOk())
                        .andExpect(content().contentType(MediaType.APPLICATION_JSON))
                        .andExpect(cookie().exists("jwt"))
                        .andExpect(cookie().exists("jwt-refresh"))
                        .andExpect(cookie().maxAge("jwt", 0))
                        .andExpect(cookie().maxAge("jwt-refresh", 0))
                        .andExpect(jsonPath("$.message").value("Le logout est réussi"));
    }

    @Test
    @DirtiesContext
    public void testRefresh() throws Exception {
        String userJson = registerAUser();

        // Récupérer le cookie de refresh
        var loginResult = mockMvc.perform(post("/api/login")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(userJson))
                        .andExpect(status().isOk())
                        .andReturn();

        String refreshCookie = loginResult.getResponse().getCookie("jwt-refresh").getValue();
        // Tester le refresh avec le cookie
        mockMvc.perform(get("/api/refresh")
                        .cookie(new jakarta.servlet.http.Cookie("jwt-refresh", refreshCookie)))
                        .andExpect(status().isOk())
                        .andExpect(content().contentType(MediaType.APPLICATION_JSON))
                        .andExpect(cookie().exists("jwt"))
                        .andExpect(cookie().httpOnly("jwt", true))
                        .andExpect(jsonPath("$.message").value("Token rafraîchi"))
                        .andExpect(jsonPath("$.accessExpiresIn").exists());
    }

    @Test
    public void testRefreshFails() throws Exception {
        // Tester le refresh sans cookie
        mockMvc.perform(get("/api/refresh"))
                        .andExpect(status().isBadRequest());

        // Tester le refresh avec un token vide
        mockMvc.perform(get("/api/refresh")
                        .cookie(new jakarta.servlet.http.Cookie("jwt-refresh", "")))
                        .andExpect(status().isBadRequest());

        // Tester le refresh avec un token invalide
        mockMvc.perform(get("/api/refresh")
                        .cookie(new jakarta.servlet.http.Cookie("jwt-refresh", "")))
                        .andExpect(status().isBadRequest());
    }

    private String registerAUser() throws Exception{
        User user = User.builder()
                        .firstname("marie")
                        .lastname("tremblay")
                        .mail("marie.tremblay@mail.com")
                        .password("MarieT123!")
                        .build();
        String userJson = objectMapper.writeValueAsString(user);

        mockMvc.perform(post("/api/register")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(userJson))
                        .andExpect(status().isOk());
        return userJson;
    }
}
