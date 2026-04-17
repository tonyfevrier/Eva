package com.eva.backend.controller;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.util.List;
import java.util.Map;
import java.util.stream.Stream;

import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.multipart.MultipartFile;

import com.eva.backend.records.DownloadContent;
import com.eva.backend.service.FileService;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;



@RestController
@RequestMapping("/file")
public class FileController {

    @Autowired
    private FileService fileService;

    @Value("${app.pdf-dir}")
    private String pdfDir;

    @PostMapping("/export")
    public ResponseEntity<byte[]> exportFile(String entry, String exportType) throws IOException {
        if (entry == null || exportType == null) {
            return ResponseEntity.status(HttpStatus.BAD_REQUEST).body("Missing request body".getBytes());
        }

        DownloadContent content = fileService.prepareContentForDownload(exportType, entry);        
        return ResponseEntity.ok().headers(content.headers()).body(content.fileBytes());
    }
    

    @PostMapping("/import")
    public ResponseEntity<String> importFile(@RequestParam("file") MultipartFile file, Long id, String importType) throws IOException {
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
        String extension = fileService.getFileExtension(safeFileName);
        List<String> authorizedExtensions = List.of("xls", "xlsx", "ods", "pdf");
        if (!authorizedExtensions.contains(extension)) {
            return ResponseEntity.status(HttpStatus.BAD_REQUEST)
                    .body("Unsupported file extension.");
        }

        fileService.registerImportedFile(importType, file, id, extension);
        return ResponseEntity.ok("File uploaded successfully");
    }

    @GetMapping("/getFileNames/{id}")
    public ResponseEntity<?> getPdfFileNames(String importType, @PathVariable Long id) throws IOException {
        if (importType == null || importType.isBlank()) {
            return ResponseEntity.status(HttpStatus.BAD_REQUEST)
                    .body(Map.of("message", "Missing importType"));
        }

        Path testsDirectory = Paths.get(pdfDir).toAbsolutePath().normalize();
        List<String> fileNames;

        if (!Files.isDirectory(testsDirectory)) {
            return ResponseEntity.ok(Map.of("fileNames", List.of()));
        }

        try (Stream<Path> files = Files.list(testsDirectory)) {
            fileNames = files
                        .filter(Files::isRegularFile)
                        .map(path -> path.getFileName().toString())
                        .filter(name -> 
                            {
                            return name.contains(importType) && fileService.fileIdEqualsExperimentationId(name, id);
                         })
                        .sorted()
                        .toList();
        }

        return ResponseEntity.ok(Map.of("fileNames", fileNames));
    }
}

