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

import jakarta.annotation.PostConstruct;
import jakarta.annotation.PreDestroy;

@Service
public class PdfFromXlsx {
    @Value("${app.xls-data-dir}")
    private String xlsDirectory;

    @Value("${app.libreoffice.home:}")
    private String libreOfficeHome;

    private LocalOfficeManager officeManager;

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
        if (!isSupportedSpreadsheet(extension)) {
            throw new IllegalArgumentException("Format non supporte : " + extension + ". Formats autorises : xls, xlsx, ods.");
        }

        Path inputToConvert = null;

        try {
            inputToConvert = buildWorkbookWithSelectedTabs(safeFilePath, tabNames, extension);
            return convertInput(inputToConvert, extension);
        } catch (IOException | OfficeException e) {
            throw new IllegalStateException("Erreur lors de la conversion du fichier tableur en PDF.", e);
        } finally {
            deleteTemporaryFile(inputToConvert);
        }
    }

    private Path buildWorkbookWithSelectedTabs(Path originalFilePath, List<String> tabNames, String extension) throws IOException {
        try (InputStream inputStream = Files.newInputStream(originalFilePath);
             Workbook workbook = WorkbookFactory.create(inputStream)) {
            keepOnlySelectedTabs(workbook, tabNames);
            return writeTabInTemporaryFile(workbook, extension);
        }
    }

    private void keepOnlySelectedTabs(Workbook workbook, List<String> tabNames){
        List<Integer> targetTabsIndexes = getTabsIndexes(workbook, tabNames);
        
        for (int i = workbook.getNumberOfSheets() - 1; i >= 0; i--) {
            if (!targetTabsIndexes.contains(i)) {
                workbook.removeSheetAt(i);
            }
        }

        workbook.setActiveSheet(0);
        workbook.setSelectedTab(0);
    }

    private List<Integer> getTabsIndexes(Workbook workbook, List<String> tabNames){
        List<Integer> targetTabIndexes = new ArrayList<>();

        for (String tabName: tabNames){
            int targetTabIndex = workbook.getSheetIndex(tabName);
            if (targetTabIndex < 0) {
                throw new IllegalArgumentException("Onglet introuvable : " + tabName);
            }
            targetTabIndexes.add(targetTabIndex);
        }
        return targetTabIndexes;
    }

    private Path writeTabInTemporaryFile(Workbook workbook, String extension) throws IOException{
        Path tempWorkbook = Files.createTempFile("eva-sheet-", "." + extension);
        try (OutputStream outputStream = Files.newOutputStream(tempWorkbook)) {
            workbook.write(outputStream);
        }

        return tempWorkbook;
    }

    protected byte[] convertInput(Path inputToConvert, String extension) throws OfficeException, IOException{
        try (ByteArrayOutputStream outputStream = new ByteArrayOutputStream()) {
                DocumentConverter converter = LocalConverter.make(officeManager);
                converter.convert(inputToConvert.toFile())
                        .as(DefaultDocumentFormatRegistry.getFormatByExtension(extension))
                        .to(outputStream)
                        .as(DefaultDocumentFormatRegistry.PDF)
                        .execute();
                return outputStream.toByteArray();
            }
    }

    private void deleteTemporaryFile(Path inputToConvert){
        if (inputToConvert != null) {
            try {
                Files.deleteIfExists(inputToConvert);
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

    private boolean isSupportedSpreadsheet(String extension) {
        return "xls".equals(extension) || "xlsx".equals(extension) || "ods".equals(extension);
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
