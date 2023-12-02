package plan;

import org.jetbrains.annotations.NotNull;
import org.json.XML;

import java.io.*;
import java.net.HttpURLConnection;
import java.net.URL;
import java.nio.charset.Charset;
import java.nio.file.Files;
import java.nio.file.Path;
import java.util.Collections;

public class PlanDataFetch {

    public record Coordinate(double x, double y) {
    }

    public record Area(Coordinate upperCorner, Coordinate lowerCorner) {
    }

    private static final String apiUrl = "https://plan.epfl.ch/mapserv_proxy?ogcserver=source%20for%20image%2Fpng&floor=";

    public static void main(String[] args) throws IOException {
        File roomChecking = new File("resources/PlanJson");
        if (!roomChecking.exists()) {
            if (!roomChecking.mkdir()) {
                throw new IOException("Failed to create folder '" + roomChecking.getPath() + "'");
            }
        }
        Area area = new Area(new Coordinate(2951582.0, 1016367.0), new Coordinate(2420000.0, 1350000.0));
        searchAllFloor(area);
    }

    private static void searchAllFloor(Area area) {
        for (int i = -4; i < 9; i++) {
            search(area, i);
        }
    }

    private static void search(Area area, int floor) {

        String requestBody =
                "<GetFeature xmlns=\"https://www.opengis.net/wfs\" " +
                        "service=\"WFS\" " +
                        "version=\"1.1.0\" " +
                        "outputFormat=\"GML3\" " +
                        "maxFeatures=\"100000000\" " +
                        "xsi:schemaLocation=\"https://www.opengis.net/wfs " +
                        "https://schemas.opengis.net/wfs/1.1.0/wfs.xsd\" " +
                        "xmlns:xsi=\"http://www.w3.org/2001/XMLSchema-instance\">" +
                        "<Query typeName=\"feature:batiments_wmsquery\" " +
                        "srsName=\"EPSG:2056\" " +
                        "xmlns:feature=\"https://mapserver.gis.umn.edu/mapserver\">" +
                        "<Filter xmlns=\"https://www.opengis.net/ogc\">" +
                        "<BBOX><PropertyName>the_geom</PropertyName>" +
                        "<Envelope xmlns=\"https://www.opengis.net/gml\" " +
                        "srsName=\"EPSG:2056\">" +
                        "<lowerCorner>" + area.lowerCorner.x() + " " + area.lowerCorner.y() + "</lowerCorner>" +
                        "<upperCorner>" + area.upperCorner.x() + " " + area.upperCorner.y() + "</upperCorner>" +
                        "</Envelope></BBOX></Filter></Query>" +
                        "</GetFeature>";
        try {
            HttpURLConnection connection = getHttpURLConnection(floor);
            DataOutputStream outputStream = new DataOutputStream(connection.getOutputStream());
            outputStream.writeBytes(requestBody);
            outputStream.flush();
            outputStream.close();

            int responseCode = connection.getResponseCode();
            if (responseCode == HttpURLConnection.HTTP_OK) {
                BufferedReader reader = new BufferedReader(new InputStreamReader(connection.getInputStream()));
                String line;
                StringBuilder response = new StringBuilder();

                while ((line = reader.readLine()) != null) {
                    response.append(line);
                }
                reader.close();

                Path jsonPath = Path.of("resources/PlanJson/plan floor " + floor + ".json");
                Files.deleteIfExists(jsonPath);
                String jsonString = XML.toJSONObject(response.toString()).toString();
                Files.write(jsonPath, Collections.singleton(jsonString), Charset.defaultCharset());
                System.out.println(jsonPath.getFileName());
            } else {
                System.out.println("HTTP POST request failed with response code: " + responseCode);
            }
            connection.disconnect();
        } catch (IOException e) {
            e.fillInStackTrace();
        }
    }

    @NotNull
    private static HttpURLConnection getHttpURLConnection(int floor) throws IOException {
        URL url = new URL(apiUrl + floor);
        HttpURLConnection connection = (HttpURLConnection) url.openConnection();
        connection.setRequestMethod("POST");

        // Set headers
        connection.setRequestProperty("User-Agent", "Mozilla/5.0 (Windows NT 10.0; Win64; x64; rv:120.0) Gecko/20100101 Firefox/120.0");
        connection.setRequestProperty("Accept", "application/json, text/plain, */*");
        connection.setRequestProperty("Accept-Language", "fr,fr-FR;q=0.8,en-US;q=0.5,en;q=0.3");
        connection.setRequestProperty("Sec-Fetch-Dest", "empty");
        connection.setRequestProperty("Sec-Fetch-Mode", "no-cors");
        connection.setRequestProperty("Sec-Fetch-Site", "same-origin");
        connection.setRequestProperty("Content-Type", "text/xml; charset=UTF-8");
        connection.setRequestProperty("Pragma", "no-cache");
        connection.setRequestProperty("Cache-Control", "no-cache");

        // Enable output and write the request body
        connection.setDoOutput(true);
        return connection;
    }
}
