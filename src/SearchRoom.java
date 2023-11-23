import SearchingRoom.FolderCreation;
import SearchingRoom.TestEPFL;
import SearchingRoom.TestFLEP;

import java.io.File;
import java.io.IOException;

public class SearchRoom {
    public static void main(String[] args) throws IOException {
        FolderCreation.CreateFolderForTest();
        File directory=new File("resources/RoomList");
        File[] files = directory.listFiles();
        if (files!=null){
            for(File file: files){
                TestEPFL.test(file.getName());
                TestFLEP.test(file.getName());
            }
        }
    }
}
