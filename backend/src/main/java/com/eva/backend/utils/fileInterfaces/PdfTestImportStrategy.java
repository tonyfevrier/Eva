package com.eva.backend.utils.fileInterfaces;

import java.io.IOException;
import java.nio.file.FileAlreadyExistsException;
import java.nio.file.Files;
import java.nio.file.Path;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Component;
import org.springframework.web.multipart.MultipartFile;

@Component
public class PdfTestImportStrategy implements FileImportStrategy {
    @Value("${app.import-dir.pdf}")
    private String importDir;

    private static final Pattern FILE_NAME_PATTERN = Pattern.compile("^(.*_)(\\d+)(\\.[^.]+)$");

    @Override 
    public boolean supports(String importType){
        return "pdfTest".equals(importType);
    }

    @Override
    public String getImportDir() { 
        return importDir; 
    }
    
    @Override
    public String createImportedFileName(Long id, String extension) {
        return "test_id" + id + "_1." + extension;
    }

    public void copy(MultipartFile file, Path filePath) throws IOException {
        /* Cette fonction copie sans écraser si le nom du fichier existe déjà */
        Path candidate = filePath;

        while (true) {
            try {
                Files.copy(file.getInputStream(), candidate);
                return;
            } catch (FileAlreadyExistsException e) {
                candidate = incrementFilePath(candidate);
            }
        }
    }

    private Path incrementFilePath(Path filePath) {
        String fileName = filePath.getFileName().toString();
        Matcher matcher = FILE_NAME_PATTERN.matcher(fileName);

        if (!matcher.matches()) {
            throw new IllegalArgumentException("Nom de fichier invalide : " + fileName);
        }

        String prefix = matcher.group(1);
        int number = Integer.parseInt(matcher.group(2));
        String extension = matcher.group(3);

        String newFileName = prefix + (number + 1) + extension;
        return filePath.resolveSibling(newFileName);
    }
}
