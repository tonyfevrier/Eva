package com.eva.backend.service;

import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.util.List;
import java.util.Locale;

import org.springframework.http.MediaType;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.http.HttpHeaders;
import org.springframework.stereotype.Service;
import org.springframework.web.multipart.MultipartFile;

import com.eva.backend.records.DownloadContent;
import com.eva.backend.utils.fileInterfaces.FileExportStrategy;
import com.eva.backend.utils.fileInterfaces.FileImportStrategy;

@Service
public class FileService {

    /*@Value("${app.xls-data-model-dir}")
    private String exportDir;*/

    /* Les service sont chargés dès le début et pas à chaque requête.
    Il n'est donc pas conseillé d'importer une classe fille d'une classe abstraite
    à chaque requête. On importe toutes les classes filles dès le début (ici les stratégies)
    et la requête indiquera quelle stratégie on choisit.*/

    private final List<FileImportStrategy> importStrategies;
    private final List<FileExportStrategy> exportStrategies;

    public FileService(List<FileImportStrategy> importStrategies,
                       List<FileExportStrategy> exportStrategies
    ) {
        this.importStrategies = importStrategies;
        this.exportStrategies = exportStrategies;
    }

    public DownloadContent prepareContentForDownload(String exportType, String entry) throws IOException {
        /* entry peut être un format (xls, ...) ou un nom de fichier dans le cas des pdf */
        FileExportStrategy strategy = exportStrategies.stream()
                                                      .filter(s -> s.supports(exportType))
                                                      .findFirst()
                                                      .orElseThrow();
        String filename = strategy.getFileName(entry);
        byte[] fileBytes = getExportFileContent(filename, strategy.getExportDir());        

        HttpHeaders headers = new HttpHeaders();
        headers.setContentType(strategy.resolveContentType(entry));
        headers.set(HttpHeaders.CONTENT_DISPOSITION, "attachment; filename=\"" + filename + "\"");
        return new DownloadContent(headers, fileBytes);
    }

    /*private String getFileName(String format){
        return switch (format) {
            case "xls" -> "ResultatsEVA_v2_Excel97-2003.xls";
            case "xlsx" -> "ResultatsEVA_v2_Excel.xlsx";
            case "ods" -> "ResultatsEVA_v2_LibreOffice.ods";
            default -> "ResultatsEVA_v2_Excel.xlsx";
        };
    }*/

    private byte[] getExportFileContent(String filename, String exportDir) throws IOException{
        Path filePath = writeFilePath(filename, exportDir);
        return Files.readAllBytes(filePath);
    }

    /*private MediaType resolveContentType(String format) {
        return switch (format) {
            case "xls" -> MediaType.parseMediaType("application/vnd.ms-excel");
            case "xlsx" -> MediaType.parseMediaType("application/vnd.openxmlformats-officedocument.spreadsheetml.sheet");
            case "ods" -> MediaType.parseMediaType("application/vnd.oasis.opendocument.spreadsheet");
            default -> MediaType.APPLICATION_OCTET_STREAM;
        };
    }*/

    public String getFileExtension(String fileName) {
        int dotIndex = fileName.lastIndexOf('.');
        if (dotIndex < 0 || dotIndex == fileName.length() - 1) {
            return "";
        }
        return fileName.substring(dotIndex + 1).toLowerCase(Locale.ROOT);
    }

    public void registerImportedFile(String importType, MultipartFile file, Long id, String extension) throws IOException {
        FileImportStrategy strategy = importStrategies.stream()
                                                      .filter(s -> s.supports(importType))
                                                      .findFirst()
                                                      .orElseThrow();

        String importedFileName = strategy.createImportedFileName(id, extension);
        Path filePath = writeFilePath(importedFileName, strategy.getImportDir());
        strategy.copy(file, filePath);
    }

    private Path writeFilePath(String filename, String directory) throws IOException{
        /* Récupère le nom du dossier et du fichier inclus et les assemble, crée éventuellement le dossier si inexistant */
        Path baseDir = Paths.get(directory).toAbsolutePath().normalize();
        Files.createDirectories(baseDir);
        Path filePath = baseDir.resolve(filename).normalize();//nettoyer le path des ../ avec normalize et resolve concatène le dossier au nom de fichier        
        return filePath;
    }
}
