package com.eva.backend.service;

import static org.assertj.core.api.Assertions.assertThat;

import java.io.ByteArrayOutputStream;
import java.io.IOException;
import java.io.OutputStream;
import java.nio.file.Files;
import java.nio.file.Path;
import java.util.ArrayList;
import java.util.List;

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
import org.odftoolkit.simple.SpreadsheetDocument;
import org.odftoolkit.simple.table.Table;

import com.eva.backend.utils.spreadSheetInterfaces.SpreadSheetRender;

public class PdfFromSpreadSheetTests {
    private FakeConverterPdfFromSpreadSheet pdfFromSpreadSheet;

    @TempDir
    Path tempDir;
    

    @BeforeEach
    void setUp() {
        pdfFromSpreadSheet = new FakeConverterPdfFromSpreadSheet();
    }

    @Test
    void shouldConvertWholeXlsxIntoPdf() throws Exception {
        Path xlsxFile = createXlsxFile();
        byte[] pdfBytes = pdfFromSpreadSheet.convertTabsInPdf(tempDir, "test.xlsx");

        assertThat(pdfBytes).isNotNull();
        assertThat(pdfBytes.length).isGreaterThan(0);
        assertThat(new String(pdfBytes, 0, 4)).isEqualTo("%PDF");

        try (PDDocument document = PDDocument.load(pdfBytes)) {
            String text = new PDFTextStripper().getText(document);
            assertThat(text).contains("NE_DOIT_PAS_APPARAITRE");
            assertThat(text).contains("CONTENU_CIBLE_1");
            assertThat(text).contains("CONTENU_CIBLE_2");
        }

        assertThat(Files.exists(xlsxFile)).isTrue();
    }

    @Test
    void shouldConvertOdsWithoutTryingToFilterTabsWithPoi() throws Exception {
        Path odsFile = createOdsFile();

        byte[] pdfBytes = pdfFromSpreadSheet.convertTabsInPdf(tempDir, "test.ods");

        assertThat(pdfBytes).isNotNull();
        assertThat(pdfBytes.length).isGreaterThan(0);
        assertThat(new String(pdfBytes, 0, 4)).isEqualTo("%PDF");

        try (PDDocument document = PDDocument.load(pdfBytes)) {
            String text = new PDFTextStripper().getText(document);
            assertThat(text).contains("IgnoredTab");
            assertThat(text).contains("TargetTab1");
            assertThat(text).contains("TargetTab2");
        }

        assertThat(Files.exists(odsFile)).isTrue();
    }

    @Test
    void shouldKeepOnlyLastPdfPages() throws Exception {
        byte[] sourcePdf = createMultiPagePdf("PAGE_1", "PAGE_2", "PAGE_3", "PAGE_4");

        byte[] trimmedPdf = pdfFromSpreadSheet.keepOnlyLastSheets(sourcePdf, 2);

        assertThat(trimmedPdf).isNotNull();
        assertThat(trimmedPdf.length).isGreaterThan(0);
        try (PDDocument document = PDDocument.load(trimmedPdf)) {
            assertThat(document.getNumberOfPages()).isEqualTo(2);
            String text = new PDFTextStripper().getText(document);
            assertThat(text).contains("PAGE_3");
            assertThat(text).contains("PAGE_4");
            assertThat(text).doesNotContain("PAGE_1");
            assertThat(text).doesNotContain("PAGE_2");
        }
    }

    private Path createOdsFile() throws Exception {
        Path odsFile = tempDir.resolve("test.ods");
        SpreadsheetDocument document = SpreadsheetDocument.newSpreadsheetDocument();
        try {
            document.getSheetByIndex(0).setTableName("IgnoredTab");
            document.appendSheet("TargetTab1");
            document.appendSheet("TargetTab2");
            document.save(odsFile.toFile());
            return odsFile;
        } finally {
            document.close();
        }
    }

    private Path createXlsxFile() throws IOException {
        Path xlsxFile = tempDir.resolve("test.xlsx");
        try (Workbook workbook = new XSSFWorkbook();
             OutputStream outputStream = Files.newOutputStream(xlsxFile)) {
            Sheet ignoredSheet = workbook.createSheet("IgnoredTab");
            Row ignoredRow = ignoredSheet.createRow(0);
            ignoredRow.createCell(0).setCellValue("NE_DOIT_PAS_APPARAITRE");

            Sheet targetSheet1 = workbook.createSheet("TargetTab1");
            Row targetRow1 = targetSheet1.createRow(0);
            targetRow1.createCell(0).setCellValue("CONTENU_CIBLE_1");

            Sheet targetSheet2 = workbook.createSheet("TargetTab2");
            Row targetRow2 = targetSheet2.createRow(0);
            targetRow2.createCell(0).setCellValue("CONTENU_CIBLE_2");

            workbook.write(outputStream);
        }
        return xlsxFile;
    }

    private static class FakeConverterPdfFromSpreadSheet extends PdfFromSpreadSheet {
        private FakeConverterPdfFromSpreadSheet() {
            super(List.of(new XlsxSpreadSheetSampleForTest(), new OdsSpreadSheetSampleForTest()));
        }

        @Override
        protected byte[] convert(Path inputToConvert, String extension) throws IOException {
            if ("ods".equals(extension)) {
                return createPdfBytesFromOds(inputToConvert);
            }
            return createPdfBytesFromXlsx(inputToConvert);
        }

        private byte[] createPdfBytesFromOds(Path inputToConvert) throws IOException {
            String content;
            try {
                SpreadsheetDocument document = SpreadsheetDocument.loadDocument(inputToConvert.toFile());
                try {
                    List<String> sheetNames = new ArrayList<>();
                    for (int i = 0; i < document.getSheetCount(); i++) {
                        Table table = document.getSheetByIndex(i);
                        if (table != null) {
                            sheetNames.add(table.getTableName());
                        }
                    }
                    content = String.join(" ", sheetNames);
                } finally {
                    document.close();
                }
            } catch (Exception e) {
                throw new IOException("Impossible de lire le fichier ODS de test", e);
            }

            try (ByteArrayOutputStream outputStream = new ByteArrayOutputStream();
                 PDDocument document = new PDDocument()) {
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
                return outputStream.toByteArray();
            }
        }

        private byte[] createPdfBytesFromXlsx(Path inputToConvert) throws IOException {
            try (Workbook workbook = WorkbookFactory.create(Files.newInputStream(inputToConvert));
                 ByteArrayOutputStream outputStream = new ByteArrayOutputStream();
                 PDDocument document = new PDDocument()) {
                List<String> values = new ArrayList<>();
                for (int i = 0; i < workbook.getNumberOfSheets(); i++) {
                    values.add(workbook.getSheetAt(i).getRow(0).getCell(0).getStringCellValue());
                }
                String value = String.join(" ", values);

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

        private static final class XlsxSpreadSheetSampleForTest implements SpreadSheetRender {
            @Override
            public boolean supports(String extension) {
                return "xlsx".equals(extension) || "xls".equals(extension);
            }

            @Override
            public Path buildWorkbook(Path originalFilePath) throws IOException {
                Path tempWorkbook = Files.createTempFile("eva-sheet-test-", ".xlsx");
                try (Workbook workbook = WorkbookFactory.create(Files.newInputStream(originalFilePath));
                     OutputStream outputStream = Files.newOutputStream(tempWorkbook)) {
                    workbook.write(outputStream);
                    return tempWorkbook;
                } catch (Exception e) {
                    Files.deleteIfExists(tempWorkbook);
                    if (e instanceof IOException ioException) {
                        throw ioException;
                    }
                    throw new IOException("Erreur lors de la preparation du XLSX de test", e);
                }
            }
        }

        private static final class OdsSpreadSheetSampleForTest implements SpreadSheetRender {
            @Override
            public boolean supports(String extension) {
                return "ods".equals(extension);
            }

            @Override
            public Path buildWorkbook(Path originalFilePath) throws IOException {
                Path tempOds = Files.createTempFile("eva-sheet-test-", ".ods");
                SpreadsheetDocument document = null;
                try {
                    document = SpreadsheetDocument.loadDocument(originalFilePath.toFile());
                    document.save(tempOds.toFile());
                    return tempOds;
                } catch (Exception e) {
                    Files.deleteIfExists(tempOds);
                    if (e instanceof IOException ioException) {
                        throw ioException;
                    }
                    throw new IOException("Erreur lors de la preparation du ODS de test", e);
                } finally {
                    if (document != null) {
                        document.close();
                    }
                }
            }
        }
    }

    private byte[] createMultiPagePdf(String... pageTexts) throws IOException {
        try (ByteArrayOutputStream outputStream = new ByteArrayOutputStream();
             PDDocument document = new PDDocument()) {
            for (String pageText : pageTexts) {
                PDPage page = new PDPage();
                document.addPage(page);
                try (PDPageContentStream contentStream = new PDPageContentStream(document, page)) {
                    contentStream.beginText();
                    contentStream.setFont(PDType1Font.HELVETICA, 12);
                    contentStream.newLineAtOffset(100, 700);
                    contentStream.showText(pageText);
                    contentStream.endText();
                }
            }
            document.save(outputStream);
            return outputStream.toByteArray();
        }
    }
}
