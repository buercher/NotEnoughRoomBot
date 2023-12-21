package projectSetup;

import com.fasterxml.jackson.core.type.TypeReference;
import com.fasterxml.jackson.databind.DeserializationFeature;
import com.fasterxml.jackson.databind.ObjectMapper;
import utils.jsonObjects.JsonRoomArchitecture;
import org.jetbrains.annotations.NotNull;

import java.io.File;
import java.io.IOException;
import java.nio.charset.Charset;
import java.nio.file.Files;
import java.nio.file.Paths;
import java.util.*;

public class AllValidRoomToJson {

    /**
     * Private constructor to prevent instantiation of this utility class.
     */
    private AllValidRoomToJson() {

    }

    private static final String EPFL_ROOM_LIST_PATH = "database/SetupData/roomChecking/fromEPFL/";
    private static final String FLEP_ROOM_LIST_PATH = "database/SetupData/roomChecking/fromFLEP/";


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
