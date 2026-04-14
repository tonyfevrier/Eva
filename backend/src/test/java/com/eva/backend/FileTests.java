package com.eva.backend;

import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.post;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.multipart;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.content;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.header;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;

import java.nio.charset.StandardCharsets;
import java.nio.file.Files;
import java.nio.file.Path;
import java.time.LocalDate;
import java.util.Optional;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.AutoConfigureMockMvc;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.http.MediaType;
import org.springframework.mock.web.MockMultipartFile;
import org.springframework.test.context.ActiveProfiles;
import org.springframework.test.context.TestPropertySource;
import org.springframework.test.context.bean.override.mockito.MockitoBean;
import org.springframework.test.web.servlet.MockMvc;

import static org.mockito.Mockito.when;

import com.eva.backend.model.Experimentation;
import com.eva.backend.model.Institution;
import com.eva.backend.model.PedagogicalContext;
import com.eva.backend.model.User;
import com.eva.backend.repository.ExperimentationRepository;

@SpringBootTest
@AutoConfigureMockMvc(addFilters = false)
@ActiveProfiles("test")
@TestPropertySource(properties = {
		"app.export-dir=target/test-exports",
		"app.import-dir.xls=target/test-imports",
		"app.import-dir.pdf=target/test-imports"
})
public class FileTests {
	/* Tests pour l'export et l'import des fichiers xls de données */
	@Autowired
	private MockMvc mockMvc;

	@MockitoBean
	private ExperimentationRepository experimentationRepository;

	private static final String XLSX_FILE_NAME = "ResultatsEVA_v2_Excel.xlsx";
	private static final String XLS_FILE_NAME = "ResultatsEVA_v2_Excel97-2003.xls";
	private static final String ODS_FILE_NAME = "ResultatsEVA_v2_LibreOffice.ods";
	private static final byte[] XLSX_FILE_CONTENT = "test-file-content-xlsx".getBytes(StandardCharsets.UTF_8);
	private static final byte[] XLS_FILE_CONTENT = "test-file-content-xls".getBytes(StandardCharsets.UTF_8);
	private static final byte[] ODS_FILE_CONTENT = "test-file-content-ods".getBytes(StandardCharsets.UTF_8);
	private static final byte[] PDF_FILE_CONTENT = "test-file-content-pdf".getBytes(StandardCharsets.UTF_8);

	@BeforeEach
	void prepareExportFile() throws Exception {
		Path exportDir = Path.of("target", "test-exports");
		Path importDir = Path.of("target", "test-imports");
		Files.createDirectories(exportDir);
		Files.createDirectories(importDir);
		Files.write(exportDir.resolve(XLSX_FILE_NAME), XLSX_FILE_CONTENT);
		Files.write(exportDir.resolve(XLS_FILE_NAME), XLS_FILE_CONTENT);
		Files.write(exportDir.resolve(ODS_FILE_NAME), ODS_FILE_CONTENT);
	}

	@Test
	void exportFileShouldDownloadXlsxFile() throws Exception {
		mockMvc.perform(post("/file/export")
						.contentType(MediaType.APPLICATION_JSON)
						.content("{\"format\":\"xlsx\"}"))
				.andExpect(status().isOk())
				.andExpect(header().string("Content-Disposition", "attachment; filename=\"ResultatsEVA_v2_Excel.xlsx\""))
				.andExpect(content().contentType("application/vnd.openxmlformats-officedocument.spreadsheetml.sheet"))
				.andExpect(content().bytes(XLSX_FILE_CONTENT));
	}

	@Test
	void exportFileShouldDownloadXlsFile() throws Exception {
		mockMvc.perform(post("/file/export")
						.contentType(MediaType.APPLICATION_JSON)
						.content("{\"format\":\"xls\"}"))
				.andExpect(status().isOk())
				.andExpect(header().string("Content-Disposition", "attachment; filename=\"ResultatsEVA_v2_Excel97-2003.xls\""))
				.andExpect(content().contentType("application/vnd.ms-excel"))
				.andExpect(content().bytes(XLS_FILE_CONTENT));
	}

	@Test
	void exportFileShouldDownloadOdsFile() throws Exception {
		mockMvc.perform(post("/file/export")
						.contentType(MediaType.APPLICATION_JSON)
						.content("{\"format\":\"ods\"}"))
				.andExpect(status().isOk())
				.andExpect(header().string("Content-Disposition", "attachment; filename=\"ResultatsEVA_v2_LibreOffice.ods\""))
				.andExpect(content().contentType("application/vnd.oasis.opendocument.spreadsheet"))
				.andExpect(content().bytes(ODS_FILE_CONTENT));
	}

	@Test
	void importFileShouldUploadXlsxFileFromClient() throws Exception {
		Long experimentationId = 42L;
		Experimentation experimentation = Experimentation.builder()
				.id(experimentationId)
				.protocol("Protocole")
				.institution(Institution.builder().name("InstitutionTest").build())
				.user(User.builder().lastname("Doe").firstname("John").build())
				.pedagogicalContext(PedagogicalContext.builder().studyField("Math").build())
				.build();
		when(experimentationRepository.findById(experimentationId)).thenReturn(Optional.of(experimentation));

		String expectedFileName = "Protocole_"+ LocalDate.now()+ "_InstitutionTest_Doe_John_Math.xlsx";

		MockMultipartFile file = new MockMultipartFile(
				"file",
				"input.xlsx",
				"application/vnd.openxmlformats-officedocument.spreadsheetml.sheet",
				XLSX_FILE_CONTENT
		);

		mockMvc.perform(multipart("/file/import")
				.file(file)
				.param("id", experimentationId.toString())
				.param("importType", "xls"))
				.andExpect(status().isOk())
				.andExpect(content().string("File uploaded successfully"));

		Path savedFile = Path.of("target","test-imports", expectedFileName);

		byte[] savedBytes = Files.readAllBytes(savedFile);
		org.junit.jupiter.api.Assertions.assertArrayEquals(XLSX_FILE_CONTENT, savedBytes);
	}

	@Test
	void importFileShouldRejectUnsupportedExtension() throws Exception {
		MockMultipartFile file = new MockMultipartFile(
				"file",
				"input.csv",
				"text/csv",
				"a,b,c".getBytes(StandardCharsets.UTF_8)
		);

		mockMvc.perform(multipart("/file/import").file(file))
				.andExpect(status().isBadRequest());
	}

	@Test
	void importFileShouldUploadPdfTestFileFromClient() throws Exception {
		String experimentationId = "1";
		String expectedFileName = "test_id1_1.pdf";

		MockMultipartFile file = new MockMultipartFile(
				"file",
				"input.pdf",
				"application/pdf",
				PDF_FILE_CONTENT
		);

		mockMvc.perform(multipart("/file/import")
				.file(file)
				.param("id", experimentationId)
				.param("importType", "pdfTest"))
				.andExpect(status().isOk())
				.andExpect(content().string("File uploaded successfully"));

		Path savedFile = Path.of("target","test-imports", expectedFileName);
		
		byte[] savedBytes = Files.readAllBytes(savedFile);
		org.junit.jupiter.api.Assertions.assertArrayEquals(PDF_FILE_CONTENT, savedBytes);
	} 
    
	@Test
	void importFileShouldUploadPdfQuestionnaireFileFromClient() throws Exception {
		String experimentationId = "1";
		String expectedFileName = "questionnaire_id1_1.pdf";

		MockMultipartFile file = new MockMultipartFile(
				"file",
				"input.pdf",
				"application/pdf",
				PDF_FILE_CONTENT
		);

		mockMvc.perform(multipart("/file/import")
				.file(file)
				.param("id", experimentationId)
				.param("importType", "pdfQuestionnaire"))
				.andExpect(status().isOk())
				.andExpect(content().string("File uploaded successfully"));

		Path savedFile = Path.of("target","test-imports", expectedFileName);
		
		byte[] savedBytes = Files.readAllBytes(savedFile);
		org.junit.jupiter.api.Assertions.assertArrayEquals(PDF_FILE_CONTENT, savedBytes);
	} 
}
