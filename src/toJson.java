import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.databind.ObjectMapper;
import org.json.JSONObject;

import java.io.File;
import java.io.IOException;
import java.nio.charset.Charset;
import java.nio.charset.StandardCharsets;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.text.SimpleDateFormat;
import java.util.*;

public class toJson {
    public static void toJson() throws IOException {
        Map<String, Set<Integer>> reqdMap = new LinkedHashMap<String, Set<Integer>>();
        Date currentDate = new Date();
        SimpleDateFormat dateFormat = new SimpleDateFormat("yyyy-MM-dd");
        String currentDateString = dateFormat.format(currentDate);

        List<String> paths = Files.readAllLines(Paths.get("resources/list"));
        for (String path : paths) {
            String filePath = "database/" + "EPFL-" + currentDateString + "/" + path;
            File file = new File(filePath);
            if (file.exists()) {
                List<String> lines = Files.readAllLines(file.toPath(), StandardCharsets.UTF_8);
                reqdMap.put(path, new TreeSet<Integer>());
                for (String line : lines){
                    for(int i=Integer.parseInt(line.substring(0,2));i<Integer.parseInt(line.substring(3,5));i++){
                        reqdMap.get(path).add(i);
                    }
                }
            }
            filePath = "database/" + "FLEP-" + currentDateString + "/" + path;
            file = new File(filePath);
            if (file.exists()) {
                List<String> lines = Files.readAllLines(file.toPath(), StandardCharsets.UTF_8);
                if(!reqdMap.containsKey(path)){
                    reqdMap.put(path, new TreeSet<Integer>());
                }
                for (String line : lines){
                    for(int i=Integer.parseInt(line.substring(0,2));i<Integer.parseInt(line.substring(3,5));i++){
                        reqdMap.get(path).add(i);
                    }
                }
            }
        }/**
        for (Map.Entry entry : reqdMap.entrySet())
        {
            System.out.println("key: " + entry.getKey() + "; value: " + entry.getValue());
        }*/
        ObjectMapper objectMapper = new ObjectMapper();
        try {
            String json = objectMapper.writeValueAsString(reqdMap);
            System.out.println(json);
            File file = new File("database/data.json");
            if (!file.exists()) {
                // If the file does not exist, create a new file
                if (!file.createNewFile()) {
                    System.out.println("Failed to create file: " + file.getPath());
                    return;
                }
            } else if (!file.delete()) {
                System.out.println("Failed to create file '" + file.getPath() + "'.");
            }
            Files.write(file.toPath(), Collections.singleton(json), Charset.defaultCharset());

        } catch (JsonProcessingException e) {
            e.printStackTrace();
        }
    }
}
