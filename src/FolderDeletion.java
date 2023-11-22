import java.io.File;

public class FolderDeletion {

    public static void deleteFoldersExcept(String directoryPath, String specifiedFolderName, String source) {
        File directory = new File(directoryPath);

        if (!directory.isDirectory()) {
            System.out.println("Invalid directory path.");
            return;
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
                            && !file.getName().equals("FLEP-" + specifiedFolderName)){
                        deleteDirectory(file);
                    }
                } else if (file.isFile()) {
                    if (!file.getName().equals("data.json")){
                        if (!file.delete()) {
                            System.out.println("Failed to create file '" + file.getPath() + "'.");
                        }
                    }
                }
            }
        }

        // If the specified folder doesn't exist, create it
        if (!specifiedFolderExists) {
            File newFolder = new File(directoryPath, source + "-" + specifiedFolderName);
            if (newFolder.mkdir()) {
                System.out.println("Folder '" + source + "-" + specifiedFolderName + "' created.");
            } else {
                System.out.println("Failed to create folder '" + specifiedFolderName + "'.");
            }
        }
    }

    private static void deleteDirectory(File directory) {
        File[] files = directory.listFiles();
        if (files != null) {
            for (File file : files) {
                if (file.isDirectory()) {
                    deleteDirectory(file);
                } else {
                    if (!file.delete()) {
                        System.out.println("Failed to create file '" + file.getPath() + "'.");
                    }
                }
            }
        }
        // Delete the empty directory after deleting its contents
        if (!directory.delete()) {
            System.out.println("Failed to create file '" + directory.getPath() + "'.");
        }
    }
}
