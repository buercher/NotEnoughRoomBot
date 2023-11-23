package HoursSearch;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStreamReader;
import java.net.HttpURLConnection;
import java.net.URL;

public class UrlFetcherEPFL {

    private UrlFetcherEPFL(){}

    public static String fetchDataFromUrl(String path) throws IOException {
        String urlstring = "https://ewa.epfl.ch/room/Default.aspx?room=";
        URL url = new URL(urlstring + path);
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
