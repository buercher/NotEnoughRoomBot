package SearchingRoom;

import java.io.File;
import java.io.IOException;
import java.util.ArrayList;
import java.util.List;

public class FolderCreation {
    private FolderCreation(){};

    public static void CreateFolderForTest() throws IOException {

        File roomChecking = new File("database/roomChecking");
        if (!roomChecking.exists()) {
            if (!roomChecking.mkdir()) {
                throw new IOException("Failed to create folder '" + roomChecking.getPath()+"'");
            }
        }
        List<File> rooms = new ArrayList<>();
        rooms.add(new File("database/roomChecking/fromEPFL"));
        rooms.add(new File("database/roomChecking/fromFLEP"));
        rooms.add(new File("database/roomChecking/roomNotSearchable"));
        rooms.add(new File("database/roomChecking/roomWithIssue"));
        for (File room : rooms) {
            if (!room.exists()) {
                if (!room.mkdir()) {
                    throw new IOException("Failed to create folder '" + room.getPath()+"'");
                }
            }
        }
    }
}
