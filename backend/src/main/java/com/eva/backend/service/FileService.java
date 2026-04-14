package com.eva.backend.service;

import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.nio.file.StandardCopyOption;
import java.time.LocalDate;
import java.util.List;
import java.util.Locale;

import org.springframework.http.MediaType;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.http.HttpHeaders;
import org.springframework.stereotype.Service;
import org.springframework.web.multipart.MultipartFile;

import com.eva.backend.model.Experimentation;
import com.eva.backend.model.PedagogicalContext;
import com.eva.backend.model.User;
import com.eva.backend.records.DownloadContent;
import com.eva.backend.repository.ExperimentationRepository;
import com.eva.backend.utils.fileInterfaces.FileImportStrategy;

@Service
public class FileService {

    @Value("${app.export-dir}")
    private String exportDir;

    private final List<FileImportStrategy> importStrategies;

    public FileService(List<FileImportStrategy> importStrategies) {
        this.importStrategies = importStrategies;
    }

    public DownloadContent prepareContentForDownload(String format) throws IOException {
        String filename = createFileName(format);
        byte[] fileBytes = getExportFileContent(filename);        

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

    private byte[] getExportFileContent(String filename) throws IOException{
        Path filePath = writeFilePath(filename, exportDir);
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
