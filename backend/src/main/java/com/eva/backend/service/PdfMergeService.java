package com.eva.backend.service;

import java.io.File;
import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Path;
import java.util.List;

import org.apache.pdfbox.io.MemoryUsageSetting;
import org.apache.pdfbox.multipdf.PDFMergerUtility;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Service;
import org.springframework.web.multipart.MultipartFile;

@Service
public class PdfMergeService {

    @Value("${app.generated-pdf-dir}")
    private String pdfDir;
    

    public File merge(List<MultipartFile> files, String outputFileName) {
        PDFMergerUtility merger = new PDFMergerUtility();
        Path outputDir = Path.of(pdfDir);
        String normalizedOutputName = normalizeFileName(outputFileName, ".pdf");
        Path outputPath = outputDir.resolve(normalizedOutputName);

        try {
            Files.createDirectories(outputDir);

            for (MultipartFile file : files) {
                if (file == null || file.isEmpty()) {
                    continue;
                }
                merger.addSource(file.getInputStream());
            }

            merger.setDestinationFileName(outputPath.toString());
            merger.mergeDocuments(MemoryUsageSetting.setupMainMemoryOnly());

            return outputPath.toFile();
        } catch (IOException e) {
            throw new IllegalStateException("Erreur lors de la fusion des fichiers PDF.", e);
        }
    }

    private String normalizeFileName(String fileName, String format){
        return fileName.toLowerCase().endsWith(format) ? fileName : fileName + format; 
    }
}