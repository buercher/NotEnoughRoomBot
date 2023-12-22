package projectSetup.plan;

import me.tongfei.progressbar.ProgressBar;
import me.tongfei.progressbar.ProgressBarBuilder;
import me.tongfei.progressbar.ProgressBarStyle;
import org.jetbrains.annotations.NotNull;
import org.json.XML;

import java.io.BufferedReader;
import java.io.DataOutputStream;
import java.io.IOException;
import java.io.InputStreamReader;
import java.net.HttpURLConnection;
import java.net.URL;
import java.nio.charset.Charset;
import java.nio.file.Files;
import java.nio.file.Path;
import java.util.Collections;

/**
 * This class is used to fetch the data from the EPFL plan API.
 * It is used to generate the JSON files in the database/SetupData/PlanJson folder.
 */
public class PlanDataFetch {

    private static final String API_URL =
            "https://plan.epfl.ch/mapserv_proxy?ogcserver=source%20for%20image%2Fpng&floor=";

    private static final int MAX_FLOOR = 8;
    private static final int MIN_FLOOR = -4;

    private static final int FLOOR_COUNT = MAX_FLOOR - MIN_FLOOR + 1;

    /**
     * This class is not meant to be instantiated.
     */
    private PlanDataFetch() {
    }

    /**
     * Searches all the floors in the given area.
     */
    public static void searchAllFloor() {
        ProgressBarBuilder pbb = ProgressBar.builder()
                .setStyle(ProgressBarStyle.builder()
                        .refreshPrompt("\r")
                        .leftBracket("\u001b[1:36m")
                        .delimitingSequence("\u001b[1:34m")
                        .rightBracket("\u001b[1:34m")
                        .block('━')
                        .space('━')
                        .fractionSymbols(" ╸")
                        .rightSideFractionSymbol('╺')
                        .build()
                ).continuousUpdate().setTaskName("Plan").setMaxRenderedLength(86);

        try (ProgressBar pb = pbb.build()) {
            pb.maxHint(FLOOR_COUNT);
            Area area = new Area(
                    new Coordinate(2951582.0, 1016367.0), new Coordinate(2420000.0, 1350000.0));
            for (int i = MIN_FLOOR; i <= MAX_FLOOR; i++) {
                search(area, i);
                pb.step();
                pb.refresh();
            }
        }
    }

    /**
     * Searches the given floor in the given area.
     *
     * @param area  The area to search in
     * @param floor The floor to search
     */
    private static void search(Area area, int floor) {

        String requestBody =
                "<GetFeature xmlns=\"https://www.opengis.net/wfs\" " +
                        "service=\"WFS\" " +
                        "version=\"1.1.0\" " +
                        "outputFormat=\"GML3\" " +
                        "maxFeatures=\"10000000\" " +
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
            // Create connection
            HttpURLConnection connection = getHttpURLConnection(floor);
            try (DataOutputStream outputStream = new DataOutputStream(connection.getOutputStream())) {
                outputStream.writeBytes(requestBody);
                outputStream.flush();
            }

            int responseCode = connection.getResponseCode();
            if (responseCode == HttpURLConnection.HTTP_OK) {
                StringBuilder response = new StringBuilder();

                try (BufferedReader reader = new BufferedReader(new InputStreamReader(connection.getInputStream()))) {
                    String line;
                    while ((line = reader.readLine()) != null) {
                        response.append(line);
                    }
                }

                Path jsonPath = Path.of("database/SetupData/PlanJson/plan floor " + floor + ".json");
                String jsonString = XML.toJSONObject(response.toString()).toString();
                Files.write(jsonPath, Collections.singleton(jsonString), Charset.defaultCharset());
            } else {
                throw new IOException("HTTP POST request failed with response code: " + responseCode);
            }
            connection.disconnect();
        } catch (IOException e) {
            e.fillInStackTrace();
        }
    }

    /**
     * Creates a connection to the EPFL plan API.
     *
     * @param floor The floor to search
     * @return The connection to the EPFL plan API
     * @throws IOException If an I/O error occurs
     */
    @NotNull
    private static HttpURLConnection getHttpURLConnection(int floor) throws IOException {
        URL url = new URL(API_URL + floor);
        HttpURLConnection connection = (HttpURLConnection) url.openConnection();
        connection.setRequestMethod("POST");

        // Set headers
        connection.setRequestProperty(
                "User-Agent",
                "Mozilla/5.0 (Windows NT 10.0; Win64; x64; rv:120.0) Gecko/20100101 Firefox/120.0");
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

    /**
     * This class is used to represent a coordinate.
     */
    public record Coordinate(double x, double y) {
    }

    /**
     * This class is used to represent the area to search in.
     */
    public record Area(Coordinate upperCorner, Coordinate lowerCorner) {
    }
}
