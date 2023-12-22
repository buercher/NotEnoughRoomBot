package telegramBots;

import com.fasterxml.jackson.core.type.TypeReference;
import com.fasterxml.jackson.databind.ObjectMapper;
import utils.jsonObjects.Datajson;

import java.io.File;
import java.io.IOException;
import java.nio.file.Files;
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
    public static Map<String, List<String>> search(Set<String> listOfRoom, int Start, int End) throws IOException {
        Map<String, List<String>> roomsList = new TreeMap<>(Map.of(
                "EPFL", new ArrayList<>(),
                "FLEP", new ArrayList<>()));

        File jsonFile = new File("database/data.json");
        String jsonString = Files.readString(jsonFile.toPath());
        List<String> rooms = listOfRoom.stream().toList();
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
            if (result.get(room).getSource().equals("EPFL")) {
                roomsList.get("EPFL").add(room);
            }
        }
        Collections.sort(roomsList.get("EPFL"));

        for (String room : result.keySet()) {
            if (result.get(room).getSource().equals("FLEP")) {
                roomsList.get("FLEP").add(room);
            }
        }
        Collections.sort(roomsList.get("FLEP"));
        return roomsList;
    }
}
