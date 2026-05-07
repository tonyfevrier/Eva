package com.eva.backend.service;

import java.io.ByteArrayOutputStream;
import java.io.IOException;

import org.springframework.stereotype.Service;
import org.thymeleaf.TemplateEngine;
import org.thymeleaf.context.Context;

import com.eva.backend.records.DataForHtml;
import com.openhtmltopdf.pdfboxout.PdfRendererBuilder;

@Service
public class PdfGenerationServiceViaHtml {

    private final TemplateEngine templateEngine; //introduire des boucles, conditions, variables en html

    public PdfGenerationServiceViaHtml(TemplateEngine templateEngine) {
        this.templateEngine = templateEngine;
    }

    public byte[] createPdf(DataForHtml data) throws IOException {
        String html = renderTemplate(data);
        return renderHtmlToPdf(html);
    }

    private String renderTemplate(DataForHtml data) {
        /* Permet de passer des variables au html */
        Context context = new Context();
        context.setVariable(data.variableName(), data.variable());
        return templateEngine.process(data.templateName(), context);
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
