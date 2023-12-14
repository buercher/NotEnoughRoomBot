package ProjectSetup;

import ProjectSetup.AllValidRoomToJson;
import ProjectSetup.EPFLRoomDataFetcher;
import ProjectSetup.SearchRoom;

import java.io.IOException;

public class EntireProjectSetup {
    public static void main(String[] args) throws IOException {
        EPFLRoomDataFetcher.fetch();
        SearchRoom.find();
        AllValidRoomToJson.find();
    }
}
