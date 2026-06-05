package com.eva.backend.utils;

import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.post;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;

import java.util.HashMap;
import java.util.Map;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.MediaType;
import org.springframework.stereotype.Component;
import org.springframework.test.web.servlet.MockMvc;

import com.eva.backend.model.User;
import com.eva.backend.repository.UserRepository;
import com.fasterxml.jackson.databind.ObjectMapper;

@Component
public class UserCreation {
    @Autowired
    private MockMvc mockMvc;

    @Autowired
    private ObjectMapper objectMapper;

    @Autowired
    private UserRepository userRepository;

    public String registerAUser() throws Exception{
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

        User registeredUser = userRepository.findByMail(user.getMail());
        registeredUser.setEmailVerified(true);
        userRepository.save(registeredUser);

        return userJson;
    }

    public String login(String userJson) throws Exception{
        var loginResult = mockMvc.perform(post("/auth/login")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(userJson))
                        .andExpect(status().isOk())
                        .andReturn();

        String jwtCookie = loginResult.getResponse().getCookie("jwt").getValue();
        return jwtCookie;
    }

    public void registerAdditionalData(String jwtCookie) throws Exception{
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
}
