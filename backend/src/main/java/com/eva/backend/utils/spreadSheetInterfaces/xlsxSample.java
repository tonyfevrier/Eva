package com.eva.backend.utils.spreadSheetInterfaces;

import java.io.IOException;
import java.io.InputStream;
import java.io.OutputStream;
import java.nio.file.Files;
import java.nio.file.Path;
import java.util.ArrayList;
import java.util.List;

import org.apache.poi.ss.usermodel.Workbook;
import org.apache.poi.ss.usermodel.WorkbookFactory;
import org.springframework.stereotype.Component;

@Component
public class xlsxSample implements SpreadSheetSample{
    @Override
    public boolean supports(String extension){
        return "xlsx".equals(extension);
    }

    @Override
    public Path buildWorkbookWithSelectedTabs(Path originalFilePath, List<String> tabNames) throws IOException{
        try (InputStream inputStream = Files.newInputStream(originalFilePath);
             Workbook workbook = WorkbookFactory.create(inputStream)) {
            keepOnlySelectedTabs(workbook, tabNames);
            return writeTabInTemporaryFile(workbook);
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

    private Path writeTabInTemporaryFile(Workbook workbook) throws IOException{
        Path tempWorkbook = Files.createTempFile("eva-sheet-", ".xlsx");
        try (OutputStream outputStream = Files.newOutputStream(tempWorkbook)) {
            workbook.write(outputStream);
        }

        return tempWorkbook;
    }
    
} 
