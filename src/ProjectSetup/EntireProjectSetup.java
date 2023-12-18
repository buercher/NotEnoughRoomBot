package ProjectSetup;

import java.io.IOException;

/**
 * This class is used to set up the entire project.
 * It will fetch the data from Plan ,EPFL and FLEP, then it will search for all the valid rooms.
 * Finally, it will create a JSON file with all the valid rooms.
 * Use this class only once, if you want to set up the project.
 */
public class EntireProjectSetup {

    /**
     * This class is not meant to be instantiated.
     */
    private EntireProjectSetup() {
    }

    /**
     * This method is used to set up the entire project.
     * It will fetch the data from Plan ,EPFL and FLEP, then it will search for all the valid rooms.
     * Finally, it will create a JSON file with all the valid rooms.
     * Use this method only once, if you want to set up the project.
     *
     * @throws IOException if a file is not found during the process (shouldn't normally happen).
     */
    public static void main(String[] args) throws IOException {
        EPFLRoomDataFetcher.fetch();
        SearchRoom.find();
        AllValidRoomToJson.find();
    }
}
