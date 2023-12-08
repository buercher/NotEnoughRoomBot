package hoursSearch;

import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.core.json.JsonReadFeature;
import com.fasterxml.jackson.databind.JsonNode;
import com.fasterxml.jackson.databind.ObjectMapper;
import databaseOperation.FileOperation;
import databaseOperation.FolderOperation;
import databaseOperation.UrlFetcher;
import me.tongfei.progressbar.ProgressBar;
import me.tongfei.progressbar.ProgressBarBuilder;
import me.tongfei.progressbar.ProgressBarStyle;
import org.apache.commons.lang.StringUtils;

import java.io.File;
import java.io.IOException;
import java.nio.charset.Charset;
import java.nio.charset.StandardCharsets;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.text.SimpleDateFormat;
import java.util.*;

/**
 * The EPFL class provides multiple methods for fetching data from the EPFL website.
 */
public class EPFL {

    /**
     * Private constructor to prevent instantiation of this utility class.
     */
    private EPFL() {
    }

    /**
     * Reads the file containing the list of rooms and returns it as a list of strings.
     *
     * @return The list of rooms as a list of strings
     * @throws IOException If an I/O error occurs while reading the file
     */
    private static List<String> fetchStringListFromFile() throws IOException {
        Path path = Paths.get("resources/list");
        return Files.readAllLines(path, StandardCharsets.UTF_8);
    }

    /**
     * Scrapes the data from the EPFL website and stores it in the database.
     *
     * @throws IOException If an I/O error occurs
     * @see UrlFetcher#EPFL(String)
     * @see JsonNode
     * @see ObjectMapper
     */
    public static void scrap() throws IOException {

        Date currentDate = new Date();
        SimpleDateFormat dateFormat = new SimpleDateFormat("yyyy-MM-dd");
        String currentDateString = dateFormat.format(currentDate);
        FolderOperation.deleteFoldersExcept(currentDateString);

        List<String> paths = fetchStringListFromFile();
        Set<String> roomWithIssue = new HashSet<>();
        Set<String> fromEPFL = new HashSet<>();

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
                ).continuousUpdate().setTaskName("EPFL").setMaxRenderedLength(100);
        try (ProgressBar pb = pbb.build()) {
            pb.maxHint(paths.size());

            for (String path : paths) {
                String filePath = "database/" + "EPFL-" + currentDateString + "/" + path;
                String data = UrlFetcher.EPFL(path);

                if (data.contains("Pas d'information pour cette salle")) {
                    roomWithIssue.add(path);
                } else {
                    fromEPFL.add(path);
                }

                int startIndex = data.indexOf("v.events = ");
                int endIndex = data.indexOf(";", startIndex);

                if (startIndex != -1 && endIndex != -1) {

                    String json = data.substring(startIndex + 11, endIndex).trim();
                    ObjectMapper objectMapper = new ObjectMapper();
                    objectMapper.configure(
                            JsonReadFeature.ALLOW_BACKSLASH_ESCAPING_ANY_CHARACTER.mappedFeature(), true);
                    JsonNode eventsArray = objectMapper.readTree(json);
                    for (JsonNode event : eventsArray) {
                        String scheduleStartDate = event.get("Start").asText().substring(0, 10);

                        if (currentDateString.equals(scheduleStartDate)) {
                            String scheduleStartHour = event.get("Start").asText().substring(11, 13);
                            String scheduleEndHour = event.get("End").asText().substring(11, 13);
                            FileOperation.appendToFile(filePath, scheduleStartHour + " " + scheduleEndHour);
                        }
                    }
                }

                // Sort the file and merge adjacent ranges
                FileOperation.FinalFileCreation(pb, path, filePath);
            }
            pb.setExtraMessage(StringUtils.rightPad(" done", 14));
            pb.refresh();
        }
        JsonFileWrite(roomWithIssue, "roomWithIssue");
        JsonFileWrite(fromEPFL, "fromEPFL");
    }

    /**
     * Writes a json file with the given name containing the given set of strings.
     *
     * @param JsonSet The set of strings to write in the json file
     * @param name    The name of the json file
     * @throws IOException If an I/O error occurs
     */
    private static void JsonFileWrite(Set<String> JsonSet, String name) throws IOException {
        if (!JsonSet.isEmpty()) {
            File jsonFile = new File("database/" + name + ".json");
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