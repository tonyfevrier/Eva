package com.eva.backend.service;

import java.io.ByteArrayInputStream;
import java.io.ByteArrayOutputStream;
import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Path;
import java.util.List;
import java.util.Locale;

import org.apache.pdfbox.pdmodel.PDDocument;
import org.jodconverter.core.DocumentConverter;
import org.jodconverter.core.document.DefaultDocumentFormatRegistry;
import org.jodconverter.core.office.OfficeException;
import org.jodconverter.local.LocalConverter;
import org.jodconverter.local.office.LocalOfficeManager;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Service;

import com.eva.backend.utils.spreadSheetInterfaces.SpreadSheetRender;

import jakarta.annotation.PreDestroy;

@Service
public class PdfFromSpreadSheet {

    @Value("${app.libreoffice.home:}")
    private String libreOfficeHome;

    @Value("${app.libreoffice.enabled:true}")
    private boolean libreOfficeEnabled;

    private LocalOfficeManager officeManager;

    private final List<SpreadSheetRender> strategies;

    public PdfFromSpreadSheet(List<SpreadSheetRender> strategies){
        this.strategies = strategies;
    }

    public byte[] keepOnlyLastSheets(byte[] pdfContent, Integer numberOfPagesToKeep) throws IOException {
        try (ByteArrayInputStream inputStream = new ByteArrayInputStream(pdfContent);
            PDDocument document = PDDocument.load(inputStream)) {
            
            int totalPages = document.getNumberOfPages();
            int pagesToKeep = Math.min(numberOfPagesToKeep, totalPages);
            int startPageIndex = totalPages - pagesToKeep;
            
            for (int i = 0; i < startPageIndex; i++) {
                document.removePage(0); // On retire toujours la page 0 puisque les autres remontent
            }
            
            try (ByteArrayOutputStream outputStream = new ByteArrayOutputStream()) {
                document.save(outputStream);
                return outputStream.toByteArray();
            }
        }
    }

    public byte[] convertTabsInPdf(Path sourceDirectory, String fileName) {
        Path safeFilePath = sourceDirectory.resolve(Path.of(fileName).getFileName()).normalize();
        if (!safeFilePath.startsWith(sourceDirectory)) {
            throw new IllegalArgumentException("Nom de fichier invalide : " + fileName);
        }
        if (!Files.exists(safeFilePath) || !Files.isRegularFile(safeFilePath)) {
            throw new IllegalArgumentException("Fichier introuvable : " + fileName);
        }

        String extension = getExtension(safeFilePath.getFileName().toString());
        SpreadSheetRender strategy = strategies.stream()
                                                .filter(s -> s.supports(extension))
                                                .findFirst()
                                                .orElseThrow();

        Path workbookPath = null;

        try {
            workbookPath = strategy.buildWorkbook(safeFilePath);
            return convert(workbookPath, extension);
        } catch (IOException | OfficeException e) {
            throw new IllegalStateException("Erreur lors de la conversion du fichier tableur en PDF.", e);
        } finally {
            if (workbookPath != null) {
                deleteTemporaryFile(workbookPath);
            }
        }
    }
    protected byte[] convert(Path workbookPath, String extension) throws OfficeException, IOException{
        ensureOfficeManagerStarted();

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

    private synchronized void ensureOfficeManagerStarted() throws OfficeException {
        if (officeManager != null) {
            return;
        }

        if (!libreOfficeEnabled) {
            throw new IllegalStateException(
                    "La conversion LibreOffice est desactivee (app.libreoffice.enabled=false)."
                            + " Activez cette propriete pour les tests qui en ont besoin.");
        }

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
