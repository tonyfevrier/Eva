package com.eva.backend.service;

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

import com.eva.backend.service.PdfMergeService;

public class PdfMergeServiceTests {

	private PdfMergeService pdfMergeService;

	@TempDir
	Path tempDir;

	@BeforeEach
	void setup() {
		pdfMergeService = new PdfMergeService();
	}

	@Test
	void shouldMergeTwoPdfByteArraysInMemory() throws Exception {
		byte[] firstPdf = createTestPdfBytes("Document A");
		byte[] secondPdf = createTestPdfBytes("Document B");

		byte[] mergedBytes = pdfMergeService.merge(firstPdf, secondPdf);

		assertThat(mergedBytes).isNotNull();
		assertThat(mergedBytes.length).isGreaterThan(0);
		assertThat(new String(mergedBytes, 0, 4)).isEqualTo("%PDF");

		try (PDDocument document = PDDocument.load(mergedBytes)) {
			assertThat(document.getNumberOfPages()).isEqualTo(2);
			String text = new PDFTextStripper().getText(document);
			assertThat(text).contains("Document A");
			assertThat(text).contains("Document B");
		}
	}

	@Test
	void shouldMergeMultipleStoredPdfsInMemory() throws Exception {
		Path sourceDirectory = tempDir.resolve("source");
		Files.createDirectories(sourceDirectory);

		createTestPdfFile(sourceDirectory, "doc1.pdf", "Document 1");
		createTestPdfFile(sourceDirectory, "doc2.pdf", "Document 2");
		List<String> pdfList = Arrays.asList("doc1.pdf", "doc2.pdf");

		byte[] mergedBytes = pdfMergeService.mergeFilesFromDirectory(sourceDirectory, pdfList);

		assertThat(mergedBytes).isNotNull();
		assertThat(mergedBytes.length).isGreaterThan(0);
		assertThat(new String(mergedBytes, 0, 4)).isEqualTo("%PDF");

		try (PDDocument document = PDDocument.load(mergedBytes)) {
			assertThat(document.getNumberOfPages()).isEqualTo(2);
			String text = new PDFTextStripper().getText(document);
			assertThat(text).contains("Document 1");
			assertThat(text).contains("Document 2");
		}
	}

	private byte[] createTestPdfBytes(String content) throws Exception {
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

	private void createTestPdfFile(Path directory, String fileName, String content) throws Exception {
		Files.write(directory.resolve(fileName), createTestPdfBytes(content));
	}
}
