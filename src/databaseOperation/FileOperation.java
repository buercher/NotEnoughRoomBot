package databaseOperation;

import java.io.File;
import java.io.IOException;
import java.nio.charset.Charset;
import java.nio.charset.StandardCharsets;
import java.nio.file.Files;
import java.util.Collections;
import java.util.List;

public class FileOperation {

    private FileOperation(){}

    public static void create(File file) throws IOException {
        if (!file.exists()) {
            // If the file does not exist, create a new file
            if (!file.createNewFile()) {
                throw new IOException("Failed to create file: " + file.getPath());
            }
        }
        else {
            if (file.delete()) {
                if (!file.createNewFile()) {
                    throw new IOException("Failed to create file: " + file.getPath());
                }
            }
            else{
                throw new IOException("Failed to create file: " + file.getPath());
            }
        }
    }

    public static void appendToFile(String filePath, String value) throws IOException {
        File file = new File(filePath);

        if (!file.exists()) {
            // If the file does not exist, create a new file
            if (!file.createNewFile()) {
                throw new IOException("Failed to create file: " + file.getPath());
            }
            Files.write(file.toPath(), Collections.singleton(value), Charset.defaultCharset());
            return;
        }
        List<String> lines = Files.readAllLines(file.toPath(), StandardCharsets.UTF_8);
        lines.add(value);
        Files.write(file.toPath(), lines, Charset.defaultCharset());
    }
}
