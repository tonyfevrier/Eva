package com.eva.backend.controller;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import com.eva.backend.service.DataExtractionService;
import com.eva.backend.service.FileService;
import com.eva.backend.service.PdfFromSpreadSheet;
import com.eva.backend.service.PdfGenerationServiceViaHtml;
import com.eva.backend.service.PdfMergeService;

import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.GetMapping;

import java.io.IOException;
import java.nio.charset.StandardCharsets;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.util.List;
import java.util.Map;
import java.util.stream.Stream;

@RestController
@RequestMapping("/pdf")
public class PdfController {
    @Value("${app.generated-pdf-dir}")
    private String generatedPdfDir;

    @Value("${app.pdf-dir}")
    private String pdfDir;

    @Value("${app.xls-data-dir}")
    private String xlsDataDir;

    @Autowired
    private DataExtractionService dataExtractor;

    @Autowired
    private PdfGenerationServiceViaHtml pdfService;

    @Autowired
    private PdfMergeService mergeService;

    @Autowired 
    private FileService fileService;

    @Autowired
    private PdfFromSpreadSheet pdfXlsxService;

    @GetMapping("/generate/{id}")
    public ResponseEntity<byte[]> generatePdf(@PathVariable Long id) throws IOException {
        /* On crée la première page de données, on la merge aux fichiers tests et questionnaires importés par l'utilisateur et au fichier xls. */
        Map<String, Map<String, Object>> data = dataExtractor.extractExperimentationData(id);
        byte[] experimentationDataByte = pdfService.createPdf(data);
        
        try {

            Path path = Paths.get(pdfDir).toAbsolutePath().normalize();
            List<String> fileNames = fileService.getExperimentationFileNames(path, id);
            byte[] testsByte = mergeService.mergeFilesFromDirectory(path, fileNames);
            
            //byte[] dataTestsByte = mergeService.merge(experimentationDataByte, testsByte);

            Path xlsDirectory = Paths.get(xlsDataDir).toAbsolutePath().normalize();
            String xlsFileName = fileService.findXlsFileByExperimentationId(xlsDirectory, id);
            byte[] convertedByte = pdfXlsxService.convertTabsInPdf(xlsFileName);
            byte[] tabsByte = pdfXlsxService.keepOnlyLastSheets(convertedByte, 5);
            //byte[] pdfByte = mergeService.merge(dataTestsByte, tabsByte);
            
            List<byte[]> pdfToMerge = List.of(experimentationDataByte, testsByte, tabsByte);
            byte[] pdfByte = mergeService.mergeMultipleFiles(pdfToMerge);
            String generatedFileName = "experimentation_summary_" + id + ".pdf";
            fileService.registerFile(generatedPdfDir, generatedFileName, pdfByte);
            return ResponseEntity.ok(pdfByte);

        } catch (IllegalArgumentException | NullPointerException e) {
            /* Gère le cas où il n'y a pas de tests ou questionnaires ou pas de fichier xls */
            String errorMessage = "Impossible de generer le PDF: " + e.getMessage();
            return ResponseEntity.badRequest().body(errorMessage.getBytes(StandardCharsets.UTF_8));
        }   
    }
}
