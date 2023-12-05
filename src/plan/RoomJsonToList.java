package plan;

import com.fasterxml.jackson.databind.DeserializationFeature;
import com.fasterxml.jackson.databind.ObjectMapper;
import plan.jsonArchitecture.JsonRoom;

import java.io.File;
import java.io.IOException;
import java.nio.file.Files;
import java.util.*;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

public class RoomJsonToList {
    private static final List<String> whiteList = Arrays.asList(
            "LABO", "CONFERENCES", "BUREAU", "ATELIERS", "ATELIER", "BIBLIOTHEQUE NEBIS",
            "LOCAUX SECOND", "ABRI BUREAU", "BIBLIOTHEQUE", "SALLE TP", "MAGASIN",
            "ABRI REFECTOIRE", "SALLE DE COURS", "RECEPTION", "CAFETERIA",
            "TUTORAT", "SERVICE", "ESPACE COLLABORATIF",
            "BUREAUTIQUE", "CONF MULTIMEDIA", "AFFECT DIV", "PHOTOS", "RESTAURANT", "AUDITOIRE", "BUREAUX",
            "VENTE", "ESPACE FORUM", "ACCUEIL", "LABO INFORM", "REUNION-ATTENTE", "HOSTDESK", "STUDIOS",
            "HELP DESK", "PATIENTS", "ATTENTE", "CHAMBRE NOIRE", "SECRETARIAT",
            "AUMONERIE", "DESSIN", "REUNION", "LIBRAIRIE", "CULTURE");

    private static final List<String> buildings = Arrays.asList(
            "AAB", "AAC", "AAD", "AI", "ALO", "ALP", "ART", "AST", "AU", "B25A", "BAC", "BAF", "BAH",
            "BAP", "BAR", "BCH", "BFFA", "BI", "BM", "BP", "BSP", "BS", "CAPU", "CCT", "CE", "CH", "CM",
            "COV", "CO", "CP1", "CRR", "CSB", "CSN", "CSS", "CSV", "DIA", "DLLEL", "ECAL", "ELA", "ELB", "ELD", "ELE",
            "ELG", "ELH", "ELL", "CL", "EPH", "EXTRA", "FBC", "BC", "FO", "G6", "GC", "GEO", "GO10", "GR", "H8", "HBL",
            "I17", "H4", "I19", "INF", "INJ", "INM", "INN", "INR", "JO40", "JORD", "LE", "MA", "MC", "MED", "ME", "MXC",
            "MXD", "MXE", "MXF", "MXG", "MXH", "NH", "ODY", "PL", "GA", "PO", "PPB", "PPH", "PH", "PSEA", "PSEB", "PSEC",
            "PSED", "PSEL", "PS_QN", "PV", "QIE", "QIF", "QIG", "QIH", "QII", "QIJ", "QIK", "QIN", "QIO", "RLC", "B1", "SCT",
            "SF", "SG", "SKIL", "SOS1", "SOS2", "SPN", "SPP", "SSH", "SS", "B3", "STCC", "STF", "STT", "SV", "TCV", "TRIC",
            "TRIE", "TRIH", "VOR", "ZC", "ZD", "ZP", "AN");
    public static final List<String> rooms = new ArrayList<>();

    public static void main(String[] args) throws IOException {
        for (int i = -4; i < 9; i++) {
            test(i);
        }
        for (String building : buildings) {
            List<String> outputList = new ArrayList<>();
            File output = new File("resources/RoomToConvert/" + building);
            Files.deleteIfExists(output.toPath());
            for (String room : rooms) {
                if (room.contains(building)) {
                    outputList.add(room);
                }
            }
            rooms.removeAll(outputList);
            if(Objects.equals(building, "BC")){
                outputList.add("BC 07-08");
            }
            if (!outputList.isEmpty()) {
                Collections.sort(outputList);
                Files.write(output.toPath(), outputList);
            }
        }
    }

    public static void test(int floor) throws IOException {
        // Load JSON file
        File jsonFile = new File("resources/PlanJson/plan floor " + floor + ".json");

        // Create ObjectMapper
        ObjectMapper objectMapper = new ObjectMapper();
        objectMapper.enable(DeserializationFeature.ACCEPT_SINGLE_VALUE_AS_ARRAY);

        JsonRoom jsonRoom = objectMapper.readValue(jsonFile, JsonRoom.class);

        // Now you can work with the parsed Java objects
        jsonRoom.
                getWfsFeatureCollection().
                getGmlFeatureMember().forEach(gmlFeatureMember -> {
                    if (whiteList.contains(gmlFeatureMember.getMsBatimentsWmsquery().getMsRoomUtiA())) {
                        rooms.add(extractRoomText(
                                gmlFeatureMember.
                                        getMsBatimentsWmsquery()
                                        .getMsRoomAbrLink()));
                    }
                });
    }

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
}
