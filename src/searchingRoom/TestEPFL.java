package searchingRoom;

import databaseOperation.UrlFetcher;

import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.util.HashSet;
import java.util.List;
import java.util.Set;

import static databaseOperation.JsonOperation.JsonFileWrite;

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
            String data = UrlFetcher.EPFL(path);
            if(data.contains("Pas d'information pour cette salle")){
                data = UrlFetcher.EPFL(path);
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

}
