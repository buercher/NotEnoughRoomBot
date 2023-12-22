package projectSetup;

import com.fasterxml.jackson.core.type.TypeReference;
import com.fasterxml.jackson.databind.DeserializationFeature;
import com.fasterxml.jackson.databind.ObjectMapper;
import org.jetbrains.annotations.NotNull;
import utils.jsonObjects.JsonRoomArchitecture;

import java.io.File;
import java.io.IOException;
import java.nio.charset.Charset;
import java.nio.file.Files;
import java.nio.file.Paths;
import java.util.*;

/**
 * The AllValidRoomToJson class processes creates a list of all valid rooms.
 * Because the EPFL website does not have a consistent naming convention for rooms,
 * this class is used to create a list of all possible cases of room names.
 */
public class AllValidRoomToJson {

    private static final String EPFL_ROOM_LIST_PATH = "database/SetupData/roomChecking/fromEPFL/";
    private static final String FLEP_ROOM_LIST_PATH = "database/SetupData/roomChecking/fromFLEP/";

    /**
     * Private constructor to prevent instantiation of this utility class.
     */
    private AllValidRoomToJson() {

    }

    /**
     * Creates a list of all valid rooms.
     *
     * @throws IOException If an I/O error occurs
     */
    public static void find() throws IOException {
        List<JsonRoomArchitecture> jsonRoomArchitecture;
        File epflDirectory = new File(EPFL_ROOM_LIST_PATH);
        File flepDirectory = new File(FLEP_ROOM_LIST_PATH);

        List<String> AllString = getStringList(flepDirectory, epflDirectory);
        Collections.sort(AllString);

        // Load JSON file
        File roomsDataJson = new File("database/SetupData/roomsDataJson.json");

        // Create ObjectMapper
        try {
            Set<String> smallString = new HashSet<>();
            AllString.forEach(l ->
                    smallString.add(l.replaceAll("[^A-Za-z0-9]", "")));

            ObjectMapper objectMapper = new ObjectMapper();
            objectMapper.enable(DeserializationFeature.ACCEPT_SINGLE_VALUE_AS_ARRAY);

            JsonRoomArchitecture[] jsonRoomsDataJson =
                    objectMapper
                            .readValue(
                                    roomsDataJson,
                                    JsonRoomArchitecture[].class);
            jsonRoomArchitecture = new ArrayList<>(
                    Arrays.stream(jsonRoomsDataJson)
                            .filter(l -> smallString.contains(l.getRooms()))
                            .toList());
        } catch (IOException e) {
            throw new RuntimeException(e);
        }

        ObjectMapper objectMapper = new ObjectMapper();
        String jsonString = objectMapper.writeValueAsString(AllString);
        Files.write(Paths.get("resources/allValidRooms.json"),
                Collections.singleton(jsonString), Charset.defaultCharset());

        try {
            File validRoomData = new File("database/validRoomData.json");
            objectMapper.writeValue(validRoomData, jsonRoomArchitecture);
        } catch (IOException e) {
            e.fillInStackTrace();
        }
    }

    /**
     * Retrieves a list of all room names from the EPFL and FLEP directories.
     *
     * @param flepDirectory The directory containing the FLEP room data
     * @param epflDirectory The directory containing the EPFL room data
     * @return A list of all room names
     */
    @NotNull
    private static List<String> getStringList(File flepDirectory, File epflDirectory) {
        List<File> allFiles = new ArrayList<>();

        allFiles.addAll(List.of(Objects.requireNonNull(epflDirectory.listFiles())));
        allFiles.addAll(List.of(Objects.requireNonNull(flepDirectory.listFiles())));

        ObjectMapper objectMapper = new ObjectMapper();
        TypeReference<List<String>> typeRef = new TypeReference<>() {
        };

        return new ArrayList<>(allFiles.stream().map(l -> {
            try {
                return objectMapper.readValue(l, typeRef);
            } catch (IOException e) {
                throw new RuntimeException(e);
            }
        }).flatMap(List::stream).toList());
    }
}
