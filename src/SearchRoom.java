import databaseOperation.FolderOperation;
import me.tongfei.progressbar.ProgressBar;
import me.tongfei.progressbar.ProgressBarBuilder;
import me.tongfei.progressbar.ProgressBarStyle;
import org.apache.commons.lang.StringUtils;
import searchingRoom.TestEPFL;
import searchingRoom.TestFLEP;

import java.io.File;
import java.io.IOException;
import java.nio.charset.StandardCharsets;
import java.nio.file.Files;
import java.util.*;
import java.util.stream.Stream;

/**
 * The SearchRoom class provides multiple methods for testing existence of room in EPFL and FLEP websites.
 */
public class SearchRoom {

    private static final String ROOM_LIST_PATH = "database/RoomList/";

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
     * @see TestEPFL#test(String, ProgressBar, String)
     * @see TestFLEP#test(String, ProgressBar)
     */
    public static void main(String[] args) throws IOException {
        File database = new File("database");
        if (!database.exists() && !database.mkdirs()) {
            throw new IOException("Failed to create folder '" + database.getPath() + "'");
        }
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
            pb.maxHint(31156 * 3);

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
                TestEPFL.test(file.getName(), pbEPFL, "");
                fileQueue.add(file);
            }
        }
        while (!fileQueue.isEmpty()) {
            pbEPFL.refresh();
        }
        Thread.sleep(10000);
        File roomWithIssue = new File("database/roomChecking/roomWithIssue/");
        List<File> files2 = List.of(Objects.requireNonNull(roomWithIssue.listFiles()));
        List<String> filesName = new ArrayList<>();
        files2.iterator().forEachRemaining(file ->
                filesName.add(file.getName().replaceAll(".json", "")));

        for (File file : Objects.requireNonNull(files)) {
            if (filesName.contains(file.getName())) {
                TestEPFL.test(file.getName(), pbEPFL, "2");
                Thread.sleep(50);
            } else {
                List<String> a = Files.readAllLines(file.toPath());
                pbEPFL.stepBy(a.size());
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
