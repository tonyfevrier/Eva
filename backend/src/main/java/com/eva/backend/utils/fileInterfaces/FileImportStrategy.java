package com.eva.backend.utils.fileInterfaces;

import java.io.IOException;
import java.nio.file.Path;

import org.springframework.web.multipart.MultipartFile;

public interface FileImportStrategy {
    boolean supports(String importType);
    String getImportDir();
    String createImportedFileName(Long id, String extension);
    void copy(MultipartFile file, Path filePath) throws IOException;
}
