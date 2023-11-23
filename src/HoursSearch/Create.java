package HoursSearch;

import java.io.File;
import java.io.IOException;

public class Create {

    private Create(){}

    public static void file(File file) throws IOException {
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
}
