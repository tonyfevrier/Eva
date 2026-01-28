package com.eva.backend;

import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.get;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.post;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.put;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.jsonPath;
import static org.hamcrest.Matchers.is;
import static org.hamcrest.Matchers.hasSize;
import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertTrue;

import java.util.Optional;

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

import com.eva.backend.model.Institution;
import com.eva.backend.repository.InstitutionRepository;
import com.eva.backend.service.InstitutionService;
import com.eva.backend.utils.UserCreation;
import com.fasterxml.jackson.databind.ObjectMapper;


@SpringBootTest
@AutoConfigureMockMvc
@ActiveProfiles("test")
@DirtiesContext(classMode = ClassMode.BEFORE_EACH_TEST_METHOD)
public class CrudInstitutionTests {

    @Autowired
    private MockMvc mockMvc;

    @Autowired
    private ObjectMapper objectMapper;

    @Autowired
    private InstitutionService institutionService;

    @Autowired
    private InstitutionRepository institutionRepository;

    @Autowired
    private UserCreation userCreation;

    private String jwtCookie;

    @BeforeEach
    public void setup() throws Exception {
        String userJson = userCreation.registerAUser();        
        jwtCookie = userCreation.login(userJson); 
    }

    @Test
    public void testCreateInstitution() throws Exception {        
        Institution institution = createInstitution();
        
        String institutionJson = objectMapper.writeValueAsString(institution);
        
        mockMvc.perform(post("/institution/create")
                .cookie(new jakarta.servlet.http.Cookie("jwt", jwtCookie))
                .contentType(MediaType.APPLICATION_JSON)
                .content(institutionJson))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.message", is("Institution créée")));
        
        // Vérification dans la base de données
        Optional<Institution> savedInstitution = institutionRepository.findAll()
                .stream()
                .filter(i -> i.getContactMail().equals("contact@initial.fr"))
                .findFirst();
        
        assertTrue(savedInstitution.isPresent(), "L'institution devrait être présente dans la base de données");
        assertEquals("Institution Initiale", savedInstitution.get().getName());
        assertEquals("Marseille", savedInstitution.get().getTown());
        assertEquals(500, savedInstitution.get().getStudentsNumber());
    }

    @Test
    public void testGetInstitution() throws Exception {
        Institution savedInstitution = createInstitution();
        
        mockMvc.perform(get("/institution/get/" + savedInstitution.getId())
                .cookie(new jakarta.servlet.http.Cookie("jwt", jwtCookie)))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.name", is("Institution Initiale")))
                .andExpect(jsonPath("$.town", is("Marseille")))
                .andExpect(jsonPath("$.contactMail", is("contact@initial.fr")))
                .andExpect(jsonPath("$.category", is("Collège")))
                .andExpect(jsonPath("$.studentsNumber", is(500)))
                .andExpect(jsonPath("$.socialStatus", is("Public")))
                .andExpect(jsonPath("$.institutionSpecifities", is("Spécialité initiale")))
                .andExpect(jsonPath("$.studentsSpecificities", is("Étudiants initiaux")))
                .andExpect(jsonPath("$.teachersSpecificities", is("Enseignants initiaux")));
    }

    @Test
    public void testGetInstitutionNotFound() throws Exception {
        mockMvc.perform(get("/institution/get/999")
                .cookie(new jakarta.servlet.http.Cookie("jwt", jwtCookie)))
                .andExpect(status().isNotFound());
    }

    @Test
    public void testUpdateInstitution() throws Exception {
        Institution savedInstitution = createInstitution();
        
        Institution updatedData = Institution.builder()
                .name("Institution Mise à Jour")
                .town("Toulouse")
                .contactMail("nouveau@institution.fr")
                .category("Lycée")
                .studentsNumber(800)
                .socialStatus("Privé")
                .institutionSpecifities("Nouvelle spécialité")
                .studentsSpecificities("Nouveaux étudiants")
                .teachersSpecificities("Nouveaux enseignants")
                .build();
        
        String updatedJson = objectMapper.writeValueAsString(updatedData);
        
        mockMvc.perform(put("/institution/update/" + savedInstitution.getId())
                .cookie(new jakarta.servlet.http.Cookie("jwt", jwtCookie))
                .contentType(MediaType.APPLICATION_JSON)
                .content(updatedJson))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.message", is("Institution bien mise à jour")));
        
        // Vérification dans la base de données
        Optional<Institution> updatedInstitution = institutionRepository.findById(savedInstitution.getId());
        
        assertTrue(updatedInstitution.isPresent(), "L'institution devrait être présente dans la base de données");
        assertEquals("Institution Mise à Jour", updatedInstitution.get().getName());
        assertEquals("Toulouse", updatedInstitution.get().getTown());
        assertEquals("nouveau@institution.fr", updatedInstitution.get().getContactMail());
        assertEquals("Lycée", updatedInstitution.get().getCategory());
        assertEquals(800, updatedInstitution.get().getStudentsNumber());
        assertEquals("Privé", updatedInstitution.get().getSocialStatus());
        assertEquals("Nouvelle spécialité", updatedInstitution.get().getInstitutionSpecifities());
        assertEquals("Nouveaux étudiants", updatedInstitution.get().getStudentsSpecificities());
        assertEquals("Nouveaux enseignants", updatedInstitution.get().getTeachersSpecificities());
    }

    @Test
    public void testUpdateInstitutionNotFound() throws Exception {
        Institution updatedData = Institution.builder()
                .name("Test")
                .build();
        
        String updatedJson = objectMapper.writeValueAsString(updatedData);
        
        mockMvc.perform(put("/institution/update/999")
                .cookie(new jakarta.servlet.http.Cookie("jwt", jwtCookie))
                .contentType(MediaType.APPLICATION_JSON)
                .content(updatedJson))
                .andExpect(status().isBadRequest())
                .andExpect(jsonPath("$.message", is("Institution non trouvée")));
    }

    @Test
    public void testGetAllInstitutions() throws Exception {
        // Création de plusieurs institutions
        Institution institution1 = Institution.builder()
                .name("Institution 1")
                .town("Paris")
                .contactMail("contact1@test.fr")
                .category("Collège")
                .studentsNumber(300)
                .socialStatus("Public")
                .institutionSpecifities("Spécialité 1")
                .studentsSpecificities("Étudiants 1")
                .teachersSpecificities("Enseignants 1")
                .build();
        Institution saved1 = institutionService.save(institution1);
        
        Institution institution2 = Institution.builder()
                .name("Institution 2")
                .town("Lyon")
                .contactMail("contact2@test.fr")
                .category("Lycée")
                .studentsNumber(600)
                .socialStatus("Privé")
                .institutionSpecifities("Spécialité 2")
                .studentsSpecificities("Étudiants 2")
                .teachersSpecificities("Enseignants 2")
                .build();
        Institution saved2 = institutionService.save(institution2);
        
        Institution institution3 = Institution.builder()
                .name("Institution 3")
                .town("Bordeaux")
                .contactMail("contact3@test.fr")
                .category("Université")
                .studentsNumber(1000)
                .socialStatus("Public")
                .institutionSpecifities("Spécialité 3")
                .studentsSpecificities("Étudiants 3")
                .teachersSpecificities("Enseignants 3")
                .build();
        Institution saved3 = institutionService.save(institution3);
        
        // Vérification de la récupération de toutes les institutions
        mockMvc.perform(get("/institution/getAll")
                .cookie(new jakarta.servlet.http.Cookie("jwt", jwtCookie)))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.institutions", hasSize(3)))
                .andExpect(jsonPath("$.institutions[0]." + saved1.getId(), is("Institution 1")))
                .andExpect(jsonPath("$.institutions[1]." + saved2.getId(), is("Institution 2")))
                .andExpect(jsonPath("$.institutions[2]." + saved3.getId(), is("Institution 3")));
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
