package com.eva.backend.controller;

import org.springframework.beans.factory.annotation.Autowired;
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
import java.nio.file.StandardCopyOption;
import java.time.LocalDate;
import java.util.Locale;
import java.util.Map;

import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.multipart.MultipartFile;

import com.eva.backend.model.Experimentation;
import com.eva.backend.model.PedagogicalContext;
import com.eva.backend.model.User;
import com.eva.backend.records.DownloadContent;
import com.eva.backend.repository.ExperimentationRepository;


@RestController
@RequestMapping("/file")
public class FileController {

    @Value("${app.export-dir}")
    private String exportDir;

    @Value("${app.import-dir}")
    private String importDir;

    @Autowired
    private ExperimentationRepository experimentationRepository;

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
        Path filePath = baseDir.resolve(filename).normalize();//nettoyer le path des ../ avec normalize et resolve concatène le dossier au nom de fichier        
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

    @PostMapping("/import")
    public ResponseEntity<String> importFile(@RequestParam("file") MultipartFile file, Long id) throws IOException {
        if (file == null || file.isEmpty()) {
            return ResponseEntity.status(HttpStatus.BAD_REQUEST)
                    .body("Missing uploaded file");
        }

        String originalFileName = file.getOriginalFilename();//Récupère le path entier côté client
        if (originalFileName == null || originalFileName.isBlank()) {
            return ResponseEntity.status(HttpStatus.BAD_REQUEST)
                    .body("Missing original file name");
        }

        String safeFileName = Paths.get(originalFileName).getFileName().toString();//supprimer les dossiers dans le nom pr ne garder que le nom du fichier
        String extension = getFileExtension(safeFileName);
        if (!"xls".equals(extension) && !"xlsx".equals(extension) && !"ods".equals(extension)) {
            return ResponseEntity.status(HttpStatus.BAD_REQUEST)
                    .body("Unsupported file extension. Allowed: xls, xlsx, ods");
        }

        String importedFileName = createImportedFileName(id, extension);
        Path baseDir = Paths.get(importDir).toAbsolutePath().normalize();
        Files.createDirectories(baseDir);
        Path filePath = baseDir.resolve(importedFileName).normalize();

        if (!filePath.startsWith(baseDir)) {
            return ResponseEntity.status(HttpStatus.BAD_REQUEST)
                    .body("Invalid file path");
        }

        Files.copy(file.getInputStream(), filePath, StandardCopyOption.REPLACE_EXISTING);

        return ResponseEntity.ok("File uploaded successfully: " + filePath.getFileName());
    }

    private String getFileExtension(String fileName) {
        int dotIndex = fileName.lastIndexOf('.');
        if (dotIndex < 0 || dotIndex == fileName.length() - 1) {
            return "";
        }
        return fileName.substring(dotIndex + 1).toLowerCase(Locale.ROOT);
    }

    private String createImportedFileName(Long id, String extension){
        //Nom du fichier de la forme : Variante_Annee_institution_nom_prenom_discipline.format

        /*Récupérer l'expé et l'utilisateur et le contexte */
        Experimentation experimentation = experimentationRepository.findById(id).orElseThrow();
        User user = experimentation.getUser();
        PedagogicalContext context = experimentation.getPedagogicalContext();

        String protocol = experimentation.getProtocol().split(":")[0];
        String date = LocalDate.now().toString();
        String institution = experimentation.getInstitution().getName();
        String lastName = user.getLastname();
        String firstName = user.getFirstname();
        String studyField = context.getStudyField(); 

        return protocol + "_" + date + "_" + institution + "_" + lastName + "_" + firstName + "_" + studyField + "." + extension;
    }
    
}
