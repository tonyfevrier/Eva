package com.eva.backend.controller;

import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.post;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.multipart;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.content;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.header;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.jsonPath;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;
import static org.hamcrest.Matchers.is;

import java.io.IOException;
import java.nio.charset.StandardCharsets;
import java.nio.file.Files;
import java.nio.file.Path;
import java.util.Optional;
import java.util.stream.Stream;

import org.junit.jupiter.api.AfterEach;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.AutoConfigureMockMvc;
import org.springframework.boot.test.context.SpringBootTest;
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
		"app.xls-data-model-dir=target/test-exports",
		"app.xls-data-dir=target/test-imports",
		"app.pdf-dir=target/test-imports",
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

	@AfterEach
	void cleanTestDirectories() throws Exception {
		cleanDirectory(Path.of("target", "test-exports"));
		cleanDirectory(Path.of("target", "test-imports"));
	}

	private void cleanDirectory(Path directory) throws IOException {
		if (!Files.isDirectory(directory)) {
			return;
		}

		try (Stream<Path> files = Files.list(directory)) {
			files.filter(Files::isRegularFile)
					.forEach(path -> {
						try {
							Files.deleteIfExists(path);
						} catch (IOException e) {
							throw new RuntimeException(e);
						}
					});
		}
	}

	@Test
	void exportFileShouldDownloadXlsxFile() throws Exception {
		mockMvc.perform(post("/file/export")
						.param("entry", "xlsx")
						.param("exportType", "format"))
				.andExpect(status().isOk())
				.andExpect(header().string("Content-Disposition", "attachment; filename=\"ResultatsEVA_v2_Excel.xlsx\""))
				.andExpect(content().contentType("application/vnd.openxmlformats-officedocument.spreadsheetml.sheet"))
				.andExpect(content().bytes(XLSX_FILE_CONTENT));
	}

	@Test
	void exportFileShouldDownloadXlsFile() throws Exception {
		mockMvc.perform(post("/file/export")
						.param("entry", "xls")
						.param("exportType", "format"))
				.andExpect(status().isOk())
				.andExpect(header().string("Content-Disposition", "attachment; filename=\"ResultatsEVA_v2_Excel97-2003.xls\""))
				.andExpect(content().contentType("application/vnd.ms-excel"))
				.andExpect(content().bytes(XLS_FILE_CONTENT));
	}

	@Test
	void exportFileShouldDownloadOdsFile() throws Exception {
		mockMvc.perform(post("/file/export")
						.param("entry", "ods")
						.param("exportType", "format"))
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

		String expectedFileName = experimentationId + "_Protocole" + "_InstitutionTest_Doe_John_Math.xlsx";
		Path importDir = Path.of("target", "test-imports");
		Path existingFile = importDir.resolve(expectedFileName);
		Files.write(existingFile, "old-content".getBytes(StandardCharsets.UTF_8));

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
		Path importDir = Path.of("target", "test-imports");
		Path existingSpreadsheetFile = importDir.resolve("1_previous.xlsx");
		Files.write(existingSpreadsheetFile, XLSX_FILE_CONTENT);

		MockMultipartFile file = new MockMultipartFile(
				"file",
				"input.pdf",
				"application/pdf",
				PDF_FILE_CONTENT
		);

		mockMvc.perform(multipart("/file/import")
				.file(file)
				.param("id", experimentationId)
				.param("importType", "test"))
				.andExpect(status().isOk())
				.andExpect(content().string("File uploaded successfully"));

		Path savedFile = Path.of("target","test-imports", expectedFileName);
		
		byte[] savedBytes = Files.readAllBytes(savedFile);
		org.junit.jupiter.api.Assertions.assertArrayEquals(PDF_FILE_CONTENT, savedBytes);
		org.junit.jupiter.api.Assertions.assertFalse(Files.exists(existingSpreadsheetFile));
	} 
    
	@Test
	void importFileShouldUploadPdfQuestionnaireFileFromClient() throws Exception {
		String experimentationId = "1";
		String expectedFileName = "questionnaire_id1_1.pdf";
		Path importDir = Path.of("target", "test-imports");
		Path existingSpreadsheetFile = importDir.resolve("1_previous.xlsx");
		Files.write(existingSpreadsheetFile, XLSX_FILE_CONTENT);

		MockMultipartFile file = new MockMultipartFile(
				"file",
				"input.pdf",
				"application/pdf",
				PDF_FILE_CONTENT
		);

		mockMvc.perform(multipart("/file/import")
				.file(file)
				.param("id", experimentationId)
				.param("importType", "questionnaire"))
				.andExpect(status().isOk())
				.andExpect(content().string("File uploaded successfully"));

		Path savedFile = Path.of("target","test-imports", expectedFileName);
		
		byte[] savedBytes = Files.readAllBytes(savedFile);
		org.junit.jupiter.api.Assertions.assertArrayEquals(PDF_FILE_CONTENT, savedBytes);
		org.junit.jupiter.api.Assertions.assertFalse(Files.exists(existingSpreadsheetFile));
	} 

	@Test
	void getFileNamesShouldReturnBadRequestWhenImportTypeIsMissing() throws Exception {
		mockMvc.perform(post("/file/getFileNames/1"))
				.andExpect(status().isBadRequest());
	}

	@Test
	void getFileNamesShouldReturnOnlyNamesContainingImportType() throws Exception {
		Path importDir = Path.of("target", "test-imports");
		Files.createDirectories(importDir);

		Files.write(importDir.resolve("test_id1_1.pdf"), PDF_FILE_CONTENT);
		Files.write(importDir.resolve("test_id2_1.pdf"), PDF_FILE_CONTENT);
		Files.write(importDir.resolve("test_id1_2.pdf"), PDF_FILE_CONTENT);
		Files.write(importDir.resolve("questionnaire_id1_1.pdf"), PDF_FILE_CONTENT);

		mockMvc.perform(post("/file/getFileNames/1")
						.param("importType", "test")
						.param("id", "1"))
						.andExpect(status().isOk())
						.andExpect(jsonPath("$.fileNames[0]", is("test_id1_1.pdf")))
						.andExpect(jsonPath("$.fileNames[1]", is("test_id1_2.pdf"))); 

		mockMvc.perform(post("/file/getFileNames/1")
						.param("importType", "questionnaire")
						.param("id", "1"))
						.andExpect(status().isOk())
						.andExpect(jsonPath("$.fileNames[0]", is("questionnaire_id1_1.pdf")));
	}

	@Test
	void deleteFilesShouldDeleteProvidedFileNames() throws Exception {
		Path importDir = Path.of("target", "test-imports");
		Files.createDirectories(importDir);

		Path firstFile = importDir.resolve("test_id1_1.pdf");
		Path secondFile = importDir.resolve("questionnaire_id1_1.pdf");
		Files.write(firstFile, PDF_FILE_CONTENT);
		Files.write(secondFile, PDF_FILE_CONTENT);

		mockMvc.perform(post("/file/delete")
				.param("fileNames", "test_id1_1.pdf")
				.param("fileNames", "questionnaire_id1_1.pdf"))
				.andExpect(status().isOk())
				.andExpect(content().string("Files are deleted"));

		org.junit.jupiter.api.Assertions.assertFalse(Files.exists(firstFile));
		org.junit.jupiter.api.Assertions.assertFalse(Files.exists(secondFile));
	}
}
