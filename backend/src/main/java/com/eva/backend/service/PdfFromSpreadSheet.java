package com.eva.backend.service;

import java.io.ByteArrayOutputStream;
import java.io.IOException;
import java.io.InputStream;
import java.io.OutputStream;
import java.nio.file.Files;
import java.nio.file.Path;
import java.util.ArrayList;
import java.util.List;
import java.util.Locale;

import org.apache.poi.ss.usermodel.Workbook;
import org.apache.poi.ss.usermodel.WorkbookFactory;
import org.jodconverter.core.DocumentConverter;
import org.jodconverter.core.document.DefaultDocumentFormatRegistry;
import org.jodconverter.core.office.OfficeException;
import org.jodconverter.local.LocalConverter;
import org.jodconverter.local.office.LocalOfficeManager;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Service;

import com.eva.backend.utils.spreadSheetInterfaces.SpreadSheetSample;

import jakarta.annotation.PostConstruct;
import jakarta.annotation.PreDestroy;

@Service
public class PdfFromSpreadSheet {
    @Value("${app.xls-data-dir}")
    private String xlsDirectory;

    @Value("${app.libreoffice.home:}")
    private String libreOfficeHome;

    private LocalOfficeManager officeManager;

    private final List<SpreadSheetSample> sampleStrategies;

    public PdfFromSpreadSheet(List<SpreadSheetSample> sampleStrategies){
        this.sampleStrategies = sampleStrategies;
    }

    public byte[] convertTabsInPdf(String fileName, List<String> tabNames) {
        Path sourceDirectory = Path.of(xlsDirectory).normalize();
        Path safeFilePath = sourceDirectory.resolve(Path.of(fileName).getFileName()).normalize();
        if (!safeFilePath.startsWith(sourceDirectory)) {
            throw new IllegalArgumentException("Nom de fichier invalide : " + fileName);
        }
        if (!Files.exists(safeFilePath) || !Files.isRegularFile(safeFilePath)) {
            throw new IllegalArgumentException("Fichier introuvable : " + fileName);
        }

        String extension = getExtension(safeFilePath.getFileName().toString());
        SpreadSheetSample sampleStrategy = sampleStrategies.stream()
                                                .filter(s -> s.supports(extension))
                                                .findFirst()
                                                .orElseThrow();

        Path sampledWorkbookPath = null;

        try {
            sampledWorkbookPath = sampleStrategy.buildWorkbookWithSelectedTabs(safeFilePath, tabNames);
            return convert(sampledWorkbookPath, extension);
        } catch (IOException | OfficeException e) {
            throw new IllegalStateException("Erreur lors de la conversion du fichier tableur en PDF.", e);
        } finally {
            if (sampledWorkbookPath != null) {
                deleteTemporaryFile(sampledWorkbookPath);
            }
        }
    }

    protected byte[] convert(Path workbookPath, String extension) throws OfficeException, IOException{
        try (ByteArrayOutputStream outputStream = new ByteArrayOutputStream()) {
                DocumentConverter converter = LocalConverter.make(officeManager);
                converter.convert(workbookPath.toFile())
                        .as(DefaultDocumentFormatRegistry.getFormatByExtension(extension))
                        .to(outputStream)
                        .as(DefaultDocumentFormatRegistry.PDF)
                        .execute();
                return outputStream.toByteArray();
            }
    }

    private void deleteTemporaryFile(Path filePath){
        if (filePath != null) {
            try {
                Files.deleteIfExists(filePath);
            } catch (IOException ignored) {
                // Ignorer une erreur de nettoyage de fichier temporaire.
            }
        }
    }

    private String getExtension(String fileName) {
        int lastDot = fileName.lastIndexOf('.');
        if (lastDot < 0 || lastDot == fileName.length() - 1) {
            return "";
        }
        return fileName.substring(lastDot + 1).toLowerCase(Locale.ROOT);
    }

    @PostConstruct
    void startOfficeManager() {
        /* Démarrer libre office à l'initialisation spring afin de pouvoir plus tard faire la conversion pdf des onglets */
        try {
            LocalOfficeManager.Builder builder = LocalOfficeManager.builder();
            if (libreOfficeHome != null && !libreOfficeHome.isBlank()) {
                builder.officeHome(libreOfficeHome);
            }

            officeManager = builder.build();
            officeManager.start();
        } catch (OfficeException e) {
            throw new IllegalStateException(
                    "Impossible de demarrer LibreOffice. Verifiez l'installation ou configurez app.libreoffice.home.", e);
        }
    }

    @PreDestroy
    void stopOfficeManager() {
        if (officeManager == null) {
            return;
        }
        try {
            officeManager.stop();
        } catch (OfficeException ignored) {
            // Ignorer les erreurs d'arret au shutdown de l'application.
        }
    }
}
