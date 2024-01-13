package projectSetup.plan;

import com.fasterxml.jackson.databind.DeserializationFeature;
import com.fasterxml.jackson.databind.ObjectMapper;
import utils.jsonObjects.JsonRoomArchitecture;
import utils.jsonObjects.planJsonArchtecture.JsonRoom;

import java.io.File;
import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Paths;
import java.util.*;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

/**
 * The RoomJsonToList class is used to convert the JSON files containing the rooms data to a list of rooms.
 */
public class RoomJsonToList {

    public static final List<String> rooms = new ArrayList<>();
    public static final List<JsonRoomArchitecture> jsonRoomArchitecture = new ArrayList<>();
    private static final int MAX_FLOOR = 8;
    private static final int MIN_FLOOR = -4;
    private static final List<String> WHITE_LIST = Arrays.asList(
            "LABO", "DETENTE", "CONFERENCES", "BUREAU", "ATELIERS", "ATELIER", "BIBLIOTHEQUE NEBIS",
            "LOCAUX SECOND", "AUDIO-VISUEL", "ABRI BUREAU", "BIBLIOTHEQUE", "SALLE TP", "MAGASIN",
            "ABRI REFECTOIRE", "SALLE DE COURS", "RECEPTION", "CAFETERIA",
            "TUTORAT", "SERVICE", "ESPACE COLLABORATIF",
            "BUREAUTIQUE", "CONF MULTIMEDIA", "AFFECT DIV", "PHOTOS", "RESTAURANT", "AUDITOIRE", "BUREAUX",
            "VENTE", "ESPACE FORUM", "ACCUEIL", "LABO INFORM", "REUNION-ATTENTE", "HOSTDESK", "STUDIOS",
            "HELP DESK", "PATIENTS", "ATTENTE", "CHAMBRE NOIRE", "SECRETARIAT",
            "AUMONERIE", "DESSIN", "REUNION", "LIBRAIRIE", "CULTURE");
    private static final List<String> BUILDINGS = Arrays.asList(
            "AAB", "AAC", "AAD", "AI", "ALO", "ALP", "ART", "AST", "SAUV", "AU", "B25A", "BAC", "BAF", "BAH", "BAP",
            "BAR", "BCH", "BFFA", "BI", "BM", "BP", "BSP", "BS", "CAPU", "CCT", "CE", "CH", "CM", "COV", "CO", "CP1",
            "CRR", "CSB", "CSN", "CSS", "CSV", "DIA", "DLLEL", "ECAL", "ELA", "ELB", "ELD", "ELE", "ELG", "ELH", "ELL",
            "CL", "EPH", "EXTRA", "FBC", "BC", "FO", "G6", "GC", "GEN", "GEO", "GO10", "GR", "H8", "HBL", "I17", "H4",
            "I19", "INF", "INJ", "INM", "INN", "INR", "JO40", "JORD", "LE", "MA", "MC", "MED", "ME", "MXC", "MXD",
            "MXE", "MXF", "MXG", "MXH", "NH", "ODY", "PL", "GA", "PO", "PPB", "PPH", "PH", "PSEA", "PSEB", "PSEC",
            "PSED", "PSEL", "PS_QN", "PV", "QIE", "QIF", "QIG", "QIH", "QII", "QIJ", "QIK", "QIN", "QIO", "RLC", "B1",
            "SCT", "SF", "SG", "SKIL", "SOS1", "SOS2", "SPN", "SPP", "SSH", "SS", "B3", "STCC", "STF", "STT", "SV",
            "TCV", "TRIC", "TRIE", "TRIH", "VOR", "ZC", "ZD", "ZP", "AN");

    /**
     * The RoomJsonToList constructor is private because this class is not meant to be instantiated.
     */
    private RoomJsonToList() {
    }

    /**
     * Extracts the valid rooms from the JSON files and stores them in the database according to their building.
     *
     * @throws IOException If an I/O error occurs
     * @see RoomJsonToList#extractValidRooms(int)
     */
    public static void RoomToJson() throws IOException {
        File roomToConvert = new File("database/SetupData/RoomToConvert");

        File roomsDataJson = new File("database/SetupData/roomsDataJson.json");

        if (!roomToConvert.exists()) {
            if (!roomToConvert.mkdir()) {
                throw new IOException("Failed to create folder '" + roomToConvert.getPath() + "'");
            }
        }
        try {
            for (int i = MIN_FLOOR; i <= MAX_FLOOR; i++) {
                extractValidRooms(i);
            }
            for (String building : BUILDINGS) {
                List<String> outputList = new ArrayList<>(rooms.stream()
                        .filter(room -> room.contains(building))
                        .toList());
                rooms.removeAll(outputList);
                if (building.equals("BC")) {
                    outputList.add("BC 07-08");
                }
                if (!outputList.isEmpty()) {
                    Collections.sort(outputList);
                    Files.write(Paths.get("database/SetupData/RoomToConvert/" + building), outputList);
                }
            }

            ObjectMapper objectMapper = new ObjectMapper();
            objectMapper.writeValue(roomsDataJson, jsonRoomArchitecture);
        } catch (IOException e) {
            throw new IOException("An error occurred while processing the rooms: " + e.getMessage());
        }
    }

    /**
     * Extracts the valid rooms from the JSON file of the given floor and stores them in the database.
     *
     * @param floor The floor of the rooms to be extracted
     * @throws IOException If an I/O error occurs
     * @see JsonRoom
     * @see ObjectMapper
     */
    private static void extractValidRooms(int floor) throws IOException {
        // Load JSON file
        File jsonFile = new File("database/SetupData/PlanJson/plan floor " + floor + ".json");

        // Create ObjectMapper
        ObjectMapper objectMapper = new ObjectMapper();
        objectMapper.enable(DeserializationFeature.ACCEPT_SINGLE_VALUE_AS_ARRAY);

        try {
            JsonRoom jsonRoom = objectMapper.readValue(jsonFile, JsonRoom.class);

            // Now you can work with the parsed Java objects
            jsonRoom.getWfsFeatureCollection()
                    .getGmlFeatureMember().stream()
                    .filter(gmlFeatureMember ->
                            WHITE_LIST.contains(gmlFeatureMember.getMsBatimentsWmsquery().getMsRoomUtiA()))
                    .forEach(gmlFeatureMember -> {
                        String room = extractRoomText(
                                gmlFeatureMember
                                        .getMsBatimentsWmsquery()
                                        .getMsRoomAbrLink());
                        rooms.add(room);
                        jsonRoomArchitecture.add(
                                new JsonRoomArchitecture(
                                        room.replaceAll("[^A-Za-z0-9]", ""),
                                        BUILDINGS
                                                .stream()
                                                .filter(room::contains)
                                                .findFirst()
                                                .orElseThrow(() ->
                                                        new NoSuchElementException(
                                                                "la Salle" + room +
                                                                        "est dans aucun bâtiment")),
                                        room,
                                        extractPdfLink(gmlFeatureMember.getMsBatimentsWmsquery().getMsPdfLink()),
                                        extractPlanLink(gmlFeatureMember.getMsBatimentsWmsquery().getMsRoomAbrLink()),
                                        gmlFeatureMember.getMsBatimentsWmsquery().getMsRoomUtiA(),
                                        gmlFeatureMember.getMsBatimentsWmsquery().getMsRoomPlace(),
                                        String.valueOf(floor)
                                ));
                    });

        } catch (IOException e) {
            throw new IOException("An error occurred while processing the rooms: " + e.getMessage());
        }
    }

    /**
     * Extracts the room text from the given input.
     *
     * @param input The input string from which the room text is extracted
     * @return The room text
     * @throws NoSuchElementException If the room text cannot be found in the input string
     */
    private static String extractRoomText(String input) {
        final String ROOM_TEXT_REGEX = "<div class=\"room\">(.*?)</div>";
        Pattern pattern = Pattern.compile(ROOM_TEXT_REGEX);
        Matcher matcher = pattern.matcher(input);

        if (matcher.find()) {
            return matcher.group(1).trim();
        } else {
            throw new NoSuchElementException("No room text found in the input string");
        }
    }

    /**
     * Extracts the PDF link from the given input.
     *
     * @param input The input from which the PDF link is extracted
     * @return The PDF link
     */
    private static String extractPdfLink(String input) {
        final String PDF_LINK_REGEX = "<a target=\"_blank\" href=\"(.*?)\">lien</a>";
        Pattern pattern = Pattern.compile(PDF_LINK_REGEX);
        Matcher matcher = pattern.matcher(input);

        if (matcher.find()) {
            return matcher.group(1).trim();
        } else {
            return "";
        }
    }

    /**
     * Extracts the plan link from the given input.
     *
     * @param input The input from which the plan link is extracted
     * @return The plan link
     */
    private static String extractPlanLink(String input) {
        final String PLAN_LINK_REGEX =
                "<div><button class=\"clipboard\" data-clipboard-text=\"(.*?)\" translate>Copier le lien</div>";
        Pattern pattern = Pattern.compile(PLAN_LINK_REGEX);
        Matcher matcher = pattern.matcher(input);

        if (matcher.find()) {
            return matcher.group(1).trim();
        } else {
            return "";
        }
    }
}
