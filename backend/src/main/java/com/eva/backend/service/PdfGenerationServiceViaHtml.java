package com.eva.backend.service;

import java.io.ByteArrayOutputStream;
import java.io.IOException;
import java.util.Map;

import org.springframework.stereotype.Service;
import org.thymeleaf.TemplateEngine;
import org.thymeleaf.context.Context;

import com.openhtmltopdf.pdfboxout.PdfRendererBuilder;

@Service
public class PdfGenerationServiceViaHtml {

    private final TemplateEngine templateEngine; //introduire des boucles, conditions, variables en html

    public PdfGenerationServiceViaHtml(TemplateEngine templateEngine) {
        this.templateEngine = templateEngine;
    }

    public byte[] createPdf(Map<String, Map<String, Object>> experimentationData) throws IOException {
        String html = renderTemplate(experimentationData);
        return renderHtmlToPdf(html);
    }

    private String renderTemplate(Map<String, Map<String, Object>> experimentationData) {
        /* Permet de passer des variables au html */
        Context context = new Context();
        context.setVariable("experimentationData", experimentationData);
        return templateEngine.process("experimentation-pdf", context);
    }

    private byte[] renderHtmlToPdf(String html) throws IOException {
        try (ByteArrayOutputStream outputStream = new ByteArrayOutputStream()) {
            PdfRendererBuilder builder = new PdfRendererBuilder();
            builder.useFastMode();
            builder.withHtmlContent(html, null);
            builder.toStream(outputStream);
            builder.run();
            return outputStream.toByteArray();
        }
    }
}
