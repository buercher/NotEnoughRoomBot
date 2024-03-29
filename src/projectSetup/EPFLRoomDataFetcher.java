package projectSetup;

import projectSetup.plan.PlanDataFetch;
import projectSetup.plan.RoomJsonToList;
import projectSetup.searchingRoom.AllCases;

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
    public static void fetch() throws IOException {
        File directory = new File("database");
        File subDirectory = new File("database/SetupData");
        File planJson = new File("database/SetupData/PlanJson");


        if (!directory.exists() && !directory.mkdirs()) {
            throw new IOException("Failed to create folder '" + directory.getPath() + "'");
        }
        if (!subDirectory.exists() && !subDirectory.mkdirs()) {
            throw new IOException("Failed to create folder '" + directory.getPath() + "'");
        }
        if (!planJson.exists() && !planJson.mkdir()) {
            throw new IOException("Failed to create folder '" + planJson.getPath() + "'");
        }
        PlanDataFetch.searchAllFloor();
        RoomJsonToList.RoomToJson();
        AllCases.find();
    }
}
