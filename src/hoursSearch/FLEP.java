package hoursSearch;

import com.fasterxml.jackson.core.type.TypeReference;
import com.fasterxml.jackson.databind.ObjectMapper;
import databaseOperation.FileOperation;
import databaseOperation.UrlFetcher;
import org.json.JSONObject;

import java.io.File;
import java.io.IOException;
import java.nio.charset.Charset;
import java.nio.charset.StandardCharsets;
import java.util.Collections;
import java.util.List;
import java.text.SimpleDateFormat;
import java.util.Date;
import java.nio.file.Files;

/**
 * The FLEP class provides multiple methods for fetching data from the FLEP website.
 */
public class FLEP {

    /**
     * Private constructor to prevent instantiation of this utility class.
     */
    private FLEP() {
    }

    /**
     * Scrapes the data from the FLEP website and stores it in the database.
     *
     * @throws IOException If an I/O error occurs
     * @see UrlFetcher#FLEP(String)
     * @see JSONObject
     * @see ObjectMapper
     */
    public static void scrap() throws IOException {
        Date currentDate = new Date();
        SimpleDateFormat dateFormat = new SimpleDateFormat("yyyy-MM-dd");
        String currentDateString = dateFormat.format(currentDate);

        ObjectMapper objectMapper = new ObjectMapper();
        List<String> paths = objectMapper.readValue(
                new File("database/roomWithIssue.json"), new TypeReference<>() {
                });


        for (String path : paths) {
            String filePath = "database/" + "FLEP-" + currentDateString + "/" + path;
            List<JSONObject> schedules = UrlFetcher.FLEP(path);

            for (JSONObject schedule : schedules) {
                String scheduleStartDate = schedule.getString("start").substring(0, 10);

                if (currentDateString.equals(scheduleStartDate)) {
                    String scheduleStartHour = schedule.getString("start_datetime").substring(17, 19);
                    String scheduleEndHour = schedule.getString("end_datetime").substring(17, 19);
                    FileOperation.appendToFile(filePath, scheduleStartHour + " " + scheduleEndHour);
                }
            }

            // Sort the file and merge adjacent ranges
            File file = new File(filePath);
            if (file.exists()) {
                List<String> lines = Files.readAllLines(file.toPath(), StandardCharsets.UTF_8);
                Collections.sort(lines);
                MergeRanges.mergeAdjacentRanges(lines);

                Files.write(file.toPath(), lines, Charset.defaultCharset());
            }
        }
    }
}