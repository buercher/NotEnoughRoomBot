import SearchingRoom.FolderCreation;
import SearchingRoom.TestEPFL;
import SearchingRoom.TestFLEP;
import javafx.collections.FXCollections;
import javafx.collections.ListChangeListener;
import javafx.collections.ObservableList;

import java.io.File;
import java.io.IOException;
import java.util.*;

public class SearchRoom {
    public static void main(String[] args) throws IOException {
        FolderCreation.CreateFolderForTest();

        File directory = new File("resources/RoomList");
        File[] files = directory.listFiles();

        List<String> FlepExist = Arrays.asList(
                "AAC","AI", "BC", "BCH","BS", "BSP","CE", "CH", "CM", "CO", "DIA",
                "ELA","ELD","ELE","ELG","GC", "GR", "INF","INJ","INM","INR","MA",
                "ME", "MED","MXC","MXF","MXG","ODY","PH", "PO", "RLC","SG", "STCC");

        ObservableList<File> buffer = FXCollections.observableArrayList();

        Thread epflThread = new Thread(() -> {
            try {
                if (files != null) {
                    for (File file : files) {
                        TestEPFL.test(file.getName());
                    }
                }
            } catch (IOException e) {
                throw new RuntimeException(e);
            }
        });

        Queue<File> fileQueue =new LinkedList<>();
        buffer.addListener((ListChangeListener<File>) change -> {
            while (change.next()) {
                if (change.wasAdded()) {
                    fileQueue.addAll(change.getList());
                }
            }
        });

        Thread flepThread = new Thread(() -> {
            try {
                while (epflThread.isAlive() || !fileQueue.isEmpty()) {
                    if (!fileQueue.isEmpty()) {
                        File currentFile = fileQueue.poll();
                        if (!FlepExist.contains(currentFile.getName())) {
                            TestFLEP.test(currentFile.getName());
                        }
                    }
                }
            } catch (IOException e) {
                e.fillInStackTrace();
            }
        });

        flepThread.setDaemon(true);
        epflThread.setDaemon(true);
        epflThread.start();
        flepThread.start();
    }
}
