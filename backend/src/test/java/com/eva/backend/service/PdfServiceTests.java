package com.eva.backend.service;

import static org.assertj.core.api.Assertions.assertThat;

import java.nio.file.Files;
import java.nio.file.Path;
import java.util.Map;

import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.io.TempDir;
import org.apache.pdfbox.pdmodel.PDDocument;
import org.apache.pdfbox.text.PDFTextStripper;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.orm.jpa.DataJpaTest;
import org.springframework.boot.test.context.TestConfiguration;
import org.springframework.context.ApplicationContext;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Import;
import org.springframework.test.context.ActiveProfiles;
import org.thymeleaf.TemplateEngine;
import org.thymeleaf.templatemode.TemplateMode;
import org.thymeleaf.spring6.SpringTemplateEngine;
import org.thymeleaf.spring6.templateresolver.SpringResourceTemplateResolver;

import com.eva.backend.model.Institution;
import com.eva.backend.model.User;
import com.eva.backend.service.DataExtractionService;
import com.eva.backend.service.PdfGenerationServiceViaHtml;
import com.eva.backend.utils.JpaDataCreation;

@DataJpaTest
@Import({JpaDataCreation.class, DataExtractionService.class, PdfGenerationServiceViaHtml.class, PdfServiceTests.ThymeleafTestConfig.class})
@ActiveProfiles("test")
public class PdfServiceTests {
	@Autowired
	private JpaDataCreation dataCreator;

	@Autowired
	private DataExtractionService dataExtractionService;

	@Autowired
	private PdfGenerationServiceViaHtml pdfGenerationService;

	@TempDir
	Path tempDir;

	@Test
	void shouldCreatePdfFromExperimentationData() throws Exception {
		User user = dataCreator.createAUser();
		Institution institution = dataCreator.createAnInstitution();
		Long experimentationId = dataCreator.createAnExperimentation(user, institution);

		Map<String, Map<String, Object>> experimentationData = dataExtractionService.extractExperimentationData(experimentationId);
		byte[] pdfBytes = pdfGenerationService.createPdf(experimentationData);

		Path generatedPdf = tempDir.resolve("experimentation.pdf");
		Files.write(generatedPdf, pdfBytes);

		assertThat(Files.exists(generatedPdf)).isTrue();
		assertThat(Files.size(generatedPdf)).isGreaterThan(0);
		assertThat(new String(pdfBytes, 0, 4)).isEqualTo("%PDF");

		try (PDDocument document = PDDocument.load(pdfBytes)) {
			String text = new PDFTextStripper().getText(document);
			assertThat(text).contains("Données de l'expérimentation");
			assertThat(text).contains("Institution Test");
			assertThat(text).contains("contact@test.fr");
			assertThat(text).contains("Protocole 1");
			assertThat(text).contains("Algèbre et géométrie");
			assertThat(text).contains("24");
		}
	}

	@TestConfiguration
	static class ThymeleafTestConfig {
		@Bean
		TemplateEngine templateEngine(SpringResourceTemplateResolver templateResolver) {
			SpringTemplateEngine templateEngine = new SpringTemplateEngine();
			templateEngine.setTemplateResolver(templateResolver);
			return templateEngine;
		}

		@Bean
		SpringResourceTemplateResolver templateResolver(ApplicationContext applicationContext) {
			SpringResourceTemplateResolver resolver = new SpringResourceTemplateResolver();
			resolver.setApplicationContext(applicationContext);
			resolver.setPrefix("classpath:/templates/");
			resolver.setSuffix(".html");
			resolver.setTemplateMode(TemplateMode.HTML);
			resolver.setCharacterEncoding("UTF-8");
			resolver.setCacheable(false);
			return resolver;
		}
	}
}
