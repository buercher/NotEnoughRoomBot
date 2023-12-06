package databaseOperation;

import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.core.type.TypeReference;
import com.fasterxml.jackson.databind.ObjectMapper;

import java.io.File;
import java.io.IOException;
import java.nio.charset.Charset;
import java.nio.charset.StandardCharsets;
import java.nio.file.Files;
import java.nio.file.Paths;
import java.text.SimpleDateFormat;
import java.util.*;

/**
 * The JsonOperation class provides multiple methods for performing JSON-related operations.
 */
public class JsonOperation {

    /**
     * Private constructor to prevent instantiation of this utility class.
     */
    private JsonOperation() {
    }

    /**
     * <p>Creates a JSON file containing the data from the 'fromEPFL.json' and 'roomWithIssue.json' files.
     * The data is organized in a map with the following structure:</p>
     * <pre>
     * {@code
     *     "AAC006": [4, 5, 10],
     *     "BC05": [1, 2, 3, 4, 5, 10],
     *     "BC06": [1, 2, 8, 9, 10],
     *     "BC07": [3, 6, 7, 8, 9, 10],
     * }
     * </pre>
     *
     * @throws IOException If an I/O error occurs
     */
    public static void makeFile() throws IOException {
        Map<String, Set<Integer>> roomSchedules = new LinkedHashMap<>();

        Date currentDate = new Date();
        SimpleDateFormat dateFormat = new SimpleDateFormat("yyyy-MM-dd");
        String currentDateString = dateFormat.format(currentDate);

        List<String> paths = Files.readAllLines(Paths.get("resources/list"));
        ObjectMapper objectMapper = new ObjectMapper();
        List<String> pathsEPFL = objectMapper.readValue(
                new File("database/fromEPFL.json"), new TypeReference<>() {
                });

        List<String> pathsRoomWithIssue = objectMapper.readValue(
                new File("database/roomWithIssue.json"), new TypeReference<>() {
                });


        for (String path : paths) {
            roomSchedules.put(path, new TreeSet<>());
            if (pathsEPFL.contains(path)) {
                AddToMap(roomSchedules,
                        path,
                        "database/" + "EPFL-" + currentDateString + "/" + path);
            } else if (pathsRoomWithIssue.contains(path)) {
                AddToMap(roomSchedules,
                        path,
                        "database/" + "FLEP-" + currentDateString + "/" + path);
            } else {
                throw new IllegalStateException("The data wasn't attributed to any of the maps");
            }
        }

        // BC07 and BC08 are merged into one room in the EPFL schedule
        if (!roomSchedules.get("BC07-08").isEmpty()) {
            roomSchedules.replace("BC07", roomSchedules.get("BC07-08"));
            roomSchedules.replace("BC08", roomSchedules.get("BC07-08"));
        }
        roomSchedules.remove("BC07-08");

        try {
            String json = objectMapper.writeValueAsString(roomSchedules);
            File file = new File("database/data.json");

            FileOperation.create(file);
            Files.write(file.toPath(), Collections.singleton(json), Charset.defaultCharset());

        } catch (JsonProcessingException e) {
            e.fillInStackTrace();
        }
    }

    /**
     * Adds the data from the file with the given path to the map with the given name.
     *
     * @param map      The map to which the data is added
     * @param roomName The name of the map
     * @param filePath The path of the file from which the data is read
     * @throws IOException If an I/O error occurs
     */
    private static void AddToMap(Map<String, Set<Integer>> map,
                                 String roomName,
                                 String filePath) throws IOException {

        File file = new File(filePath);
        if (file.exists()) {
            List<String> lines = Files.readAllLines(file.toPath(), StandardCharsets.UTF_8);
            for (String line : lines) {
                for (int i = Integer.parseInt(line.substring(0, 2)); i < Integer.parseInt(line.substring(3, 5)); i++) {
                    map.get(roomName).add(i);
                }
            }
        }
    }

    /**
     * Creates a JSON file with the data from the given set.
     *
     * @param JsonSet The set to be written
     * @param name    The name of the file
     * @throws IOException If an I/O error occurs
     */
    public static void JsonFileWrite(Set<String> JsonSet, String name) throws IOException {
        if (!JsonSet.isEmpty()) {
            File jsonFile = new File("database/roomChecking/" + name + ".json");
            try {
                ObjectMapper objectMapper = new ObjectMapper();
                String jsonString = objectMapper.writeValueAsString(JsonSet);

                FileOperation.create(jsonFile);
                Files.write(jsonFile.toPath(), Collections.singleton(jsonString), Charset.defaultCharset());

            } catch (JsonProcessingException e) {
                e.fillInStackTrace();
            }
        }
    }
}
