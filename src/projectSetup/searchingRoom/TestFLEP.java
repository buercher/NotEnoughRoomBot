package projectSetup.searchingRoom;

import com.fasterxml.jackson.core.type.TypeReference;
import com.fasterxml.jackson.databind.ObjectMapper;
import me.tongfei.progressbar.ProgressBar;
import org.apache.commons.lang.StringUtils;

import java.io.BufferedReader;
import java.io.File;
import java.io.IOException;
import java.io.InputStreamReader;
import java.net.HttpURLConnection;
import java.net.URL;
import java.nio.file.Files;
import java.nio.file.Path;
import java.util.HashSet;
import java.util.List;
import java.util.Set;

import static utils.databaseOperation.JsonOperation.JsonFileWrite;

/**
 * The TestFLEP class provides multiple methods for testing existence of room in FLEP website.
 */
public class TestFLEP {

    private static final String FOLDER_PATH = "database/SetupData/roomChecking/";

    /**
     * Private constructor to prevent instantiation of this utility class.
     */
    private TestFLEP() {
    }

    /**
     * Test the existence of room in FLEP website.
     *
     * @param buildingName The name of the building
     * @throws IOException If an I/O error occurs
     */
    public static void test(String buildingName, ProgressBar pbFLEP) throws IOException {
        String BASE_URL = "https://occupancy-backend-e150a8daef31.herokuapp.com/api/rooms/";
        Set<String> roomNoSearchable = new HashSet<>();
        Set<String> fromFLEP = new HashSet<>();
        ObjectMapper objectMapper = new ObjectMapper();
        List<String> paths = objectMapper.readValue(
                new File("database/SetupData/roomChecking/roomWithIssue/" + buildingName + ".json"),
                new TypeReference<>() {
                }
        );
        if (Files.exists(Path.of("database/SetupData/roomChecking/fromEPFL/" + buildingName + ".json"))) {
            List<String> temp = objectMapper.readValue(
                    new File("database/SetupData/roomChecking/fromEPFL/" + buildingName + ".json"),
                    new TypeReference<>() {
                    }
            );
            pbFLEP.stepBy(temp.size());
        }
        for (String path : paths) {
            URL url = new URL(BASE_URL + path);
            HttpURLConnection connection = (HttpURLConnection) url.openConnection();
            connection.setRequestMethod("GET");
            try (BufferedReader ignored = new BufferedReader(new InputStreamReader(connection.getInputStream()))) {
                fromFLEP.add(path);
            } catch (java.io.FileNotFoundException e) {
                roomNoSearchable.add(path);
            }
            pbFLEP.step();
            pbFLEP.setExtraMessage(StringUtils.rightPad(" FLEP: " + path, 20));
            pbFLEP.refresh();
        }
        JsonFileWrite(roomNoSearchable, "roomNotSearchable/" + buildingName, FOLDER_PATH);
        JsonFileWrite(fromFLEP, "fromFLEP/" + buildingName, FOLDER_PATH);
    }
}
