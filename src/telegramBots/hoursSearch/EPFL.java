package telegramBots.hoursSearch;

import com.fasterxml.jackson.core.json.JsonReadFeature;
import com.fasterxml.jackson.core.type.TypeReference;
import com.fasterxml.jackson.databind.JsonNode;
import com.fasterxml.jackson.databind.ObjectMapper;
import me.tongfei.progressbar.ProgressBar;
import me.tongfei.progressbar.ProgressBarBuilder;
import me.tongfei.progressbar.ProgressBarStyle;
import org.apache.commons.lang3.StringUtils;
import utils.databaseOperation.FileOperation;
import utils.databaseOperation.FolderOperation;
import utils.databaseOperation.UrlFetcher;

import java.io.File;
import java.io.IOException;
import java.text.SimpleDateFormat;
import java.util.Date;
import java.util.HashSet;
import java.util.List;
import java.util.Set;

import static utils.databaseOperation.JsonOperation.JsonFileWrite;


/**
 * The EPFL class provides multiple methods for fetching data from the EPFL website.
 */
public class EPFL {

    private static final String DATABASE_PATH = "database/";
    private static final String EPFL_PREFIX = "EPFL-";
    private static final String DATE_FORMAT = "yyyy-MM-dd";
    private static final String EVENTS_START = "v.events = ";
    private static final String NO_INFO_MESSAGE = "Pas d'information pour cette salle";
    private static final ObjectMapper objectMapper = new ObjectMapper();

    private static final String FOLDER_PATH = "database/DataFromDailySteps/";


    /**
     * Private constructor to prevent instantiation of this utility class.
     */
    private EPFL() {
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
        SimpleDateFormat dateFormat = new SimpleDateFormat(DATE_FORMAT);
        String currentDateString = dateFormat.format(currentDate);
        FolderOperation.deleteFoldersExcept(currentDateString);

        List<String> paths = objectMapper.readValue(
                new File("resources/allValidRooms.json"),
                new TypeReference<>() {
                });

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
                String filePath = DATABASE_PATH + EPFL_PREFIX + currentDateString + "/" + path;
                String data = UrlFetcher.EPFL(path);

                if (data.contains(NO_INFO_MESSAGE)) {
                    roomWithIssue.add(path);
                } else {
                    fromEPFL.add(path);
                }

                int startIndex = data.indexOf(EVENTS_START);
                int endIndex = data.indexOf(";", startIndex);

                if (startIndex != -1 && endIndex != -1) {

                    String json = data.substring(startIndex + 11, endIndex).trim();
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
                Thread.sleep(100);
            }
            pb.setExtraMessage(StringUtils.rightPad(" done", 14));
            pb.refresh();
        } catch (InterruptedException e) {
            throw new RuntimeException(e);
        }
        JsonFileWrite(roomWithIssue, "roomWithIssue", FOLDER_PATH);
        JsonFileWrite(fromEPFL, "fromEPFL", FOLDER_PATH);
    }
}