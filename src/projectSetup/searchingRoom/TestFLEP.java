package projectSetup.searchingRoom;

import com.fasterxml.jackson.core.type.TypeReference;
import com.fasterxml.jackson.databind.ObjectMapper;
import me.tongfei.progressbar.ProgressBar;
import okhttp3.OkHttpClient;
import okhttp3.Request;
import okhttp3.Response;
import org.apache.commons.lang.StringUtils;

import java.io.File;
import java.io.IOException;
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
            OkHttpClient client = new OkHttpClient();
            Request request = new Request.Builder()
                    .url(BASE_URL + path)
                    .get()
                    .addHeader("Origin", "https://occupancy.flep.ch")
                    .build();

            try (Response response = client.newCall(request).execute()) {
                if (response.code() == 404) {
                    roomNoSearchable.add(path);
                }
                else{
                    fromFLEP.add(path);
                }
            }
            pbFLEP.step();
            pbFLEP.setExtraMessage(StringUtils.rightPad(" FLEP: " + path, 20));
            pbFLEP.refresh();
        }
        JsonFileWrite(roomNoSearchable, "roomNotSearchable/" + buildingName, FOLDER_PATH);
        JsonFileWrite(fromFLEP, "fromFLEP/" + buildingName, FOLDER_PATH);
    }
}
