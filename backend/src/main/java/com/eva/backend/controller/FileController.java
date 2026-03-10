package com.eva.backend.controller;

import org.springframework.beans.factory.annotation.Value;
import org.springframework.http.HttpHeaders;
import org.springframework.http.HttpStatus;
import org.springframework.http.MediaType;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.util.Map;

import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;

import com.eva.backend.records.DownloadContent;


@RestController
@RequestMapping("/file")
public class FileController {

    @Value("${app.export-dir}")
    private String exportDir;

    @PostMapping("/export")
    public ResponseEntity<byte[]> exportFile(@RequestBody Map<String, String> body) throws IOException {
        String format = body.get("format");
        if (format == null) {
            return ResponseEntity.status(HttpStatus.BAD_REQUEST).body("Missing 'format' in request body".getBytes());
        }

        DownloadContent content = prepareForDownload(format);        
        return ResponseEntity.ok().headers(content.headers()).body(content.fileBytes());
    }

    private DownloadContent prepareForDownload(String format) throws IOException{
        String filename = createFileName(format);
        byte[] fileBytes = getFileContent(filename);        

        HttpHeaders headers = new HttpHeaders();
        headers.setContentType(resolveContentType(format));
        headers.set(HttpHeaders.CONTENT_DISPOSITION, "attachment; filename=\"" + filename + "\"");
        return new DownloadContent(headers, fileBytes);
    }

    private String createFileName(String format){
        return switch (format) {
            case "xls" -> "ResultatsEVA_v2_Excel97-2003.xls";
            case "xlsx" -> "ResultatsEVA_v2_Excel.xlsx";
            case "ods" -> "ResultatsEVA_v2_LibreOffice.ods";
            default -> "ResultatsEVA_v2_Excel.xlsx";
        };
    }

    private byte[] getFileContent(String filename) throws IOException{
        Path baseDir = Paths.get(exportDir).toAbsolutePath().normalize();
        Path filePath = baseDir.resolve(filename).normalize();        
        return Files.readAllBytes(filePath);
    }

    private MediaType resolveContentType(String format) {
        return switch (format) {
            case "xls" -> MediaType.parseMediaType("application/vnd.ms-excel");
            case "xlsx" -> MediaType.parseMediaType("application/vnd.openxmlformats-officedocument.spreadsheetml.sheet");
            case "ods" -> MediaType.parseMediaType("application/vnd.oasis.opendocument.spreadsheet");
            default -> MediaType.APPLICATION_OCTET_STREAM;
        };
    }
    
}
