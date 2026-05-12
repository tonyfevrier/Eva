package com.eva.backend.controller;

import static org.assertj.core.api.Assertions.assertThat;
import static org.mockito.Mockito.any;
import static org.mockito.Mockito.mock;
import static org.mockito.Mockito.times;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.get;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;

import java.io.ByteArrayOutputStream;
import java.nio.file.Files;
import java.nio.file.Path;
import java.util.List;
import java.util.Map;
import java.util.Optional;

import org.apache.pdfbox.pdmodel.PDDocument;
import org.apache.pdfbox.pdmodel.PDPage;
import org.apache.pdfbox.pdmodel.PDPageContentStream;
import org.apache.pdfbox.pdmodel.font.PDType1Font;
import org.apache.pdfbox.text.PDFTextStripper;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.io.TempDir;
import org.springframework.test.web.servlet.MockMvc;
import org.springframework.test.web.servlet.MvcResult;
import org.springframework.test.web.servlet.setup.MockMvcBuilders;
import org.springframework.test.util.ReflectionTestUtils;

import com.eva.backend.records.DataForHtml;
import com.eva.backend.model.Experimentation;
import com.eva.backend.service.DataExtractionService;
import com.eva.backend.service.ExperimentationService;
import com.eva.backend.service.FileService;
import com.eva.backend.service.PdfFromSpreadSheet;
import com.eva.backend.service.PdfGenerationServiceViaHtml;
import com.eva.backend.service.PdfMergeService;

class PdfControllerTests {

	private DataExtractionService dataExtractionService;
	private ExperimentationService experimentationService;
	private PdfGenerationServiceViaHtml pdfGenerationService;
	private PdfFromSpreadSheet pdfFromXlsx;
	private PdfController pdfController;
	private MockMvc mockMvc;

	@TempDir
	Path tempDir;

	@TempDir
	Path xlsDataDir;

	@BeforeEach
	void setUp() {
		dataExtractionService = mock(DataExtractionService.class);
		experimentationService = mock(ExperimentationService.class);
		pdfGenerationService = mock(PdfGenerationServiceViaHtml.class);
		pdfFromXlsx = mock(PdfFromSpreadSheet.class);

		pdfController = new PdfController();
		ReflectionTestUtils.setField(pdfController, "pdfDir", tempDir.toString());
		ReflectionTestUtils.setField(pdfController, "generatedPdfDir", tempDir.toString());
		ReflectionTestUtils.setField(pdfController, "xlsDataDir", xlsDataDir.toString());
		ReflectionTestUtils.setField(pdfController, "dataExtractor", dataExtractionService);
		ReflectionTestUtils.setField(pdfController, "experimentationService", experimentationService);
		ReflectionTestUtils.setField(pdfController, "pdfService", pdfGenerationService);
		ReflectionTestUtils.setField(pdfController, "mergeService", new PdfMergeService());
		ReflectionTestUtils.setField(pdfController, "fileService", new FileService(List.of(), List.of()));
		ReflectionTestUtils.setField(pdfController, "pdfXlsxService", pdfFromXlsx);
		mockMvc = MockMvcBuilders.standaloneSetup(pdfController).build();
	}

	@Test
	void shouldLaunchGetRequestGenerateSaveAndReturnMergedPdf() throws Exception {
		Long experimentationId = 42L;
		String experimentationText = "Données de l'expérimentation du contrôleur";
		String interpretationText = "Données d'interprétation du contrôleur";
		String mergedFileText = "Document de test merge";
		String xlsxTabsText = "Contenu des onglets XLSX";
		Map<String, Map<String, Object>> extractedData = Map.of(
				"Informations générales", Map.of("institution", "Institution Test", "contact", "contact@test.fr"));
		Map<String, Object> interpretationData = Map.of(
				"1", Map.of("content", "Analyse positive", "name", "Marie Tremblay"));

		byte[] convertedXlsxPdf = createPdfBytes("PDF XLSX complet");
		byte[] lastFivePagesPdf = createPdfBytes(xlsxTabsText);
		Experimentation experimentation = new Experimentation();

		when(dataExtractionService.extractExperimentationData(experimentationId)).thenReturn(extractedData);
		when(dataExtractionService.extractInterpretationsData(experimentationId)).thenReturn(interpretationData);
		when(experimentationService.findById(experimentationId)).thenReturn(Optional.of(experimentation));
		// Mock les deux appels à createPdf : experimentation puis interpretation
		byte[] experimentationPdf = createPdfBytes(experimentationText);
		byte[] interpretationPdf = createPdfBytes(interpretationText);
		when(pdfGenerationService.createPdf(any(DataForHtml.class)))
			.thenReturn(experimentationPdf)
			.thenReturn(interpretationPdf);
		when(pdfFromXlsx.convertTabsInPdf(xlsDataDir, "42_resultats.xlsx")).thenReturn(convertedXlsxPdf);
		when(pdfFromXlsx.keepOnlyLastSheets(convertedXlsxPdf, 5)).thenReturn(lastFivePagesPdf);

		Files.write(tempDir.resolve("tests_id42_1.pdf"), createPdfBytes(mergedFileText));
		Files.write(xlsDataDir.resolve("42_resultats.xlsx"), "xlsx placeholder".getBytes());

		MvcResult mvcResult = mockMvc.perform(get("/pdf/generate/{id}", experimentationId))
				.andExpect(status().isOk())
				.andReturn();

		// Vérifier qu'un pdf est enregistré et que son contenu est identique à la réponse binaire
		byte[] generatedPdf = mvcResult.getResponse().getContentAsByteArray();
		Path savedFile = tempDir.resolve("experimentation_summary_42.pdf");

		assertThat(generatedPdf).isNotEmpty();
		assertThat(new String(generatedPdf, 0, 4)).isEqualTo("%PDF");
		assertThat(savedFile).exists();
		assertThat(Files.readAllBytes(savedFile)).isEqualTo(generatedPdf);
		assertThat(experimentation.getDataPath()).isEqualTo(savedFile.toString());

        // Vérification la présence de contenus spécifiques
		try (PDDocument document = PDDocument.load(generatedPdf)) {
			// 4 pages : experimentation + tests + xlsx + interpretation
			assertThat(document.getNumberOfPages()).isEqualTo(4);
			String text = new PDFTextStripper().getText(document);
			assertThat(text).contains(experimentationText);
			assertThat(text).contains(mergedFileText);
			assertThat(text).contains(xlsxTabsText);
			// Vérifier que la nouvelle page d'interprétation a bien été créée et mergée
			assertThat(text).contains(interpretationText);
		}

        // Vérifie que ces fonctions ont bien été appelés via les mocks
		verify(dataExtractionService).extractExperimentationData(experimentationId);
		verify(dataExtractionService).extractInterpretationsData(experimentationId);
		verify(pdfGenerationService, times(2)).createPdf(any(DataForHtml.class));
		verify(pdfFromXlsx).convertTabsInPdf(xlsDataDir, "42_resultats.xlsx");
		verify(pdfFromXlsx).keepOnlyLastSheets(convertedXlsxPdf, 5);
		verify(experimentationService).save(experimentation);
	}

	private byte[] createPdfBytes(String content) throws Exception {
		ByteArrayOutputStream outputStream = new ByteArrayOutputStream();

		try (PDDocument document = new PDDocument()) {
			PDPage page = new PDPage();
			document.addPage(page);

			try (PDPageContentStream contentStream = new PDPageContentStream(document, page)) {
				contentStream.beginText();
				contentStream.setFont(PDType1Font.HELVETICA, 12);
				contentStream.newLineAtOffset(100, 700);
				contentStream.showText(content);
				contentStream.endText();
			}

			document.save(outputStream);
		}

		return outputStream.toByteArray();
	}
}