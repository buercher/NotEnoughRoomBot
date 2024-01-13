package telegramBots.hoursSearch;

import com.fasterxml.jackson.core.type.TypeReference;
import com.fasterxml.jackson.databind.ObjectMapper;
import me.tongfei.progressbar.ProgressBar;
import me.tongfei.progressbar.ProgressBarBuilder;
import me.tongfei.progressbar.ProgressBarStyle;
import org.apache.commons.lang.StringUtils;
import org.json.JSONObject;
import utils.databaseOperation.FileOperation;
import utils.databaseOperation.UrlFetcher;

import java.io.File;
import java.io.IOException;
import java.text.SimpleDateFormat;
import java.util.Date;
import java.util.List;
import java.util.Objects;

/**
 * The FLEP class provides multiple methods for fetching data from the FLEP website.
 */
public class FLEP {

    private static final String DATABASE_PATH = "database/";
    private static final String FLEP_PREFIX = "FLEP-";
    private static final String DATE_FORMAT = "yyyy-MM-dd";
    private static final String JSON_FILE_PATH = "database/DataFromDailySteps/roomWithIssue.json";

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
        SimpleDateFormat dateFormat = new SimpleDateFormat(DATE_FORMAT);
        String currentDateString = dateFormat.format(currentDate);

        ObjectMapper objectMapper = new ObjectMapper();
        List<String> paths = objectMapper.readValue(
                new File(JSON_FILE_PATH),
                new TypeReference<>() {
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
                ).continuousUpdate().setTaskName("FLEP").setMaxRenderedLength(100);
        try (ProgressBar pb = pbb.build()) {
            pb.maxHint(paths.size());

            for (String path : paths) {
                String filePath = DATABASE_PATH + FLEP_PREFIX + currentDateString + "/" + path;
                List<JSONObject> schedules = UrlFetcher.FLEP(path);
                if (Objects.nonNull(schedules)) {
                    for (JSONObject schedule : schedules) {
                        String scheduleStartDate = schedule.getString("start_datetime").substring(0, 10);
                        if (currentDateString.equals(scheduleStartDate)) {
                            String scheduleStartHour = schedule.getString("start_datetime").substring(11, 13);
                            String scheduleEndHour = schedule.getString("end_datetime").substring(11, 13);
                            FileOperation.appendToFile(filePath, scheduleStartHour + " " + scheduleEndHour);
                        }
                    }
                }

                // Sort the file and merge adjacent ranges
                FileOperation.FinalFileCreation(pb, path, filePath);
                Thread.sleep(50);
            }
            pb.setExtraMessage(StringUtils.rightPad(" done", 14));
            pb.refresh();
        } catch (InterruptedException e) {
            throw new RuntimeException(e);
        }
    }
}