package telegramBots;

import telegramBots.hoursSearch.EPFL;
import telegramBots.hoursSearch.FLEP;
import utils.databaseOperation.JsonOperation;

import java.io.IOException;

/**
 * The DatabaseSearch class provides multiple methods for fetching data from the EPFL and FLEP websites.
 */
public class DatabaseSearch {

    /**
     * Private constructor to prevent instantiation of this utility class.
     */
    private DatabaseSearch() {
    }

    /**
     * Scrapes the data from the EPFL and FLEP websites and stores it in the database.
     *
     * @throws IOException If an I/O error occurs
     * @see EPFL#scrap()
     * @see FLEP#scrap()
     * @see JsonOperation#makeFile()
     */
    public static void fetch() throws IOException {
        EPFL.scrap();
        FLEP.scrap();
        JsonOperation.makeFile();
    }
}
