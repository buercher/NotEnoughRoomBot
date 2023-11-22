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
                reqdMap.put(path, new HashSet<Integer>());
                for (String line : lines){
                    for(int i=Integer.parseInt(line.substring(0,2));i<Integer.parseInt(line.substring(3,5));i++){
                        reqdMap.get(path).add(i);
                    }
                }
            }
        }
    }
}
