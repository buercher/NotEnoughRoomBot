package plan;

import com.fasterxml.jackson.databind.ObjectMapper;
import plan.jsonArchitecture.JsonRoom;

import java.io.File;
import java.io.IOException;

public class JsonRoom123 {
    public static void main(String[] args) throws IOException {
        int count=0;
        for(int i=-4;i<9;i++){
            count+=test(i);
        }
        System.out.println(count);
    }
    public static int test(int floor) throws IOException {
                // Load JSON file
                File jsonFile = new File("resources/PlanJson/plan floor "+floor+".json");

                // Create ObjectMapper
                ObjectMapper objectMapper = new ObjectMapper();

                JsonRoom jsonRoom = objectMapper.readValue(jsonFile, JsonRoom.class);

                // Now you can work with the parsed Java objects
                System.out.println(floor+" "+jsonRoom.getWfsFeatureCollection().getGmlFeatureMember().size());
                return jsonRoom.getWfsFeatureCollection().getGmlFeatureMember().size();

    }
}
