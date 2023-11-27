import hoursSearch.EPFL;
import hoursSearch.FLEP;
import databaseOperation.JsonOperation;

import java.io.IOException;
public class DatabaseSearch {

    private DatabaseSearch(){}

    public static void main(String[] args) throws IOException {
        EPFL.scrap();
        System.out.println("EPFL done");
        FLEP.scrap();
        System.out.println("FLEP done");
        JsonOperation.makeFile();
        System.out.println("Json Made");
    }
}
