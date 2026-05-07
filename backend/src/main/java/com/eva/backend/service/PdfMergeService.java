package com.eva.backend.service;

import java.io.ByteArrayInputStream;
import java.io.ByteArrayOutputStream;
import java.io.IOException;
import java.nio.file.Path;
import java.util.List;

import org.apache.pdfbox.io.MemoryUsageSetting;
import org.apache.pdfbox.multipdf.PDFMergerUtility;
import org.springframework.stereotype.Service;

@Service
public class PdfMergeService {

    public byte[] merge(byte[] firstPdfContent, byte[] secondPdfContent) {
        PDFMergerUtility merger = new PDFMergerUtility();

        if (firstPdfContent == null || firstPdfContent.length == 0) {
            throw new IllegalArgumentException("Le premier contenu PDF est vide.");
        }
        if (secondPdfContent == null || secondPdfContent.length == 0) {
            throw new IllegalArgumentException("Le second contenu PDF est vide. Pour générer le pdf, il faut avoir ajouté au moins un fichier test ou un questionnaire");
        }

        try (ByteArrayInputStream firstInput = new ByteArrayInputStream(firstPdfContent);
                ByteArrayInputStream secondInput = new ByteArrayInputStream(secondPdfContent);
                ByteArrayOutputStream mergedOutput = new ByteArrayOutputStream()) {

            merger.addSource(firstInput);
            merger.addSource(secondInput);
            merger.setDestinationStream(mergedOutput);
            merger.mergeDocuments(MemoryUsageSetting.setupMainMemoryOnly());

            return mergedOutput.toByteArray();
        } catch (IOException e) {
            throw new IllegalStateException("Erreur lors de la fusion des fichiers PDF.", e);
        }
    }

    public byte[] mergeFilesFromDirectory(Path sourceDirectory, List<String> fileNames) {
        PDFMergerUtility merger = new PDFMergerUtility();

        try (ByteArrayOutputStream mergedOutput = new ByteArrayOutputStream()) {
            for (String fileName : fileNames) {
                if (fileName == null || fileName.isBlank()) {
                    continue;
                }
                Path safeFilePath = sourceDirectory.resolve(Path.of(fileName).getFileName()).normalize();
                if (!safeFilePath.startsWith(sourceDirectory.normalize())) {
                    throw new IllegalArgumentException("Nom de fichier invalide : " + fileName);
                }
                merger.addSource(safeFilePath.toFile());
            }

            merger.setDestinationStream(mergedOutput);
            merger.mergeDocuments(MemoryUsageSetting.setupMainMemoryOnly());

            return mergedOutput.toByteArray();
        } catch (IOException e) {
            throw new IllegalStateException("Erreur lors de la fusion des fichiers PDF.", e);
        }
    }
}