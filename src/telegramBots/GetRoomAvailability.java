package telegramBots;

import com.fasterxml.jackson.core.type.TypeReference;
import com.fasterxml.jackson.databind.ObjectMapper;
import utils.jsonObjects.Datajson;

import java.io.File;
import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Path;
import java.util.*;

/**
 * The GetRoomAvailability class provides method for getting the availability of rooms (Will possibly remove later).
 */
public class GetRoomAvailability {

    /**
     * Private constructor to prevent instantiation of this utility class.
     */
    private GetRoomAvailability() {
    }

    /**
     * Gets the availability of rooms.
     *
     * @param Start The start hour
     * @param End   The end hour
     * @return The availability of rooms
     * @throws IOException If an I/O error occurs
     */
    public static Map<String, List<String>> search(int Start, int End) throws IOException {
        Map<String, List<String>> roomsList = new TreeMap<>(Map.of(
                "EPFL", new ArrayList<>(),
                "FLEP", new ArrayList<>()));

        File jsonFile = new File("database/data.json");
        String jsonString = Files.readString(jsonFile.toPath());
        List<String> rooms = new ArrayList<>();
        Files.readAllLines(Path.of("resources/list")).forEach(room ->
                rooms.add(room.replaceAll("-", "")
                        .replaceAll(" ", "")
                        .replaceAll("\\.", "")
                        .replaceAll("_", "")));
        TypeReference<Map<String, Datajson>> typeRef = new TypeReference<>() {
        };
        Map<String, Datajson> result = new ObjectMapper().readValue(jsonString, typeRef);

        for (int i = Start; i < End; i++) {
            Set<String> keys = Set.copyOf(result.keySet());
            for (String room : keys) {
                if (result.get(room).getHoraire().contains(i) || !rooms.contains(room)) {
                    result.remove(room);
                }
            }
        }
        for (String room : result.keySet()) {
            if (Objects.equals(result.get(room).getSource(), "EPFL")) {
                roomsList.get("EPFL").add(room);
            }
        }
        if (roomsList.get("EPFL").isEmpty()) {
            roomsList.remove("EPFL");
        } else {
            Collections.sort(roomsList.get("EPFL"));
        }
        for (String room : result.keySet()) {
            if (Objects.equals(result.get(room).getSource(), "FLEP")) {
                roomsList.get("FLEP").add(room);
            }
        }
        if (roomsList.get("FLEP").isEmpty()) {
            roomsList.remove("FLEP");
        } else {
            Collections.sort(roomsList.get("FLEP"));
        }
        return roomsList;
    }
}
