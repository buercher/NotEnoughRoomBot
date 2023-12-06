import hoursSearch.EPFL;
import hoursSearch.FLEP;
import databaseOperation.JsonOperation;

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
    public static void main(String[] args) throws IOException {
        EPFL.scrap();
        System.out.println("EPFL done");
        FLEP.scrap();
        System.out.println("FLEP done");
        JsonOperation.makeFile();
        System.out.println("Json Made");
    }
}
