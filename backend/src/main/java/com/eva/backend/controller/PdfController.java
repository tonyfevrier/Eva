package com.eva.backend.controller;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.ResponseEntity;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.RequestMapping;

import com.eva.backend.service.DataExtractionService;
import com.eva.backend.service.PdfGenerationService;

import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;

import java.io.IOException;
import java.util.Map;

@Controller
@RequestMapping("/pdf")
public class PdfController {
    @Autowired
    private DataExtractionService dataExtractor;

    @Autowired
    private PdfGenerationService pdfService;

    @PostMapping("/generate/{id}")
    public ResponseEntity<byte[]> generatePdf(@PathVariable Long id) throws IOException {
        Map<String, Map<String, Object>> data = dataExtractor.extractExperimentationData(id);
        byte[] pdfByte = pdfService.createPdf(data);
        return ResponseEntity.ok(pdfByte);
    }
    
}
