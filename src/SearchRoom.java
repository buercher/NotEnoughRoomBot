import databaseOperation.FolderOperation;
import searchingRoom.TestEPFL;
import searchingRoom.TestFLEP;

import java.io.File;
import java.io.IOException;
import java.util.*;

/**
 * The SearchRoom class provides multiple methods for testing existence of room in EPFL and FLEP websites.
 */
public class SearchRoom {

    private static final String ROOM_LIST_PATH = "resources/RoomList";

    private static final List<String> FLEP_EXISTS = Arrays.asList(
            "AAC", "AI", "BC", "BCH", "BS", "BSP", "CE", "CH", "CM", "CO", "DIA",
            "ELA", "ELD", "ELE", "ELG", "GC", "GR", "INF", "INJ", "INM", "INR", "MA",
            "ME", "MED", "MXC", "MXF", "MXG", "ODY", "PH", "PO", "RLC", "SG", "STCC");
    private static final Queue<File> fileQueue = new LinkedList<>();
    private static Thread EPFLThread;
    private static Thread FLEPThread;

    /**
     * Private constructor to prevent instantiation of this utility class.
     */
    private SearchRoom() {
    }

    /**
     * Tests the existence of rooms in EPFL and FLEP websites using multiple threads to expedite the process.
     * Each thread handles a different website, where EPFLThread has priority over FLEPThread.
     *
     * @throws IOException If an I/O error occurs during the testing process
     * @see FolderOperation#CreateFoldersForTest()
     * @see TestEPFL#test(String)
     * @see TestFLEP#test(String)
     */
    public static void main(String[] args) throws IOException {
        FolderOperation.CreateFoldersForTest();

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

        FLEPThread.setDaemon(true);
        EPFLThread.setDaemon(true);
        EPFLThread.start();
        FLEPThread.start();
    }

    /**
     * Static method to process EPFLThread.
     *
     * @param files The files to be processed
     * @throws IOException If an I/O error occurs
     */
    private static void EPFLThreadProcess(File[] files) throws IOException {
        if (files != null) {
            for (File file : files) {
                TestEPFL.test(file.getName());
                fileQueue.add(file);
            }
        }
    }

    /**
     * Static method to process FLEPThread.
     *
     * @throws IOException If an I/O error occurs
     */
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
}
