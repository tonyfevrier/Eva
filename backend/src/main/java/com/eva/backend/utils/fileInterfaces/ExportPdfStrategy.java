package com.eva.backend.utils.fileInterfaces;

import org.springframework.http.MediaType;
import org.springframework.stereotype.Component;
import org.springframework.beans.factory.annotation.Value;

@Component
public class ExportPdfStrategy implements FileExportStrategy{
    @Value("${app.pdf-dir}")
    private String exportDir;

    @Override
    public boolean supports(String exportType){
        return exportType.equals("pdf");
    }
 
    @Override
    public String getFileName(String fileName){
        return fileName;
    }

    @Override
    public String getExportDir(){
        return exportDir;
    }

    @Override
    public MediaType resolveContentType(String fileName) {
        return MediaType.parseMediaType("application/pdf");  
    } 
}
