import com.fasterxml.jackson.core.type.TypeReference;
import com.fasterxml.jackson.databind.ObjectMapper;

import java.io.File;
import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Path;
import java.util.*;

public class GetRoomAvailability {

    public static void search(int Start, int End){
        System.out.println(Start+End);
    }
    public static void main(String[] args) throws IOException {

        File jsonFile = new File("database/data.json");

        String jsonString= Files.readString(jsonFile.toPath());

        Map<String, Integer> count= new LinkedHashMap<>();

        List<String> rooms=Files.readAllLines(Path.of("resources/list"));

        TypeReference<Map<String, Set<Integer>>> typeRef = new TypeReference<>() {};

        Map<String, Set<Integer>> result = new ObjectMapper().readValue(jsonString, typeRef);

        for(String key:result.keySet()){
            Set<Integer> heure= new HashSet<>(Set.of(17, 18, 19));
            heure.removeAll(result.get(key));
            count.put(key,heure.size());
        }

        for (int i=3;i>2;i--){
            System.out.println(i+"h: ");
            for(String key:rooms){
                if(count.get(key)==i &&
                        rooms.contains(key)){
                    System.out.printf(key+" ");
                }
            }
        }
    }
}
