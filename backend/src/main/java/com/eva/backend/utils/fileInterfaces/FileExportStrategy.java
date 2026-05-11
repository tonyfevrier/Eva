package com.eva.backend.utils.fileInterfaces;

import org.springframework.http.MediaType;

public interface FileExportStrategy {
    boolean supports(String exportType);
    String getFileName(String entry);
    String getExportDir();
    MediaType resolveContentType(String entry);
}
