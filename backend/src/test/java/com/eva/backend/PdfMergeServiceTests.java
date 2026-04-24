package com.eva.backend;

import static org.assertj.core.api.Assertions.assertThat;

import java.io.ByteArrayOutputStream;
import java.nio.file.Files;
import java.nio.file.Path;
import java.util.Arrays;
import java.util.List;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.io.TempDir;
import org.apache.pdfbox.pdmodel.PDDocument;
import org.apache.pdfbox.pdmodel.PDPage;
import org.apache.pdfbox.pdmodel.PDPageContentStream;
import org.apache.pdfbox.pdmodel.font.PDType1Font;
import org.apache.pdfbox.text.PDFTextStripper;
import org.springframework.mock.web.MockMultipartFile;
import org.springframework.test.util.ReflectionTestUtils;
import org.springframework.web.multipart.MultipartFile;

import com.eva.backend.service.PdfMergeService;

public class PdfMergeServiceTests {

	private PdfMergeService pdfMergeService;

	@TempDir
	Path tempDir;

	@BeforeEach
	void setup() {
		pdfMergeService = new PdfMergeService();
		ReflectionTestUtils.setField(pdfMergeService, "pdfDir", tempDir.toString());
	}

	@Test
	void shouldMergeMultiplePdfsAndSaveFile() throws Exception {
		MultipartFile pdf1 = createTestPdf("Document 1");
		MultipartFile pdf2 = createTestPdf("Document 2");
		List<MultipartFile> pdfList = Arrays.asList(pdf1, pdf2);

		var mergedFile = pdfMergeService.merge(pdfList, "merged");

		// Vérifier que le fichier a été créé
		assertThat(mergedFile).isNotNull();
		assertThat(mergedFile.exists()).isTrue();
		assertThat(mergedFile.getName()).endsWith(".pdf");
		assertThat(mergedFile.length()).isGreaterThan(0);

		// Vérifier que le fichier contient le contenu des deux PDFs
		byte[] mergedBytes = Files.readAllBytes(mergedFile.toPath());
		assertThat(new String(mergedBytes, 0, 4)).isEqualTo("%PDF");

		try (PDDocument document = PDDocument.load(mergedBytes)) {
			String text = new PDFTextStripper().getText(document);
			assertThat(text).contains("Document 1");
			assertThat(text).contains("Document 2");
		}
	}

	private MultipartFile createTestPdf(String content) throws Exception {
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

		byte[] pdfBytes = outputStream.toByteArray();
		return new MockMultipartFile("file", "test.pdf", "application/pdf", pdfBytes);
	}
}
