package projectSetup.searchingRoom;

import utils.databaseOperation.UrlFetcher;
import me.tongfei.progressbar.ProgressBar;
import org.apache.commons.lang.StringUtils;

import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.util.HashSet;
import java.util.List;
import java.util.Set;

import static utils.databaseOperation.JsonOperation.JsonFileWrite;

/**
 * The TestEPFL class provides multiple methods for testing existence of room in EPFL website.
 */
public class TestEPFL {

    private static final String FOLDER_PATH = "database/SetupData/roomChecking/";

    /**
     * Private constructor to prevent instantiation of this utility class.
     */
    private TestEPFL() {
    }

    /**
     * Reads the file containing the list of rooms and returns it as a list of strings.
     *
     * @param filePath The path of the file containing the list of rooms
     * @return The list of rooms as a list of strings
     * @throws IOException If an I/O error occurs while reading the file
     */
    private static List<String> fetchStringsFromFile(String filePath) throws IOException {
        Path path = Paths.get(filePath);
        return Files.readAllLines(path);
    }

    /**
     * Test the existence of room in EPFL website.
     *
     * @param buildingName The name of the building
     * @throws IOException If an I/O error occurs
     * @see UrlFetcher#EPFL(String)
     */
    public static void test(String buildingName, ProgressBar pbEPFL) throws IOException {
        Set<String> roomWithIssue = new HashSet<>();
        Set<String> fromEPFL = new HashSet<>();
        List<String> paths = fetchStringsFromFile("database/SetupData/RoomList/" + buildingName);
        for (String path : paths) {
            String data = UrlFetcher.EPFL(path);
            if (data.contains("Pas d'information pour cette salle")) {
                roomWithIssue.add(path);
            } else {
                fromEPFL.add(path);
            }
            pbEPFL.step();
            pbEPFL.setExtraMessage(StringUtils.rightPad(" EPFL: " + path, 20));
            pbEPFL.refresh();
        }
        JsonFileWrite(roomWithIssue, "roomWithIssue/" + buildingName,FOLDER_PATH);
        JsonFileWrite(fromEPFL, "fromEPFL/" + buildingName,FOLDER_PATH);
    }
}
