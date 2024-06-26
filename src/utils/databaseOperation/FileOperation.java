package utils.databaseOperation;

import me.tongfei.progressbar.ProgressBar;
import org.apache.commons.lang3.StringUtils;
import telegramBots.hoursSearch.MergeRanges;

import java.io.File;
import java.io.IOException;
import java.nio.charset.Charset;
import java.nio.charset.StandardCharsets;
import java.nio.file.FileAlreadyExistsException;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.StandardOpenOption;
import java.util.Collections;
import java.util.List;

/**
 * The FileOperation class provides multiple methods for performing file-related operations.
 */
public class FileOperation {

    /**
     * Private constructor to prevent instantiation of this utility class.
     */
    private FileOperation() {
    }

    /**
     * Creates a new file or replaces it if it already exists
     *
     * @param file The File to be created.
     * @throws IOException If an I/O error occurs
     */
    public static void create(File file) throws IOException {
        Path path = file.toPath();
        try {
            Files.createFile(path);
        } catch (FileAlreadyExistsException e) {
            Files.delete(path);
            Files.createFile(path);
        }
    }

    /**
     * <p>Appends the specified value to the end of the file indicated by the file path.</p>
     * <p>If the file does not exist, it creates a new file and appends the content.</p>
     *
     * @param filePath The path of the file to which the content will be appended.
     * @param value    The value to be appended to the file.
     * @throws IOException If an I/O error occurs
     */
    public static void appendToFile(String filePath, String value) throws IOException {
        Path path = Path.of(filePath);
        try {
            Files.writeString(path,
                    value + System.lineSeparator(),
                    StandardOpenOption.CREATE,
                    StandardOpenOption.APPEND);
        } catch (FileAlreadyExistsException e) {
            List<String> lines = Files.readAllLines(path, Charset.defaultCharset());
            lines.add(value);
            Files.write(path, lines, Charset.defaultCharset());
        }
    }

    /**
     * <p>Creates a new file or replaces it if it already exists and writes the specified content to it.</p>
     * <p>If the file does not exist, it creates a new file and writes the content.</p>
     *
     * @param filePath The path of the file to which the content will be written.
     * @throws IOException If an I/O error occurs
     */
    public static void FinalFileCreation(ProgressBar progressBar, String path, String filePath) throws IOException {
        File file = new File(filePath);
        if (file.exists()) {
            List<String> lines = Files.readAllLines(file.toPath(), StandardCharsets.UTF_8);
            Collections.sort(lines);
            MergeRanges.mergeAdjacentRanges(lines);

            Files.write(file.toPath(), lines, Charset.defaultCharset());
        }
        progressBar.step();
        progressBar.setExtraMessage(StringUtils.rightPad(" " + path, 14));
        progressBar.refresh();
    }
}
