package databaseOperation;

import java.io.File;
import java.io.IOException;
import java.nio.file.InvalidPathException;
import java.util.ArrayList;
import java.util.List;

public class FolderOperation {

    private FolderOperation(){}

    public static void deleteFoldersExcept(String directoryPath,
                                           String specifiedFolderName,
                                           String source) throws IOException {

        File directory = new File(directoryPath);

        if (!directory.isDirectory()) {
            throw new InvalidPathException(directoryPath, "Invalid directory path");
        }

        File[] files = directory.listFiles();

        // Flag to check if the specified folder exists
        boolean specifiedFolderExists = false;

        // Iterate through the files and delete folders that do not match the specified name
        if (files != null) {
            for (File file : files) {
                if (file.isDirectory()) {
                    if (file.getName().equals(source + "-" + specifiedFolderName)) {
                        specifiedFolderExists = true;
                    } else if (!file.getName().equals("EPFL-" + specifiedFolderName)
                            && !file.getName().equals("FLEP-" + specifiedFolderName)
                            && !file.getName().equals("roomChecking")){
                        deleteDirectory(file);
                    }
                } else if (file.isFile()) {
                    if (!file.getName().equals("data.json")
                            && !file.getName().equals("fromEPFL.json")
                            && !file.getName().equals("roomWithIssue.json")){
                        if (!file.delete()) {
                            throw new IOException("Failed to create file: " + file.getPath());
                        }
                    }
                }
            }
        }

        // If the specified folder doesn't exist, create it
        if (!specifiedFolderExists) {
            File newFolder = new File(directoryPath, source + "-" + specifiedFolderName);

            if (!newFolder.mkdir()) {
                throw new IOException("Failed to create folder '" + specifiedFolderName);
            }
        }
    }

    public static void CreateFolderForTest() throws IOException {

        File roomChecking = new File("database/roomChecking");
        if (!roomChecking.exists()) {
            if (!roomChecking.mkdir()) {
                throw new IOException("Failed to create folder '" + roomChecking.getPath()+"'");
            }
        }
        List<File> rooms = new ArrayList<>();
        rooms.add(new File("database/roomChecking/fromEPFL"));
        rooms.add(new File("database/roomChecking/fromFLEP"));
        rooms.add(new File("database/roomChecking/roomNotSearchable"));
        rooms.add(new File("database/roomChecking/roomWithIssue"));
        for (File room : rooms) {
            if (!room.exists()) {
                if (!room.mkdir()) {
                    throw new IOException("Failed to create folder '" + room.getPath()+"'");
                }
            }
        }
    }

    private static void deleteDirectory(File directory) throws IOException {
        File[] files = directory.listFiles();
        if (files != null) {
            for (File file : files) {
                if (file.isDirectory()) {
                    deleteDirectory(file);
                } else {
                    if (!file.delete()) {
                        throw new IOException("Failed to delete file: " + file.getPath());
                    }
                }
            }
        }
        // Delete the empty directory after deleting its contents
        if (!directory.delete()) {
            throw new IOException("Failed to delete directory: " + directory.getPath());
        }
    }
}
