package databaseOperation;

import org.json.JSONArray;
import org.json.JSONObject;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStreamReader;
import java.net.HttpURLConnection;
import java.net.URL;
import java.util.ArrayList;
import java.util.List;

public class UrlFetcher {

    private UrlFetcher(){}

    public static String EPFL (String path) throws IOException {
        String urlString = "https://ewa.epfl.ch/room/Default.aspx?room=";
        return connect(urlString,path);
    }

    public static List<JSONObject> FLEP(String path) throws IOException {
        String urlString = "https://occupancy-backend-e150a8daef31.herokuapp.com/api/rooms/";

        JSONObject data = new JSONObject(connect(urlString,path));
        JSONArray schedulesArray = data.getJSONArray("schedules");

        List<JSONObject> schedulesList = new ArrayList<>();
        for (int i = 0; i < schedulesArray.length(); i++) {
            schedulesList.add(schedulesArray.getJSONObject(i));
        }

        return schedulesList;
    }
    private static String connect(String urlString, String path) throws IOException {
        URL url = new URL(urlString + path);
        HttpURLConnection connection = (HttpURLConnection) url.openConnection();

        try {
            connection.setRequestMethod("GET");

            StringBuilder content = new StringBuilder();
            try (BufferedReader reader = new BufferedReader(new InputStreamReader(connection.getInputStream()))) {
                String line;
                while ((line = reader.readLine()) != null) {
                    content.append(line);
                }
            }

            return content.toString();
        } finally {
            connection.disconnect();
        }
    }
}
