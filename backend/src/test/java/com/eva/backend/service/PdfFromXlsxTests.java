package com.eva.backend.service;

import static org.assertj.core.api.Assertions.assertThat;

import java.io.ByteArrayOutputStream;
import java.io.IOException;
import java.io.OutputStream;
import java.nio.file.Files;
import java.nio.file.Path;

import org.apache.pdfbox.pdmodel.PDDocument;
import org.apache.pdfbox.pdmodel.PDPage;
import org.apache.pdfbox.pdmodel.PDPageContentStream;
import org.apache.pdfbox.pdmodel.font.PDType1Font;
import org.apache.pdfbox.text.PDFTextStripper;
import org.apache.poi.ss.usermodel.Row;
import org.apache.poi.ss.usermodel.Sheet;
import org.apache.poi.ss.usermodel.Workbook;
import org.apache.poi.ss.usermodel.WorkbookFactory;
import org.apache.poi.xssf.usermodel.XSSFWorkbook;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.io.TempDir;
import org.springframework.test.util.ReflectionTestUtils;

public class PdfFromXlsxTests {
	private FakeConverterPdfFromXlsx pdfFromXlsx;

	@TempDir
	Path tempDir;

	@BeforeEach
	void setUp() {
		pdfFromXlsx = new FakeConverterPdfFromXlsx();
		ReflectionTestUtils.setField(pdfFromXlsx, "xlsDirectory", tempDir.toString());
	}

	@Test
	void shouldConvertOnlyRequestedTabIntoPdf() throws Exception {
		Path xlsxFile = createXlsxFile();
		byte[] pdfBytes = pdfFromXlsx.convertTabInPdf("test.xlsx", "TargetTab");

		assertThat(pdfBytes).isNotNull();
		assertThat(pdfBytes.length).isGreaterThan(0);
		assertThat(new String(pdfBytes, 0, 4)).isEqualTo("%PDF");

		try (PDDocument document = PDDocument.load(pdfBytes)) {
			String text = new PDFTextStripper().getText(document);
			assertThat(text).contains("CONTENU_CIBLE");
			assertThat(text).doesNotContain("NE_DOIT_PAS_APPARAITRE");
		}

		assertThat(Files.exists(xlsxFile)).isTrue();
	}

    private Path createXlsxFile() throws IOException{
        Path xlsxFile = tempDir.resolve("test.xlsx");
		try (Workbook workbook = new XSSFWorkbook();
			 OutputStream outputStream = Files.newOutputStream(xlsxFile)) {
			Sheet ignoredSheet = workbook.createSheet("IgnoredTab");
			Row ignoredRow = ignoredSheet.createRow(0);
			ignoredRow.createCell(0).setCellValue("NE_DOIT_PAS_APPARAITRE");

			Sheet targetSheet = workbook.createSheet("TargetTab");
			Row targetRow = targetSheet.createRow(0);
			targetRow.createCell(0).setCellValue("CONTENU_CIBLE");

			workbook.write(outputStream);
		}
        return xlsxFile;
    }

	private static class FakeConverterPdfFromXlsx extends PdfFromXlsx {
        /* Mock de la fonction convertInput pour éviter de dépendre d'un Libre office local */
		@Override
		protected byte[] convertInput(Path inputToConvert, String extension) throws java.io.IOException {
			try (Workbook workbook = WorkbookFactory.create(Files.newInputStream(inputToConvert));
				 ByteArrayOutputStream outputStream = new ByteArrayOutputStream();
				 PDDocument document = new PDDocument()) {
				String value = workbook.getSheetAt(0).getRow(0).getCell(0).getStringCellValue();

				PDPage page = new PDPage();
				document.addPage(page);
				try (PDPageContentStream contentStream = new PDPageContentStream(document, page)) {
					contentStream.beginText();
					contentStream.setFont(PDType1Font.HELVETICA, 12);
					contentStream.newLineAtOffset(100, 700);
					contentStream.showText(value);
					contentStream.endText();
				}

				document.save(outputStream);
				return outputStream.toByteArray();
			}
		}
	}
}
