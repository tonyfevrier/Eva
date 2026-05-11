package com.eva.backend.utils.spreadSheetInterfaces;

import java.io.IOException;
import java.io.InputStream;
import java.io.OutputStream;
import java.nio.file.Files;
import java.nio.file.Path;

import org.apache.poi.ss.usermodel.Workbook;
import org.apache.poi.ss.usermodel.WorkbookFactory;
import org.springframework.stereotype.Component;

@Component
public class xlsxSample implements SpreadSheetRender{
    @Override
    public boolean supports(String extension){
        return "xlsx".equals(extension) || "xls".equals(extension);
    }

    @Override
    public Path buildWorkbook(Path originalFilePath) throws IOException{
        try (InputStream inputStream = Files.newInputStream(originalFilePath);
             Workbook workbook = WorkbookFactory.create(inputStream)) {
            return writeTabInTemporaryFile(workbook);
        }
    }

    private Path writeTabInTemporaryFile(Workbook workbook) throws IOException{
        Path tempWorkbook = Files.createTempFile("eva-sheet-", ".xlsx");
        try (OutputStream outputStream = Files.newOutputStream(tempWorkbook)) {
            workbook.write(outputStream);
        }

        return tempWorkbook;
    }
    
} 
