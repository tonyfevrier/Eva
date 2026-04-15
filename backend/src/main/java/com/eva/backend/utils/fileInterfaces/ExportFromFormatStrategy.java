package com.eva.backend.utils.fileInterfaces;

import org.springframework.http.MediaType;
import org.springframework.stereotype.Component;
import org.springframework.beans.factory.annotation.Value;

@Component
public class ExportFromFormatStrategy implements FileExportStrategy{
    @Value("${app.xls-data-model-dir}")
    private String exportDir;

    @Override
    public boolean supports(String exportType){
        return exportType.equals("format");
    }

    @Override
    public String getFileName(String format){
        return switch (format) {
            case "xls" -> "ResultatsEVA_v2_Excel97-2003.xls";
            case "xlsx" -> "ResultatsEVA_v2_Excel.xlsx";
            case "ods" -> "ResultatsEVA_v2_LibreOffice.ods";
            default -> "ResultatsEVA_v2_Excel.xlsx";
        };
    }

    @Override
    public String getExportDir(){
        return exportDir;
    }

    @Override
    public MediaType resolveContentType(String format) {
        return switch (format) {
            case "xls" -> MediaType.parseMediaType("application/vnd.ms-excel");
            case "xlsx" -> MediaType.parseMediaType("application/vnd.openxmlformats-officedocument.spreadsheetml.sheet");
            case "ods" -> MediaType.parseMediaType("application/vnd.oasis.opendocument.spreadsheet");
            default -> MediaType.APPLICATION_OCTET_STREAM;
        };
    } 
}
