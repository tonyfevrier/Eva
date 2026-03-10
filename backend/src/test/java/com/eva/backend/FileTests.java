package com.eva.backend;

import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.post;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.content;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.header;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;

import java.nio.charset.StandardCharsets;
import java.nio.file.Files;
import java.nio.file.Path;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.AutoConfigureMockMvc;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.http.MediaType;
import org.springframework.test.context.ActiveProfiles;
import org.springframework.test.context.TestPropertySource;
import org.springframework.test.web.servlet.MockMvc;

@SpringBootTest
@AutoConfigureMockMvc(addFilters = false)
@ActiveProfiles("test")
@TestPropertySource(properties = "app.export-dir=target/test-exports")
public class FileTests {

	@Autowired
	private MockMvc mockMvc;

	private static final String XLSX_FILE_NAME = "ResultatsEVA_v2_Excel.xlsx";
	private static final String XLS_FILE_NAME = "ResultatsEVA_v2_Excel97-2003.xls";
	private static final String ODS_FILE_NAME = "ResultatsEVA_v2_LibreOffice.ods";
	private static final byte[] XLSX_FILE_CONTENT = "test-file-content-xlsx".getBytes(StandardCharsets.UTF_8);
	private static final byte[] XLS_FILE_CONTENT = "test-file-content-xls".getBytes(StandardCharsets.UTF_8);
	private static final byte[] ODS_FILE_CONTENT = "test-file-content-ods".getBytes(StandardCharsets.UTF_8);

	@BeforeEach
	void prepareExportFile() throws Exception {
		Path exportDir = Path.of("target", "test-exports");
		Files.createDirectories(exportDir);
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
    
}
