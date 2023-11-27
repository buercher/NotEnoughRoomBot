package searchingRoom;

import com.fasterxml.jackson.core.type.TypeReference;
import com.fasterxml.jackson.databind.ObjectMapper;

import java.io.*;
import java.net.HttpURLConnection;
import java.net.URL;
import java.util.HashSet;
import java.util.List;
import java.util.Set;

import static databaseOperation.JsonOperation.JsonFileWrite;

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
}
