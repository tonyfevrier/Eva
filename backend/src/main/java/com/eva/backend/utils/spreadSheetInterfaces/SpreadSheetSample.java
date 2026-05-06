package com.eva.backend.utils.spreadSheetInterfaces;

import java.io.IOException;
import java.nio.file.Path;
import java.util.List;

public interface SpreadSheetSample {
    boolean supports(String extension);
    Path buildWorkbookWithSelectedTabs(Path originalFilePath, List<String> tabNames) throws IOException; 
}
