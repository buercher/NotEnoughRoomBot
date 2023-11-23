import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.core.json.JsonReadFeature;
import com.fasterxml.jackson.databind.JsonNode;
import com.fasterxml.jackson.databind.ObjectMapper;

import java.io.File;
import java.io.IOException;
import java.nio.charset.Charset;
import java.nio.charset.StandardCharsets;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.text.SimpleDateFormat;
import java.util.*;

public class EPFL {
    public static List<String> fetchStringsFromFile(String filePath) throws IOException {
        Path path = Paths.get(filePath);
        return Files.readAllLines(path);
    }

    public static void scrap() throws IOException {
        String source = "EPFL";

        Date currentDate = new Date();
        SimpleDateFormat dateFormat = new SimpleDateFormat("yyyy-MM-dd");
        String currentDateString = dateFormat.format(currentDate);
        FolderDeletion.deleteFoldersExcept("database", currentDateString, source);

        List<String> paths = fetchStringsFromFile("resources/list");
        Set<String> roomWithIssue=new HashSet<>();
        Set<String> fromEPFL=new HashSet<>();

        for (String path : paths) {
            String filePath = "database/" + "EPFL-" + currentDateString + "/" + path;
            String data = UrlFetcherEPFL.fetchDataFromUrl(path);

            if(data.contains("Pas d'information pour cette salle")){
                roomWithIssue.add(path);}
            else{
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
                        FileManipulation.appendToFile(filePath, scheduleStartHour + " " + scheduleEndHour);
                    }
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
        JsonFileWrite(roomWithIssue,"roomWithIssue");
        JsonFileWrite(fromEPFL,"fromEPFL");
    }

    private static void JsonFileWrite(Set<String> JsonSet, String name) throws IOException{
        if(!JsonSet.isEmpty()){
            File jsonFile =new File("database/"+name+".json");
            try {
                ObjectMapper objectMapper = new ObjectMapper();
                String jsonString = objectMapper.writeValueAsString(JsonSet);

                Create.file(jsonFile);
                Files.write(jsonFile.toPath(), Collections.singleton(jsonString), Charset.defaultCharset());

            } catch (JsonProcessingException e) {
                e.fillInStackTrace();
            }
        }
    }
}