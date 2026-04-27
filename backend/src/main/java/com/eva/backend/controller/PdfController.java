package com.eva.backend.controller;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import com.eva.backend.service.DataExtractionService;
import com.eva.backend.service.FileService;
import com.eva.backend.service.PdfGenerationServiceViaHtml;
import com.eva.backend.service.PdfMergeService;

import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.GetMapping;

import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.util.List;
import java.util.Map;

@RestController
@RequestMapping("/pdf")
public class PdfController {
    @Value("${app.generated-pdf-dir}")
    private String generatedPdfDir;

    @Value("${app.pdf-dir}")
    private String pdfDir;

    @Autowired
    private DataExtractionService dataExtractor;

    @Autowired
    private PdfGenerationServiceViaHtml pdfService;

    @Autowired
    private PdfMergeService mergeService;

    @Autowired 
    private FileService fileService;

    @GetMapping("/generate/{id}")
    public ResponseEntity<byte[]> generatePdf(@PathVariable Long id) throws IOException {
        /* On crée la première page de données, on la merge aux fichiers tests et questionnaires importés par l'utilisateur. */
        Map<String, Map<String, Object>> data = dataExtractor.extractExperimentationData(id);
        byte[] experimentationDataByte = pdfService.createPdf(data);
        
        Path path = Paths.get(pdfDir).toAbsolutePath().normalize();
        List<String> fileNames = fileService.getExperimentationFileNames(path, id);
        byte[] testsByte = mergeService.mergeFilesFromDirectory(path, fileNames);
        
        byte[] pdfByte = mergeService.merge(experimentationDataByte, testsByte);
        
        String generatedFileName = "experimentation_summary_" + id + ".pdf";
        
        fileService.registerFile(generatedPdfDir, generatedFileName, pdfByte);
        return ResponseEntity.ok(pdfByte); 
    }
    
}
