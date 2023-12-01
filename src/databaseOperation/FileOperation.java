package databaseOperation;

import java.io.File;
import java.io.IOException;
import java.nio.charset.Charset;
import java.nio.file.*;
import java.util.List;

public class FileOperation {

    private FileOperation(){}

    public static void create(File file) throws IOException {
        Path path = file.toPath();
        try {
            Files.createFile(path);
        } catch (FileAlreadyExistsException e) {
            // If file already exists, attempt to delete and create again
            Files.delete(path);
            Files.createFile(path);
        }
    }

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
}
