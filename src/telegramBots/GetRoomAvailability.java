package telegramBots;

import utils.jsonObjects.Datajson;

import java.util.*;

import static telegramBots.TelegramBotForOccupancy.dataJson;
import static telegramBots.TelegramBotForOccupancy.validRoomData;

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
     * This method is used to search for available rooms within a given time range.
     * It reads the data from a JSON file and filters out the rooms that are not available at the specified time.
     * The method then sorts the rooms based on their source (EPFL or FLEP) and returns a map with the sorted rooms.
     *
     * @param listOfRoom A set of room names to search for.
     * @param Start      The start time for the search.
     * @param End        The end time for the search.
     * @return A map with two keys (EPFL and FLEP),
     * each containing a list of available rooms from the respective source.
     */
    public static Map<String, List<String>> search(Set<String> listOfRoom, int Start, int End) {
        Map<String, List<String>> roomsList = new TreeMap<>(Map.of(
                "EPFL", new ArrayList<>(),
                "FLEP", new ArrayList<>()));

        Map<String, Datajson> result = new TreeMap<>();
        dataJson.forEach((k, v) -> {
            if (listOfRoom.contains(k)) {
                result.put(k, v);
            }
        });

        Set<Integer> hours = new TreeSet<>();
        for (int i = Start; i < End; i++) {
            hours.add(i);
        }

        Set<String> keys = Set.copyOf(result.keySet());
        for (String room : keys) {
            if (!Collections.disjoint(result.get(room).getHoraire(), hours)) {
                result.remove(room);
            }
        }
        for (String room : result.keySet()) {
            if ("EPFL".equals(result.get(room).getSource())) {
                roomsList.get("EPFL").add(room);
            }
        }
        Collections.sort(roomsList.get("EPFL"));

        for (String room : result.keySet()) {
            if ("FLEP".equals(result.get(room).getSource())) {
                roomsList.get("FLEP").add(room);
            }
        }
        Collections.sort(roomsList.get("FLEP"));
        return roomsList;
    }

    /**
     * This method is used to count the number of available rooms in each building within a given time range.
     * It filters out the rooms that are not available at the specified time and counts the remaining rooms
     * in each building.
     *
     * @param listOfRoom A set of room names to count for.
     * @param Start      The start time for the count.
     * @param End        The end time for the count.
     * @return A map with building names as keys and the count of available rooms in each building as values.
     */
    public static Map<String, Integer> buildingCount(Set<String> listOfRoom, int Start, int End) {
        Map<String, Integer> buildingCount = new TreeMap<>();
        Map<String, Datajson> result = new TreeMap<>();

        Set<Integer> hours = new TreeSet<>();
        for (int i = Start; i < End; i++) {
            hours.add(i);
        }

        dataJson.forEach((k, v) -> {
            if (listOfRoom.contains(k)) {
                result.put(k, v);
            }
        });
        result.forEach((k, v) -> {
            if (Collections.disjoint(v.getHoraire(), hours)) {
                validRoomData
                        .stream().filter(l -> l.getRooms().equals(k))
                        .findFirst().ifPresent(m -> {
                            if (buildingCount.containsKey(m.getBuildings())) {
                                buildingCount.put(m.getBuildings(), buildingCount.get(m.getBuildings()) + 1);
                            } else {
                                buildingCount.put(m.getBuildings(), 1);
                            }
                        });
            }
        });
        return buildingCount;
    }
}
