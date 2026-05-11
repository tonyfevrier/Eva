package com.eva.backend.service;

import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.nio.file.StandardOpenOption;
import java.util.List;
import java.util.Locale;
import java.util.regex.Matcher;
import java.util.regex.Pattern;
import java.util.stream.Stream;

import org.springframework.http.HttpHeaders;
import org.springframework.stereotype.Service;
import org.springframework.web.multipart.MultipartFile;

import com.eva.backend.records.DownloadContent;
import com.eva.backend.utils.fileInterfaces.FileExportStrategy;
import com.eva.backend.utils.fileInterfaces.FileImportStrategy;

@Service
public class FileService {

    /* 
    Classe qui gère l'import, l'export, la suppression, la recherche de 
    fichiers dans un dossier donné

    Les services sont chargés dès le début et pas à chaque requête.
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

    private byte[] getExportFileContent(String filename, String exportDir) throws IOException{
        Path baseDir = getBaseDir(exportDir);
        Path filePath = writeFilePath(filename, baseDir);
        return Files.readAllBytes(filePath);
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
        Path baseDir = getBaseDir(strategy.getImportDir());
        deleteExistingExperimentationFile(baseDir, id);
        String importedFileName = strategy.createImportedFileName(id, extension);
        Path importedFilePath = writeFilePath(importedFileName, baseDir);
        strategy.copy(file, importedFilePath);
    }

    private Path getBaseDir(String directory) throws IOException{
        Path baseDir = Paths.get(directory).toAbsolutePath().normalize();
        Files.createDirectories(baseDir);
        return baseDir;
    }

    private void deleteExistingExperimentationFile(Path baseDir, Long id) throws IOException{
        String expectedPrefix = id + "_";

        try (Stream<Path> files = Files.list(baseDir)) {
            Path fileToDelete = files
                            .filter(Files::isRegularFile)
                            .filter(path -> path.getFileName().toString().startsWith(expectedPrefix))
                            .findFirst()
                            .orElse(null);

            if (fileToDelete != null) {
                Files.deleteIfExists(fileToDelete);
            }
        } 
    }

    private Path writeFilePath(String filename, Path baseDir) throws IOException{
        /* Récupère le nom du dossier et du fichier inclus et les assemble, crée éventuellement le dossier si inexistant */
        Path filePath = baseDir.resolve(filename).normalize();//nettoyer le path des ../ avec normalize et resolve concatène le dossier au nom de fichier        
        return filePath;
    }

    public List<String> getExperimentationFileNamesByType(Path directory, String importType, Long id) throws IOException {
        /* importType permet de choisir les fichiers à renvoyer (tests.pdf ou questionnaires.pdf) */
        try (Stream<Path> files = Files.list(directory)) {
            List<String> fileNames = files
                        .filter(Files::isRegularFile)
                        .map(path -> path.getFileName().toString())
                        .filter(name -> 
                            {
                            return name.contains(importType) && fileIdEqualsExperimentationId(name, id);
                         })
                        .sorted()
                        .toList();
            return fileNames;
        }
    }

    private boolean fileIdEqualsExperimentationId(String fileName, Long id){
        Pattern FILE_NAME_PATTERN = Pattern.compile("^[a-z]+_id(\\d+)_\\d+\\.[^.]+$");
        Matcher matcher = FILE_NAME_PATTERN.matcher(fileName);

        if (!matcher.matches()) {
            throw new IllegalArgumentException("Nom de fichier invalide : " + fileName);
        }

        Long number = Long.parseLong(matcher.group(1));
        return number.equals(id);
    }

    public void deleteFiles(Path testsDirectory, List<String> fileNames) throws IOException{
        for (String fileName : fileNames) {
            String safeFileName = Paths.get(fileName).getFileName().toString();
            Path targetFile = testsDirectory.resolve(safeFileName).normalize();
            Files.deleteIfExists(targetFile);
        }
    }

    public List<String> getExperimentationFileNames(Path directory, Long id) throws IOException {
        try (Stream<Path> files = Files.list(directory)) {
            List<String> experimentationFiles = files
                        .filter(Files::isRegularFile)
                        .map(path -> path.getFileName().toString())
                        .filter(name -> fileIdEqualsExperimentationId(name, id))
                        .sorted()
                        .toList();
            return experimentationFiles;
        }
    }

    public void registerFile(String directory, String outputFileName, byte[] content) throws IOException {
        Path baseDir = getBaseDir(directory);
        Path filePath = writeFilePath(outputFileName, baseDir);
        Files.write(filePath, content, StandardOpenOption.CREATE, StandardOpenOption.TRUNCATE_EXISTING);
    }

    public String findXlsFileByExperimentationId(Path directory, Long id) throws IOException {
        String expectedPrefix = id + "_";

        try (Stream<Path> files = Files.list(directory)) {
            return files
                    .filter(Files::isRegularFile)
                    .map(path -> path.getFileName().toString())
                    .filter(name -> name.startsWith(expectedPrefix))
                    .filter(this::isSpreadsheetFile)
                    .sorted()
                    .findFirst()
                    .orElseThrow(() -> new IllegalArgumentException("Vous ne pouvez générer le pdf tant que vous n'avez pas réimporté le tableur contenant vos données"));
        }
    }

    private boolean isSpreadsheetFile(String fileName) {
        String lowerName = fileName.toLowerCase();
        return lowerName.endsWith(".xls") || lowerName.endsWith(".xlsx") || lowerName.endsWith(".ods");
    }
}
