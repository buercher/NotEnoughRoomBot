package projectSetup;

import me.tongfei.progressbar.ProgressBar;
import me.tongfei.progressbar.ProgressBarBuilder;
import me.tongfei.progressbar.ProgressBarStyle;
import org.apache.commons.lang.StringUtils;
import projectSetup.searchingRoom.TestEPFL;
import projectSetup.searchingRoom.TestFLEP;
import utils.databaseOperation.FolderOperation;

import java.io.File;
import java.io.IOException;
import java.nio.charset.StandardCharsets;
import java.nio.file.Files;
import java.util.Arrays;
import java.util.LinkedList;
import java.util.List;
import java.util.Queue;
import java.util.stream.Stream;

/**
 * The SearchRoom class provides multiple methods for testing existence of room in EPFL and FLEP websites.
 */
public class SearchRoom {

    private static final String ROOM_LIST_PATH = "database/SetupData/RoomList/";

    private static final List<String> FLEP_EXISTS = Arrays.asList(
            "AAC", "AI", "BC", "BCH", "BS", "BSP", "CE", "CH", "CM", "CO", "DIA",
            "ELA", "ELD", "ELE", "ELG", "GC", "GR", "INF", "INJ", "INM", "INR", "MA",
            "ME", "MED", "MXC", "MXF", "MXG", "ODY", "PH", "PO", "RLC", "SG", "STCC");
    private static final Queue<File> fileQueue = new LinkedList<>();
    private static final int AllCASES_ROOMS_COUNT = 31250;
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
     * @see TestEPFL#test(String, ProgressBar)
     * @see TestFLEP#test(String, ProgressBar)
     */
    public static void find() throws IOException {
        FolderOperation.CreateFoldersForTest();

        File directory = new File(ROOM_LIST_PATH);
        File[] files = directory.listFiles();

        ProgressBarBuilder pbb = ProgressBar.builder()
                .setStyle(ProgressBarStyle.builder()
                        .refreshPrompt("\r")
                        .leftBracket("\u001b[1:36m")
                        .delimitingSequence("\u001b[1:34m")
                        .rightBracket("\u001b[1:34m")
                        .block('━')
                        .space('━')
                        .fractionSymbols(" ╸")
                        .rightSideFractionSymbol('╺')
                        .build())
                .setTaskName("Total")
                .setMaxRenderedLength(111);

        try (ProgressBar pb = pbb.build()) {
            pb.maxHint(AllCASES_ROOMS_COUNT * 2);

            EPFLThread = new Thread(() -> {
                try {
                    EPFLThreadProcess(files, pb);
                } catch (IOException | InterruptedException e) {
                    throw new RuntimeException(e);
                }
            });

            FLEPThread = new Thread(() -> {
                try {
                    FLEPThreadProcess(pb);
                } catch (IOException e) {
                    e.fillInStackTrace();
                }
            });
            configureAndStartThreads();
            pb.setExtraMessage(StringUtils.rightPad(" done", 14));
        }
    }

    /**
     * Static method to process EPFLThread.
     *
     * @param files The files to be processed
     * @throws IOException If an I/O error occurs
     */
    private static void EPFLThreadProcess(File[] files, ProgressBar pbEPFL) throws IOException, InterruptedException {
        if (files != null) {
            for (File file : files) {
                TestEPFL.test(file.getName(), pbEPFL);
                fileQueue.add(file);
            }
        }

    }

    /**
     * Static method to process FLEPThread.
     *
     * @throws IOException If an I/O error occurs
     */
    private static void FLEPThreadProcess(ProgressBar pbFLEP) throws IOException {
        while (EPFLThread.isAlive() || !fileQueue.isEmpty()) {
            if (!fileQueue.isEmpty()) {
                File currentFile = fileQueue.poll();
                if (FLEP_EXISTS.contains(currentFile.getName())) {
                    TestFLEP.test(currentFile.getName(), pbFLEP);
                } else {
                    try (Stream<String> lines = Files.lines(currentFile.toPath(), StandardCharsets.UTF_8)) {
                        pbFLEP.stepBy(lines.count());
                    }
                }
            }
        }
    }

    /**
     * Static method to configure and start threads.
     */
    private static void configureAndStartThreads() {
        FLEPThread.setDaemon(true);
        EPFLThread.setDaemon(true);
        EPFLThread.start();
        FLEPThread.start();
        try {
            EPFLThread.join();
        } catch (InterruptedException e) {
            // Interrupted, stop EPFLThread and allow FLEPThread to continue
            EPFLThread.interrupt();
        }

        try {
            FLEPThread.join();
        } catch (InterruptedException e) {
            // Interrupted, stop FLEPThread and allow EPFLThread to continue
            FLEPThread.interrupt();
        }
    }
}
