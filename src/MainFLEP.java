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
import java.nio.file.Path;
import java.nio.file.Paths;

public class MainFLEP {
    public static List<String> fetchStringsFromFile(String filePath) throws IOException {
        Path path = Paths.get(filePath);
        return Files.readAllLines(path);
    }

    public static void main() {
        String source = "FLEP";

        Date currentDate = new Date();
        SimpleDateFormat dateFormat = new SimpleDateFormat("yyyy-MM-dd");
        String currentDateString = dateFormat.format(currentDate);

        FolderDeletion.deleteFoldersExcept("database", currentDateString, source);

        try {
            List<String> paths = fetchStringsFromFile("resources/list");

            for (String path : paths) {
                String filePath = "database/" + "FLEP-" + currentDateString + "/" + path;

                List<JSONObject> schedules = UrlFetcherFLEP.fetchSchedulesFromUrl(path);

                for (JSONObject schedule : schedules) {
                    String scheduleStartDate = schedule.getString("start").substring(0, 10);

                    if (currentDateString.equals(scheduleStartDate)) {
                        String scheduleStartHour = schedule.getString("start_datetime").substring(17, 19);
                        String scheduleEndHour = schedule.getString("end_datetime").substring(17, 19);
                        FileManipulation.appendToFile(filePath, scheduleStartHour + " " + scheduleEndHour);
                    }
                }
                File file = new File(filePath);
                if (file.exists()) {
                    List<String> lines = Files.readAllLines(file.toPath(), StandardCharsets.UTF_8);
                    Collections.sort(lines);
                    MergeRanges.mergeAdjacentRanges(lines);

                    Files.write(file.toPath(), lines, Charset.defaultCharset());
                }
            }
        } catch (IOException e) {
            e.fillInStackTrace();
        }
    }
}