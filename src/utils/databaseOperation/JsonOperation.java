package utils.databaseOperation;

import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.core.type.TypeReference;
import com.fasterxml.jackson.databind.ObjectMapper;
import utils.jsonObjects.Datajson;
import me.tongfei.progressbar.ProgressBar;
import me.tongfei.progressbar.ProgressBarBuilder;
import me.tongfei.progressbar.ProgressBarStyle;
import org.apache.commons.lang.StringUtils;

import java.io.File;
import java.io.IOException;
import java.nio.charset.Charset;
import java.nio.charset.StandardCharsets;
import java.nio.file.Files;
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
     *     "AAC006": "EPFL" , [4, 5, 9, 10]
     *     "BC05": "FLEP", [3, 4, 5, 8, 9, 10],
     *     "BC06": "FLEP", [1, 2, 8, 9, 10],
     *     "BC07": "EPFL", [3, 6, 7, 8, 9, 10],
     * }
     * </pre>
     *
     * @throws IOException If an I/O error occurs
     * @see Datajson
     */
    public static void makeFile() throws IOException {
        Map<String, Datajson> roomSchedules = new LinkedHashMap<>();

        Date currentDate = new Date();
        SimpleDateFormat dateFormat = new SimpleDateFormat("yyyy-MM-dd");
        String currentDateString = dateFormat.format(currentDate);

        ObjectMapper objectMapper = new ObjectMapper();
        List<String> paths = objectMapper.readValue(
                new File("resources/allValidRooms.json"),
                new TypeReference<>() {
                });

        List<String> pathsEPFL = objectMapper.readValue(
                new File("database/DataFromDailySteps/fromEPFL.json"), new TypeReference<>() {
                });

        List<String> pathsRoomWithIssue = objectMapper.readValue(
                new File("database/DataFromDailySteps/roomWithIssue.json"), new TypeReference<>() {
                });

        ProgressBarBuilder pbb = ProgressBar.builder()
                .setStyle(ProgressBarStyle.builder()
                        .refreshPrompt("\r")
                        .leftBracket("\u001b[1:36m")
                        .delimitingSequence("\u001b[1:34m")
                        .rightBracket("\u001b[1:34m")
                        .block('━')
                        .space('━')
                        .fractionSymbols(" ╸")
                        .rightSideFractionSymbol('╺')
                        .build()
                ).continuousUpdate().setTaskName("JSON").setMaxRenderedLength(100);
        try (ProgressBar pb = pbb.build()) {
            pb.maxHint(paths.size());
            for (String epflPath : pathsEPFL) {
                String smallPath = epflPath.replaceAll("-", "")
                        .replaceAll(" ", "")
                        .replaceAll("\\.", "")
                        .replaceAll("_", "");
                if (!roomSchedules.containsKey(epflPath)) {
                    roomSchedules.put(
                            smallPath,
                            new Datajson("EPFL", new TreeSet<>()));
                    AddToMap(roomSchedules,
                            smallPath,
                            "database/" + "EPFL-" + currentDateString + "/" + epflPath);
                }
                pb.step();
                pb.setExtraMessage(StringUtils.rightPad(" " + epflPath, 14));
                pb.refresh();
            }
            paths.removeAll(pathsEPFL);
            for (String flepPath : pathsRoomWithIssue) {
                String smallPath = flepPath.replaceAll("-", "")
                        .replaceAll(" ", "")
                        .replaceAll("\\.", "")
                        .replaceAll("_", "");
                if (!roomSchedules.containsKey(flepPath)) {
                    roomSchedules.put(
                            smallPath,
                            new Datajson("FLEP", new TreeSet<>()));
                    AddToMap(roomSchedules,
                            smallPath,
                            "database/" + "FLEP-" + currentDateString + "/" + flepPath);
                }
                pb.step();
                pb.setExtraMessage(StringUtils.rightPad(" " + flepPath, 14));
                pb.refresh();
            }
            paths.removeAll(pathsRoomWithIssue);
            if (!paths.isEmpty()) {
                System.out.println(paths);
            }
            pb.setExtraMessage(StringUtils.rightPad(" done", 14));
            pb.refresh();
        }

        // BC07 and BC08 are merged into one room in the EPFL schedule
        if (roomSchedules.containsKey("BC0708")) {
            roomSchedules.replace("BC07", roomSchedules.get("BC0708"));
            roomSchedules.replace("BC08", roomSchedules.get("BC0708"));
            roomSchedules.remove("BC0708");
        }

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
    private static void AddToMap(Map<String, Datajson> map,
                                 String roomName,
                                 String filePath) throws IOException {

        File file = new File(filePath);
        if (file.exists()) {
            List<String> lines = Files.readAllLines(file.toPath(), StandardCharsets.UTF_8);
            for (String line : lines) {
                for (int i = Integer.parseInt(line.substring(0, 2)); i < Integer.parseInt(line.substring(3, 5)); i++) {
                    map.get(roomName).getHoraire().add(i);
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
    public static void JsonFileWrite(Set<String> JsonSet, String name, String folderPath) throws IOException {
        if (!JsonSet.isEmpty()) {
            File jsonFile = new File(folderPath + name + ".json");
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
