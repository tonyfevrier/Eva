package com.eva.backend;

import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.content;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.jsonPath;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.times;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.post;

import java.util.Map;


import static org.hamcrest.Matchers.is;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.AutoConfigureMockMvc;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.http.MediaType;
import org.springframework.mail.javamail.JavaMailSender;
import org.springframework.test.annotation.DirtiesContext;
import org.springframework.test.annotation.DirtiesContext.ClassMode;
import org.springframework.test.context.ActiveProfiles;
import org.springframework.test.context.bean.override.mockito.MockitoBean;
import org.springframework.test.web.servlet.MockMvc;

import com.eva.backend.model.User;
import com.eva.backend.service.JWTService;
import com.fasterxml.jackson.databind.ObjectMapper;

import jakarta.mail.Session;
import jakarta.mail.internet.MimeMessage;

@SpringBootTest
@AutoConfigureMockMvc
@ActiveProfiles("test") // Utilise application-test.properties
@DirtiesContext(classMode = ClassMode.BEFORE_EACH_TEST_METHOD) // Réinitialise le contexte (et la bdd) à chaque test
public class ResetPasswordTests {

    @Autowired
    private MockMvc mockMvc;

    @Autowired
    private ObjectMapper objectMapper;

    @Autowired
    private JWTService jwtService;

    @MockitoBean
    private JavaMailSender mailSender;

    @BeforeEach
    public void setup() {
        // configurer le mock de Mailsender pour retourner un MimeMessage (objet mail)
        MimeMessage mimeMessage = new MimeMessage((Session) null);
        when(mailSender.createMimeMessage()).thenReturn(mimeMessage);
    }

    @Test
    public void testResetMailWithValidEmail() throws Exception {
        registerAUser();
        
        String requestBody = objectMapper.writeValueAsString(
            Map.of("mail", "tony.fevrier@gmail.com")
        );
        
        mockMvc.perform(post("/auth/resetMail")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(requestBody))
                        .andExpect(status().isOk())
                        .andExpect(jsonPath("$.message", is("Un courriel vous a été envoyé, veuillez consulter votre messagerie.")));

        verify(mailSender, times(1)).send(any(MimeMessage.class));
    }

    @Test
    public void testResetMailWithInvalidEmail() throws Exception {
        String requestBody = objectMapper.writeValueAsString(
            Map.of("mail", "inexistant@gmail.com")
        );
        
        mockMvc.perform(post("/auth/resetMail")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(requestBody))
                        .andExpect(status().isBadRequest())
                        .andExpect(content().string("Aucun compte n'est associé à ce courriel."));
    }

    @Test
    public void testRecoverPwdWithValidToken() throws Exception {
        // 1. Enregistrer un utilisateur
        registerAUser();
        
        // 2. Générer directement un token valide
        String token = jwtService.generateToken("tony.fevrier@gmail.com", 10 * 60 * 1000); // 10 min
        
        // 3. Utiliser le token pour changer le mot de passe
        String recoverBody = objectMapper.writeValueAsString(
            Map.of("token", token, "password", "newPassword123")
        );
        
        mockMvc.perform(post("/auth/recoverPwd")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(recoverBody))
                        .andExpect(status().isOk())
                        .andExpect(jsonPath("$.message", is("Le mot de passe a été modifié")));
        
        // 4. Vérifier que le nouveau mot de passe fonctionne
        User loginUser = User.builder()
                        .mail("tony.fevrier@gmail.com")
                        .password("newPassword123")
                        .build();
        
        String loginJson = objectMapper.writeValueAsString(loginUser);
        
        mockMvc.perform(post("/auth/login")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(loginJson))
                        .andExpect(status().isOk());
    }

    @Test
    public void testRecoverPwdWithExpiredToken() throws Exception {
        // 1. Enregistrer un utilisateur
        registerAUser();
        
        // 2. Générer un token déjà expiré (durée de -1000 ms)
        String expiredToken = jwtService.generateToken("tony.fevrier@gmail.com", -1000);
        
        String recoverBody = objectMapper.writeValueAsString(
            Map.of("token", expiredToken, "password", "newPassword123")
        );
        
        mockMvc.perform(post("/auth/recoverPwd")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(recoverBody))
                        .andExpect(status().isBadRequest());
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
}

