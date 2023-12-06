import plan.PlanDataFetch;
import plan.RoomJsonToList;
import searchingRoom.AllCases;

import java.io.File;
import java.io.IOException;

/**
 * The EPFLRoomDataFetcher class fetches the data from the EPFL website and stores it in the database.
 */
public class EPFLRoomDataFetcher {

    /**
     * The EPFLRoomDataFetcher constructor is private because this class is not meant to be instantiated.
     */
    private EPFLRoomDataFetcher() {
    }

    /**
     * Fetches the data from the EPFL website and stores it in the database.
     *
     * @throws IOException If an I/O error occurs
     * @see PlanDataFetch#searchAllFloor()
     * @see RoomJsonToList#RoomToJson()
     */
    public static void main(String[] args) throws IOException {
        File directory = new File("database");
        if (!directory.exists() && !directory.mkdirs()) {
            throw new IOException("Failed to create folder '" + directory.getPath() + "'");
        }
        File roomChecking = new File("database/PlanJson");
        if (!roomChecking.exists()) {
            if (!roomChecking.mkdir()) {
                throw new IOException("Failed to create folder '" + roomChecking.getPath() + "'");
            }
        }
        PlanDataFetch.searchAllFloor();
        RoomJsonToList.RoomToJson();
        AllCases.find();
    }
}
