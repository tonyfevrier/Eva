package com.eva.backend.utils.spreadSheetInterfaces;

import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Path;
import java.util.ArrayList;
import java.util.List;

import org.odftoolkit.simple.SpreadsheetDocument;
import org.springframework.stereotype.Component;
import org.odftoolkit.simple.table.Table;

@Component
public class odsSample implements SpreadSheetSample{
    @Override
    public boolean supports(String extension){
        return "ods".equals(extension);
    }

    @Override
    public Path buildWorkbookWithSelectedTabs(Path originalFilePath, List<String> tabNames) throws IOException {
        Path tempOdsFile = Files.createTempFile("eva-sheet-", ".ods");

        try {
            SpreadsheetDocument document = SpreadsheetDocument.loadDocument(originalFilePath.toFile());
            keepOnlySelectedTabs(document, tabNames);
            document.save(tempOdsFile.toFile());
            document.close();
            return tempOdsFile;
        } catch (Exception e) {
            Files.deleteIfExists(tempOdsFile);
            throw new IOException("Erreur lors du traitement du fichier ODS.", e);
        }
    }

    private void keepOnlySelectedTabs(SpreadsheetDocument document, List<String> tabNames) {
        List<Integer> targetTabsIndexes = getTabsIndexes(document, tabNames);

        for (int i = document.getSheetCount() - 1; i >= 0; i--) {
            if (!targetTabsIndexes.contains(i)) {
                document.removeSheet(i);
            }
        }
    }

    private List<Integer> getTabsIndexes(SpreadsheetDocument document, List<String> tabNames) {
        List<Integer> targetTabIndexes = new ArrayList<>();

        for (String tabName : tabNames) {
            int targetTabIndex = getSheetIndex(document, tabName);
            if (targetTabIndex < 0) {
                throw new IllegalArgumentException("Onglet introuvable : " + tabName);
            }
            targetTabIndexes.add(targetTabIndex);
        }

        return targetTabIndexes;
    }

    private int getSheetIndex(SpreadsheetDocument document, String tabName) {
        for (int i = 0; i < document.getSheetCount(); i++) {
            Table table = document.getSheetByIndex(i);
            if (table != null && tabName.equals(table.getTableName())) {
                return i;
            }
        }
        return -1;
    }
    
} 
