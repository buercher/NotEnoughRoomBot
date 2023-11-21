import java.io.*;
import java.nio.charset.Charset;
import java.nio.charset.StandardCharsets;
import java.nio.file.Files;
import java.util.Collections;
import java.util.List;

public class FileManipulation {
    public static void appendToFile(String filePath, String value) {
        try {
            File file = new File(filePath);

            if (!file.exists()) {
                // If the file does not exist, create a new file
                if (!file.createNewFile()) {
                    System.out.println("Failed to create file: " + filePath);
                    return;
                }
                Files.write(file.toPath(), Collections.singleton(value), Charset.defaultCharset());
                return;
            }
            List<String> lines = Files.readAllLines(file.toPath(), StandardCharsets.UTF_8);
            lines.add(value);
            Files.write(file.toPath(), lines, Charset.defaultCharset());

        } catch (IOException e) {
            System.out.println("Error processing file: " + filePath);
            e.fillInStackTrace();
        }
    }
}
