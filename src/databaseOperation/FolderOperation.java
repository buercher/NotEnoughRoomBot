package databaseOperation;

import java.io.File;
import java.io.IOException;
import java.nio.file.Files;
import java.util.Arrays;
import java.util.Comparator;
import java.util.List;
import java.nio.file.Path;
import java.util.stream.Stream;

/**
 * The FolderOperation class provides multiple methods for performing folder-related operations.
 */
public class FolderOperation {

    /**
     * Private constructor to prevent instantiation of this utility class.
     */
    private FolderOperation() {
    }

    /**
     * Deletes all files and folders within the 'database' directory except the ones specified in the parameters.
     *
     * @param currentDateString The current date in the format "yyyy-MM-dd".
     * @throws IOException If an I/O error occurs while deleting files.
     * @see <a href="https://stackoverflow.com/a/779519">https://stackoverflow.com/a/779519</a>
     */
    public static void deleteFoldersExcept(String currentDateString) throws IOException {

        File directory = new File("database");
        File[] files = directory.listFiles();

        // List of files and folders that should not be deleted
        List<String> filePaths = Arrays.asList(
                "data.json",
                "fromEPFL.json",
                "roomWithIssue.json"
        );
        List<String> folderPaths = Arrays.asList(
                "roomChecking",
                "EPFL-" + currentDateString,
                "FLEP-" + currentDateString
        );

        // Delete all files and folders that are not in the lists above
        if (files != null) {
            for (File file : files) {
                if (file.isFile() && !filePaths.contains(file.getName())) {
                    Files.delete(file.toPath());
                } else if (file.isDirectory() && !folderPaths.contains(file.getName())) {

                    // Delete all files and folders within the directory
                    try (Stream<Path> pathStream = Files.walk(file.toPath())) {
                        pathStream
                                .sorted(Comparator.reverseOrder())
                                .map(Path::toFile)
                                .forEach(File::deleteOnExit);
                    } catch (IOException e) {
                        e.fillInStackTrace();
                    }
                }
            }
        }

        // Create the folders if they don't exist
        for (String folderPath : folderPaths) {
            File folder = new File("database/" + folderPath);
            if (!folder.exists() && !folder.mkdirs()) {
                throw new IOException("Failed to create folder '" + folder.getPath() + "'");
            }
        }
    }

    /**
     * Creates the folders required for testing.
     *
     * @throws IOException If an I/O error occurs while creating folders.
     */
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
