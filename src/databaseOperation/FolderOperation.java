package databaseOperation;

import java.io.File;
import java.io.IOException;
import java.nio.file.Files;
import java.util.Arrays;
import java.util.Comparator;
import java.util.List;
import java.nio.file.Path;
import java.util.stream.Stream;

public class FolderOperation {

    private FolderOperation() {
    }

    public static void deleteFoldersExcept(String specifiedFolderName) throws IOException {

        File directory = new File("database");

        File[] files = directory.listFiles();

        List<String> filePaths = Arrays.asList(
                "data.json",
                "fromEPFL.json",
                "roomWithIssue.json"
        );
        List<String> folderPaths = Arrays.asList(
                "roomChecking",
                "EPFL-" + specifiedFolderName,
                "FLEP-" + specifiedFolderName
        );

        if (files != null) {
            for (File file : files) {
                if (file.isFile() && !filePaths.contains(file.getName())) {
                    Files.delete(file.toPath());
                } else if (file.isDirectory() && !folderPaths.contains(file.getName())) {
                    try (Stream<Path> pathStream = Files.walk(file.toPath())) {
                        pathStream
                                .sorted(Comparator.reverseOrder())
                                .map(Path::toFile)
                                .forEach(fileToDelete -> {
                                    if (!fileToDelete.delete()) {
                                        try {
                                            throw new IOException("Failed to create file: " + fileToDelete.getPath());
                                        } catch (IOException e) {
                                            throw new RuntimeException(e);
                                        }
                                    }
                                });
                    } catch (IOException e) {
                        e.fillInStackTrace();
                    }
                }
            }
        }

        for (String folderPath : folderPaths) {
            File folder = new File("database/"+folderPath);
            if (!folder.exists() && !folder.mkdirs()) {
                throw new IOException("Failed to create folder '" + folder.getPath() + "'");
            }
        }
    }

    public static void CreateFoldersForTest() throws IOException {
        List<String> folderPaths = Arrays.asList(
                "database/roomChecking",
                "database/roomChecking/fromEPFL",
                "database/roomChecking/fromFLEP",
                "database/roomChecking/roomNotSearchable",
                "database/roomChecking/roomWithIssue"
        );

        for (String folderPath : folderPaths) {
            File folder = new File(folderPath);
            if (!folder.exists() && !folder.mkdirs()) {
                throw new IOException("Failed to create folder '" + folder.getPath() + "'");
            }
        }
    }
}
