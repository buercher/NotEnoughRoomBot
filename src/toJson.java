import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.core.type.TypeReference;
import com.fasterxml.jackson.databind.ObjectMapper;

import java.io.File;
import java.io.IOException;
import java.nio.charset.Charset;
import java.nio.charset.StandardCharsets;
import java.nio.file.Files;
import java.nio.file.Paths;
import java.text.SimpleDateFormat;
import java.util.*;

public class toJson {
    public static void makeFile() throws IOException {
        Map<String, Set<Integer>> reqdMap = new LinkedHashMap<>();

        Date currentDate = new Date();
        SimpleDateFormat dateFormat = new SimpleDateFormat("yyyy-MM-dd");
        String currentDateString = dateFormat.format(currentDate);

        List<String> paths = Files.readAllLines(Paths.get("resources/list"));
        ObjectMapper objectMapper = new ObjectMapper();
        List<String> pathsEPFL = objectMapper.readValue(
                new File("database/fromEPFL.json"), new TypeReference<>() {});

        List<String> pathsRoomWithIssue = objectMapper.readValue(
                new File("database/roomWithIssue.json"), new TypeReference<>() {});

        for (String path : paths) {
            reqdMap.put(path, new TreeSet<>());
            if (pathsEPFL.contains(path)) {
                AddToMap(reqdMap,
                        path,
                        "database/" + "EPFL-" + currentDateString + "/" + path);
            }
            else if (pathsRoomWithIssue.contains(path)) {
                AddToMap(reqdMap,
                        path,
                        "database/" + "FLEP-" + currentDateString + "/" + path);
            }
            else{
                throw new IllegalStateException("The data wasn't attributed to any of the maps");
            }
        }
        if(!reqdMap.get("BC07-08").isEmpty()){
            reqdMap.get("BC07").clear();
            reqdMap.get("BC08").clear();
            reqdMap.get("BC07").addAll(reqdMap.get("BC07-08"));
            reqdMap.get("BC08").addAll(reqdMap.get("BC07-08"));
        }
        reqdMap.remove("BC07-08");
        try {
            String json = objectMapper.writeValueAsString(reqdMap);
            File file = new File("database/data.json");

            Create.file(file);
            Files.write(file.toPath(), Collections.singleton(json), Charset.defaultCharset());

        } catch (JsonProcessingException e) {
            e.fillInStackTrace();
        }
    }

    private static void AddToMap(Map<String, Set<Integer>> reqdMap,
                                 String path,
                                 String filePath) throws IOException {

        File file = new File(filePath);
        if(file.exists()){
            List<String> lines = Files.readAllLines(file.toPath(), StandardCharsets.UTF_8);
            for (String line : lines){
                for(int i=Integer.parseInt(line.substring(0,2));i<Integer.parseInt(line.substring(3,5));i++){
                    reqdMap.get(path).add(i);
                }
            }
        }
    }
}
