package ProjectSetup.plan;

import com.fasterxml.jackson.databind.DeserializationFeature;
import com.fasterxml.jackson.databind.ObjectMapper;
import Utils.jsonObjects.JsonRoomArchitecture;
import Utils.jsonObjects.planJsonArchtecture.JsonRoom;

import java.io.File;
import java.io.IOException;
import java.nio.file.Files;
import java.util.*;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

/**
 * The RoomJsonToList class is used to convert the JSON files containing the rooms data to a list of rooms.
 */
public class RoomJsonToList {

    /**
     * The RoomJsonToList constructor is private because this class is not meant to be instantiated.
     */
    private RoomJsonToList() {

    }

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
            "AAB", "AAC", "AAD", "AI", "ALO", "ALP", "ART", "AST", "AU", "B25A", "BAC", "BAF", "BAH", "BAP", "BAR",
            "BCH", "BFFA", "BI", "BM", "BP", "BSP", "BS", "CAPU", "CCT", "CE", "CH", "CM", "COV", "CO", "CP1", "CRR",
            "CSB", "CSN", "CSS", "CSV", "DIA", "DLLEL", "ECAL", "ELA", "ELB", "ELD", "ELE", "ELG", "ELH", "ELL", "CL",
            "EPH", "EXTRA", "FBC", "BC", "FO", "G6", "GC", "GEO", "GO10", "GR", "H8", "HBL", "I17", "H4", "I19", "INF",
            "INJ", "INM", "INN", "INR", "JO40", "JORD", "LE", "MA", "MC", "MED", "ME", "MXC", "MXD", "MXE", "MXF",
            "MXG", "MXH", "NH", "ODY", "PL", "GA", "PO", "PPB", "PPH", "PH", "PSEA", "PSEB", "PSEC", "PSED", "PSEL",
            "PS_QN", "PV", "QIE", "QIF", "QIG", "QIH", "QII", "QIJ", "QIK", "QIN", "QIO", "RLC", "B1", "SCT", "SF",
            "SG", "SKIL", "SOS1", "SOS2", "SPN", "SPP", "SSH", "SS", "B3", "STCC", "STF", "STT", "SV", "TCV", "TRIC",
            "TRIE", "TRIH", "VOR", "ZC", "ZD", "ZP", "AN");

    public static final List<String> rooms = new ArrayList<>();

    public static final List<JsonRoomArchitecture> jsonRoomArchitecture = new ArrayList<>();

    /**
     * Extracts the valid rooms from the JSON files and stores them in the database according to their building.
     *
     * @throws IOException If an I/O error occurs
     * @see RoomJsonToList#extractValidRooms(int)
     */
    public static void RoomToJson() throws IOException {
        File roomToConvert = new File("database/SetupData/RoomToConvert");

        File roomsDataJson= new File("database/SetupData/roomsDataJson.json");

        if (!roomToConvert.exists()) {
            if (!roomToConvert.mkdir()) {
                throw new IOException("Failed to create folder '" + roomToConvert.getPath() + "'");
            }
        }

        for (int i = -4; i < 9; i++) {
            extractValidRooms(i);
        }
        for (String building : BUILDINGS) {
            List<String> outputList = new ArrayList<>();
            File output = new File("database/SetupData/RoomToConvert/" + building);
            Files.deleteIfExists(output.toPath());
            for (String room : rooms) {
                if (room.contains(building)) {
                    outputList.add(room);
                }
            }
            rooms.removeAll(outputList);
            if (Objects.equals(building, "BC")) {
                outputList.add("BC 07-08");
            }
            if (!outputList.isEmpty()) {
                Collections.sort(outputList);
                Files.write(output.toPath(), outputList);
            }
        }

        ObjectMapper objectMapper = new ObjectMapper();
        try {
            objectMapper.writeValue(roomsDataJson, jsonRoomArchitecture);
            System.out.println("JSON file created successfully.");
        } catch (IOException e) {
            e.fillInStackTrace();
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
    public static void extractValidRooms(int floor) throws IOException {
        // Load JSON file
        File jsonFile = new File("database/SetupData/PlanJson/plan floor " + floor + ".json");

        // Create ObjectMapper
        ObjectMapper objectMapper = new ObjectMapper();
        objectMapper.enable(DeserializationFeature.ACCEPT_SINGLE_VALUE_AS_ARRAY);

        JsonRoom jsonRoom = objectMapper.readValue(jsonFile, JsonRoom.class);


        // Now you can work with the parsed Java objects
        jsonRoom.
                getWfsFeatureCollection().
                getGmlFeatureMember().forEach(gmlFeatureMember -> {
                            if (WHITE_LIST.contains(gmlFeatureMember.getMsBatimentsWmsquery().getMsRoomUtiA())) {
                                String room = extractRoomText(
                                        gmlFeatureMember
                                                .getMsBatimentsWmsquery()
                                                .getMsRoomAbrLink());
                                rooms.add(room);
                                JsonRoomArchitecture jsonRoomArchitecture1 = new JsonRoomArchitecture();
                                jsonRoomArchitecture1.setRooms(room
                                        .replaceAll("-", "")
                                        .replaceAll(" ", "")
                                        .replaceAll("\\.", "")
                                        .replaceAll("_", ""));
                                jsonRoomArchitecture1.setBuildings(
                                        extractBuilding(room));
                                jsonRoomArchitecture1.setPlanName(room);
                                jsonRoomArchitecture1.setPdfLink(
                                        extractPdfLink(
                                                gmlFeatureMember
                                                        .getMsBatimentsWmsquery()
                                                        .getMsPdfLink()));
                                jsonRoomArchitecture1.setPlanLink(
                                        extractPlanLink(
                                                gmlFeatureMember
                                                        .getMsBatimentsWmsquery()
                                                        .getMsRoomAbrLink()));
                                jsonRoomArchitecture1.setType(
                                        gmlFeatureMember
                                                .getMsBatimentsWmsquery()
                                                .getMsRoomUtiA());
                                jsonRoomArchitecture1.setPlaces(
                                        gmlFeatureMember
                                                .getMsBatimentsWmsquery()
                                                .getMsRoomPlace());
                                jsonRoomArchitecture1.setFloor(String.valueOf(floor));
                                jsonRoomArchitecture.add(jsonRoomArchitecture1);
                            }
                        }
                );
    }

    /**
     * Extracts the room text from the given input.
     *
     * @param input The input from which the room text is extracted
     * @return The room text
     */
    public static String extractRoomText(String input) {
        String patternString = "<div class=\"room\">(.*?)</div>";
        Pattern pattern = Pattern.compile(patternString);
        Matcher matcher = pattern.matcher(input);

        if (matcher.find()) {
            return matcher.group(1).trim();
        } else {
            return "No match found";
        }
    }

    public static String extractPdfLink(String input) {
        String patternString = "<a target=\"_blank\" href=\"(.*?)\">lien</a>";
        Pattern pattern = Pattern.compile(patternString);
        Matcher matcher = pattern.matcher(input);

        if (matcher.find()) {
            return matcher.group(1).trim();
        } else {
            return "";
        }
    }

    public static String extractPlanLink(String input) {
        String patternString =
                "<div><button class=\"clipboard\" data-clipboard-text=\"(.*?)\" translate>Copier le lien</div>";
        Pattern pattern = Pattern.compile(patternString);
        Matcher matcher = pattern.matcher(input);

        if (matcher.find()) {
            return matcher.group(1).trim();
        } else {
            return "";
        }
    }
    public static String extractBuilding(String input) {

        for(String building:BUILDINGS){
            if(input.contains(building)){
                return building;
            }
        }
        throw new NoSuchElementException("la Salle"+input+"est dans aucun bâtiment");
    }
}
