package com.eva.backend.utils.spreadSheetInterfaces;

import java.io.IOException;
import java.nio.file.Path;

public interface SpreadSheetRender {
    boolean supports(String extension);
    Path buildWorkbook(Path originalFilePath) throws IOException; 
}
