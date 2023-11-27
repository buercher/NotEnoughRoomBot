import SearchingRoom.FolderCreation;
import SearchingRoom.TestEPFL;
import SearchingRoom.TestFLEP;

import java.io.File;
import java.io.IOException;
import java.util.*;

public class SearchRoom {

    private static final String ROOM_LIST_PATH = "resources/RoomList";

    private static final List<String> FLEP_EXISTS = Arrays.asList(
            "AAC", "AI", "BC", "BCH", "BS", "BSP", "CE", "CH", "CM", "CO", "DIA",
            "ELA", "ELD", "ELE", "ELG", "GC", "GR", "INF", "INJ", "INM", "INR", "MA",
            "ME", "MED", "MXC", "MXF", "MXG", "ODY", "PH", "PO", "RLC", "SG", "STCC");
    private static final Queue<File> fileQueue = new LinkedList<>();

    private static Thread EPFLThread;
    private static Thread FLEPThread;

    public static void main(String[] args) throws IOException {
        FolderCreation.CreateFolderForTest();

        File directory = new File(ROOM_LIST_PATH);
        File[] files = directory.listFiles();

        EPFLThread = new Thread(() -> {
            try {
                EPFLThreadProcess(files);
            } catch (IOException e) {
                throw new RuntimeException(e);
            }
        });

        FLEPThread = new Thread(() -> {
            try {
                FLEPThreadProcess();
            } catch (IOException e) {
                e.fillInStackTrace();
            }
        });

        configureAndStartThreads();
    }

    private static void EPFLThreadProcess(File[] files) throws IOException {
        if (files != null) {
            for (File file : files) {
                TestEPFL.test(file.getName());
                fileQueue.add(file);
            }
        }
    }

    private static void FLEPThreadProcess() throws IOException {
        while (EPFLThread.isAlive() || !fileQueue.isEmpty()) {
            if (!fileQueue.isEmpty()) {
                File currentFile = fileQueue.poll();
                if (!FLEP_EXISTS.contains(currentFile.getName())) {
                    TestFLEP.test(currentFile.getName());
                }
            }
        }
    }

    private static void configureAndStartThreads(){
        FLEPThread.setDaemon(true);
        EPFLThread.setDaemon(true);
        EPFLThread.start();
        FLEPThread.start();
    }
}
