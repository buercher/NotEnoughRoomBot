package ProjectSetup.searchingRoom;

import java.io.File;
import java.io.IOException;
import java.nio.file.Files;
import java.util.*;

/**
 * The AllCases class processes creates a list of all possible cases of room names.
 * Because the EPFL website does not have a consistent naming convention for rooms,
 */
public class AllCases {

    /**
     * Private constructor to prevent instantiation of this utility class.
     */
    private AllCases() {
    }

    private static List<String> outputList;
    private static int inputLength = 0;
    private static int outputLength = 0;

    /**
     * Creates a list of all possible cases of room names.
     *
     * @throws IOException If an I/O error occurs
     */
    public static void find() throws IOException {
        File roomList = new File("database/SetupData/RoomList");
        if (!roomList.exists()) {
            if (!roomList.mkdir()) {
                throw new IOException("Failed to create folder '" + roomList.getPath() + "'");
            }
        }
        File input = new File("database/SetupData/RoomToConvert");
        File[] files = input.listFiles();
        if (files != null) {
            for (File file : files) {

                outputList = new ArrayList<>();

                List<String> inputList = Files.readAllLines(file.toPath());

                inputLength += inputList.size();
                for (String inputString : inputList) {
                    recursiveDash(inputString);
                }
                outputLength += outputList.size();
                File output = new File("database/SetupData/RoomList/" + file.getName());
                Files.write(output.toPath(), outputList);
            }
        }
        System.out.println("Room before recursive search : " + inputLength);
        System.out.println("Room after recursive search  : " + outputLength);
    }

    /**
     * Replaces the first dash in the string with a point and calls recursivePoint.
     *
     * @param roomString The string to be processed
     */
    public static void recursiveDash(String roomString) {
        if (roomString.contains("-")) {
            recursiveDash(roomString.replaceFirst("-", ""));
        }
        recursivePoint(roomString);
    }

    /**
     * Replaces the first point in the string with an underscore and calls recursiveUnderscore.
     *
     * @param roomString The string to be processed
     */
    public static void recursivePoint(String roomString) {
        if (roomString.contains(".")) {
            recursivePoint(roomString.replaceFirst("\\.", ""));
            recursivePoint(roomString.replaceFirst("\\.", "-"));
        }
        recursiveUnderscore(roomString);
    }

    /**
     * Replaces the first underscore in the string with a space and calls recursiveSpace.
     *
     * @param roomString The string to be processed
     */
    public static void recursiveUnderscore(String roomString) {
        if (roomString.contains("_")) {
            recursiveUnderscore(roomString.replaceFirst("_", ""));
            recursiveUnderscore(roomString.replaceFirst("_", "-"));
        }
        recursiveSpace(roomString);

    }

    /**
     * Replaces the first space in the string with a dash and adds the string to the output list.
     *
     * @param roomString The string to be processed
     */
    public static void recursiveSpace(String roomString) {
        if (roomString.contains(" ")) {
            recursiveSpace(roomString.replaceFirst(" ", ""));
            recursiveSpace(roomString.replaceFirst(" ", "-"));
        } else {
            outputList.add(roomString);
        }
    }
}
