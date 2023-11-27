package SearchingRoom;

import HoursSearch.Create;
import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.core.type.TypeReference;
import com.fasterxml.jackson.databind.ObjectMapper;

import java.io.*;
import java.net.HttpURLConnection;
import java.net.URL;
import java.nio.charset.Charset;
import java.nio.file.Files;
import java.util.Collections;
import java.util.HashSet;
import java.util.List;
import java.util.Set;

public class TestFLEP {

    private TestFLEP(){}

    public static void test(String buildingName) throws IOException {
        String First = "https://occupancy-backend-e150a8daef31.herokuapp.com/api/rooms/";
        Set<String> roomNoSearchable = new HashSet<>();
        Set<String> fromFLEP = new HashSet<>();
        ObjectMapper objectMapper = new ObjectMapper();
        List<String> paths = objectMapper.readValue(
                new File("database/roomChecking/roomWithIssue/"+buildingName+".json"), new TypeReference<>() {
                });
        for (String path : paths) {
            URL url = new URL(First + path);
            HttpURLConnection connection = (HttpURLConnection) url.openConnection();
            connection.setRequestMethod("GET");
            try (BufferedReader ignored = new BufferedReader(new InputStreamReader(connection.getInputStream()))) {
                fromFLEP.add(path);
                System.out.println("FLEP : "+path);
            }
            catch (java.io.FileNotFoundException e){
                roomNoSearchable.add(path);
                System.out.println("roomNoSearchable : "+path);
            }
        }
        JsonFileWrite(roomNoSearchable, "roomNotSearchable/"+buildingName);
        JsonFileWrite(fromFLEP, "fromFLEP/"+buildingName);
    }

    private static void JsonFileWrite(Set<String> JsonSet, String name) throws IOException {
        if (!JsonSet.isEmpty()) {
            File jsonFile =new File("database/roomChecking/"+name+".json");
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
