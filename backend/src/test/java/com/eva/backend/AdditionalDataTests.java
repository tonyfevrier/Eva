package com.eva.backend;

import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.post;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;

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

import com.eva.backend.model.User;
import com.eva.backend.model.UserAdditionalData;
import com.eva.backend.repository.UserAdditionalDataRepository;
import com.eva.backend.repository.UserRepository;
import com.fasterxml.jackson.databind.ObjectMapper;

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
    private UserAdditionalDataRepository additionalDataRepository;

    @Autowired
    private UserRepository userRepository;

    @Test
    public void testRegisterAdditionalData() throws Exception {
        String userJson = registerAUser();
        String jwtCookie = login(userJson);        
        registerAdditionalData(jwtCookie);
        
        User savedUser = userRepository.findByMail("marie.tremblay@mail.com");
        UserAdditionalData savedData = additionalDataRepository.findById(savedUser.getId()).orElse(null);
        assertNotNull(savedData, "Les données additionnelles devraient être enregistrées");
        assertEquals("Université Paris", savedData.getAffiliation());
        assertTrue(savedData.isAcceptContact());
        assertFalse(savedData.isAcceptMap());
        assertEquals("123 Rue de la Paix", savedData.getStreet());
        assertEquals("75001", savedData.getPostcode());
        assertEquals("Paris", savedData.getTown());
        assertEquals("+33123456789", savedData.getPhone());
        assertEquals(savedUser.getId(), savedData.getId());
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
        additionalData.put("affiliation", "Université Paris");
        additionalData.put("acceptContact", true);
        additionalData.put("acceptMap", false);
        additionalData.put("street", "123 Rue de la Paix");
        additionalData.put("postcode", "75001");
        additionalData.put("town", "Paris");
        additionalData.put("phone", "+33123456789");

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
        UserAdditionalData savedData = additionalDataRepository.findById(savedUser.getId()).orElse(null);
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
}
