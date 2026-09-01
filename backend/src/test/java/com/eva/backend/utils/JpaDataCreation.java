package com.eva.backend.utils;

import java.time.LocalDate;
import java.time.Year;
import java.util.Arrays;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;

import com.eva.backend.model.Evaluations;
import com.eva.backend.model.Experimentation;
import com.eva.backend.model.Institution;
import com.eva.backend.model.PedagogicalContext;
import com.eva.backend.model.User;
import com.eva.backend.model.UserAdditionalData;
import com.eva.backend.repository.ExperimentationRepository;
import com.eva.backend.repository.InstitutionRepository;
import com.eva.backend.repository.UserRepository;

@Component
public class JpaDataCreation {
    @Autowired
    private UserRepository userRepository;

    @Autowired
    private InstitutionRepository institutionRepository;

    @Autowired
    private ExperimentationRepository experimentationRepository;

    public User createAUser(){
        return userRepository.save(User.builder()
		.firstname("Marie")
		.lastname("Tremblay")
		.mail("marie.tremblay@mail.com")
		.password("MarieT123!")
		.emailVerified(true)
		.additionalData(new UserAdditionalData(
			false,
			true,
			Year.of(1990),
			"Femme",
			"Enseignante",
			"Mathématiques",
			"Géométrie",
			"Bienveillant",
			"Passionnée par l'enseignement"))
		.build());
    }

    public Institution createAnInstitution(){
        return institutionRepository.save(Institution.builder()
            .name("Institution Test")
            .town("Marseille")
            .contactMail("contact@test.fr")
            .category("Collège")
            .studentsNumber(500)
            .socialStatus("Public")
            .institutionSpecifities("Spécialité test")
            .studentsSpecificities("Étudiants test")
            .teachersSpecificities("Enseignants test")
            .build());
    }

    public Long createAnExperimentation(User user, Institution institution) {
        Evaluations oldPedagogyEvaluations = Evaluations.builder()
            .pedagogyBeginning(LocalDate.of(2025, 9, 1))
            .pedagogyEnding(LocalDate.of(2025, 12, 15))
            .initialEvaluation(LocalDate.of(2026, 1, 15))
            .immediateEvaluation(LocalDate.of(2026, 2, 15))
            .delayedEvaluation(LocalDate.of(2026, 3, 15))
            .accountedEvaluation(LocalDate.of(2026, 4, 15))
            .build();

        Evaluations newPedagogyEvaluations = Evaluations.builder()
            .pedagogyBeginning(LocalDate.of(2026, 1, 1))
            .pedagogyEnding(LocalDate.of(2026, 4, 30))
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
            .experimentationTitle("Expérimentation maths collège")
            .keywords(Arrays.asList("mathématiques", "apprentissage actif", "collège"))
            .personalKeywords("motivation, collaboration")
            .protocol("Protocole 1")
            .isSharingData(true)
            .dataPath("")
            .pedagogicalContext(pedagogicalContext)
            .user(user)
            .institution(institution)
            .build();

        return experimentationRepository.save(experimentation).getId();
    }
}
