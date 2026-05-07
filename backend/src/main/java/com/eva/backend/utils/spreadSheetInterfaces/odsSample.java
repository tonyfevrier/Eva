package com.eva.backend.utils.spreadSheetInterfaces;

import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Path;

import org.springframework.stereotype.Component;

@Component
public class odsSample implements SpreadSheetRender{
    @Override
    public boolean supports(String extension){
        return "ods".equals(extension);
    }

    @Override
    public Path buildWorkbook(Path originalFilePath) throws IOException {
        Path tempOdsFile = Files.createTempFile("eva-sheet-", ".ods");
        try {
            // Preserve ODS print/layout settings exactly to avoid chart/page cropping during PDF export.
            Files.copy(originalFilePath, tempOdsFile, java.nio.file.StandardCopyOption.REPLACE_EXISTING);
            return tempOdsFile;
        } catch (IOException e) {
            Files.deleteIfExists(tempOdsFile);
            throw new IOException("Erreur lors de la copie du fichier ODS.", e);
        }
    }
    
} 
