package HoursSearch;

import org.json.JSONArray;
import org.json.JSONObject;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStreamReader;
import java.net.HttpURLConnection;
import java.net.URL;
import java.util.ArrayList;
import java.util.List;

public class UrlFetcherFLEP {

    private UrlFetcherFLEP(){}

    public static List<JSONObject> fetchDataFromUrl(String urlString) throws IOException {
        String First = "https://occupancy-backend-e150a8daef31.herokuapp.com/api/rooms/";
        URL url = new URL(First + urlString);
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

            JSONObject data = new JSONObject(content.toString());
            JSONArray schedulesArray = data.getJSONArray("schedules");

            List<JSONObject> schedulesList = new ArrayList<>();
            for (int i = 0; i < schedulesArray.length(); i++) {
                schedulesList.add(schedulesArray.getJSONObject(i));
            }

            return schedulesList;
        } finally {
            connection.disconnect();
        }
    }
}
