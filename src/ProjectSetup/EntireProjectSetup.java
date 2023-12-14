package ProjectSetup;

import java.io.IOException;

public class EntireProjectSetup {
    public static void main(String[] args) throws IOException {
        EPFLRoomDataFetcher.fetch();
        SearchRoom.find();
        AllValidRoomToJson.find();
    }
}
