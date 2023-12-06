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

/**
 * The UrlFetcher class provides multiple methods for fetching data from URLs.
 */
public class UrlFetcher {

    /**
     * The UrlFetcher constructor is private because this class is not meant to be instantiated.
     */
    private UrlFetcher() {
    }

    /**
     * Fetches the data from the EPFL website and returns it as a String.
     *
     * @param roomName The name to the room.
     * @return The data from the EPFL API as a String.
     * @throws IOException If the connection fails.
     */
    public static String EPFL(String roomName) throws IOException {
        String urlString = "https://ewa.epfl.ch/room/Default.aspx?room=";
        return connect(urlString, roomName);
    }

    /**
     * Fetches the data from the FLEP website and returns it as a List of JSONObjects.
     *
     * @param roomName The name to the room.
     * @return The data from the FLEP API as a List of JSONObjects.
     * @throws IOException If the connection fails.
     * @see JSONObject, JsonArray
     */
    public static List<JSONObject> FLEP(String roomName) throws IOException {
        String urlString = "https://occupancy-backend-e150a8daef31.herokuapp.com/api/rooms/";

        JSONObject data = new JSONObject(connect(urlString, roomName));
        JSONArray schedulesArray = data.getJSONArray("schedules");

        List<JSONObject> schedulesList = new ArrayList<>();
        for (int i = 0; i < schedulesArray.length(); i++) {
            schedulesList.add(schedulesArray.getJSONObject(i));
        }

        return schedulesList;
    }

    /**
     * Fetches the data from the FLEP website and returns it as a JSONObject.
     *
     * @param roomName The name to the room.
     * @return The data from the FLEP API as a JSONObject.
     * @throws IOException If the connection fails.
     */
    public static JSONObject FLEP2(String roomName) throws IOException {
        String urlString = "https://occupancy-backend-e150a8daef31.herokuapp.com/api/rooms/";

        return new JSONObject(connect(urlString, roomName));
    }

    /**
     * Fetches the data from the FLEP website and returns it as a String.
     *
     * @param roomName The name to the room.
     * @return The data from the FLEP API as a String.
     * @throws IOException If the connection fails.
     */
    public static String FLEP3(String roomName) throws IOException {
        String urlString = "https://occupancy-backend-e150a8daef31.herokuapp.com/api/rooms/";

        return connect(urlString, roomName);
    }

    /**
     * Fetches the data from the EPFL website and returns it as a String.
     *
     * @param roomName The name to the room.
     * @return The data from the EPFL API as a String.
     * @throws IOException If the connection fails.
     */
    public static String EPFL2(String roomName) throws IOException {
        String urlString = "https://ewa.epfl.ch/room/Default.aspx?room=";
        return connect(urlString, roomName);
    }

    /**
     * Connects to the URL and returns the content of the website as a String.
     *
     * @param urlString The URL to connect to.
     * @param roomName  The name to the room.
     * @return The content of the website as a String.
     * @throws IOException If the connection fails.
     */
    private static String connect(String urlString, String roomName) throws IOException {
        URL url = new URL(urlString + roomName);
        HttpURLConnection connection = (HttpURLConnection) url.openConnection();

        try {
            connection.setRequestMethod("GET");

            // Read the content of the website
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
