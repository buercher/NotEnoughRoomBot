package SearchingRoom;

import HoursSearch.Create;
import HoursSearch.UrlFetcherEPFL;
import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.databind.ObjectMapper;

import java.io.File;
import java.io.IOException;
import java.nio.charset.Charset;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.util.Collections;
import java.util.HashSet;
import java.util.List;
import java.util.Set;

public class TestEPFL {
    private TestEPFL(){}
    private static List<String> fetchStringsFromFile(String filePath) throws IOException {
        Path path = Paths.get(filePath);
        return Files.readAllLines(path);
    }
    public static void test(String buildingName) throws IOException {
        Set<String> roomWithIssue=new HashSet<>();
        Set<String> fromEPFL=new HashSet<>();
        List<String> paths = fetchStringsFromFile("resources/RoomList/"+buildingName);
        for (String path : paths) {
            String data = UrlFetcherEPFL.fetchDataFromUrl(path);
            if(data.contains("Pas d'information pour cette salle")){
                data = UrlFetcherEPFL.fetchDataFromUrl(path);
                if(data.contains("Pas d'information pour cette salle")){
                    roomWithIssue.add(path);
                    System.out.println("roomWithIssue : "+path);
                }
                else{
                    fromEPFL.add(path);
                    System.out.println("EPFL : "+path);
                }
            }
            else{
                fromEPFL.add(path);
                System.out.println("EPFL : "+path);
            }
        }
        JsonFileWrite(roomWithIssue,"roomWithIssue/"+buildingName);
        JsonFileWrite(fromEPFL,"fromEPFL/"+buildingName);
    }
    private static void JsonFileWrite(Set<String> JsonSet, String name) throws IOException{
        if(!JsonSet.isEmpty()){
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
