package com.eva.backend.service;

import java.io.ByteArrayOutputStream;
import java.io.IOException;
import java.util.Map;

import org.apache.pdfbox.pdmodel.PDDocument;
import org.apache.pdfbox.pdmodel.PDPage;
import org.apache.pdfbox.pdmodel.PDPageContentStream;
import org.apache.pdfbox.pdmodel.common.PDRectangle;
import org.apache.pdfbox.pdmodel.font.PDType1Font;
import org.apache.pdfbox.pdmodel.font.Standard14Fonts;
import org.springframework.stereotype.Service;

@Service
public class PdfGenerationService {
    private static final float PAGE_MARGIN = 50f;
    private static final float LINE_HEIGHT = 16f;
    private static final int MAX_CHARS_PER_LINE = 110;

    private static final class PdfRenderContext {
        private final PDDocument document;
        private PDPage page;
        private PDPageContentStream stream;
        private float y;

        private PdfRenderContext(PDDocument document, PDPage page, PDPageContentStream stream, float y) {
            this.document = document;
            this.page = page;
            this.stream = stream;
            this.y = y;
        }
    }

    public byte[] createPdf(Map<String, Map<String, Object>> experimentationData) throws IOException {
        try (PDDocument document = new PDDocument(); ByteArrayOutputStream output = new ByteArrayOutputStream()) {
            createPageWithExperimentationData(document, experimentationData);
            document.save(output);
            return output.toByteArray();
        }
    }

    private void createPageWithExperimentationData(PDDocument document, Map<String, Map<String, Object>> experimentationData) throws IOException {
        PdfRenderContext context = createRenderContext(document);
        context.stream.showText("Données de l'expérimentation");

        for (Map.Entry<String, Map<String,Object>> category : experimentationData.entrySet()) {
            Map<String, Object> categoryData = category.getValue();
            context.stream.showText(category.getKey());

            for (Map.Entry<String, Object> entry : categoryData.entrySet()) {
                String line = createLine(entry);
                String[] wrappedLines = splitLine(line);
                writeLines(context, wrappedLines);
            }
        }

        closeContentStream(context);
    }

    private PdfRenderContext createRenderContext(PDDocument document) throws IOException {
        PDPage page = createAndAddPage(document);
        float y = page.getMediaBox().getHeight() - PAGE_MARGIN;
        PDPageContentStream stream = prepareContentStream(document, page, y);
        return new PdfRenderContext(document, page, stream, y);
    }

    private PDPageContentStream prepareContentStream(PDDocument document, PDPage page, float y) throws IOException{
        PDPageContentStream stream = new PDPageContentStream(document, page);
        stream.beginText();
        stream.setFont(new PDType1Font(Standard14Fonts.FontName.HELVETICA), 12);
        stream.newLineAtOffset(PAGE_MARGIN, y);
        return stream;
    }

    private String createLine(Map.Entry<String, Object> entry){
        Object value = entry.getValue();
        return entry.getKey() + " : " + (value == null ? "" : String.valueOf(value));
    }

    private void writeLines(PdfRenderContext context, String[] wrappedLines) throws IOException {
        for (String wrappedLine : wrappedLines) {
            context.y -= LINE_HEIGHT;

            if (context.y <= PAGE_MARGIN) {
                moveToNextPage(context);
            }
            
            context.stream.newLineAtOffset(0, -LINE_HEIGHT);
            context.stream.showText(wrappedLine);
        }
    }

    private void moveToNextPage(PdfRenderContext context) throws IOException {
        closeContentStream(context);
        context.page = createAndAddPage(context.document);
        context.y = context.page.getMediaBox().getHeight() - PAGE_MARGIN;
        context.stream = prepareContentStream(context.document, context.page, context.y);
    }

    private void closeContentStream(PdfRenderContext context) throws IOException {
        context.stream.endText();
        context.stream.close();
    }

    private PDPage createAndAddPage(PDDocument document) {
        PDPage page = new PDPage(PDRectangle.A4);
        document.addPage(page);
        return page;
    }

    private String[] splitLine(String line) {
        /* Fabrication d'une ou plusieurs lignes suivant que le nombre de caractères est supérieur au nb de caractères de la ligne*/
        int chunkCount = (int) Math.ceil((double) line.length() / MAX_CHARS_PER_LINE);
        if (chunkCount <= 1) {
            return new String[] { line };
        }

        String[] chunks = new String[chunkCount];
        for (int i = 0; i < chunkCount; i++) {
            int start = i * MAX_CHARS_PER_LINE;
            int end = Math.min(start + MAX_CHARS_PER_LINE, line.length());
            chunks[i] = line.substring(start, end);
        }
        return chunks;
    }

}
