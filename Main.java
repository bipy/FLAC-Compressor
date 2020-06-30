import java.io.*;
import java.util.ArrayList;
import java.util.concurrent.*;
import java.util.concurrent.atomic.AtomicInteger;

public class Main {
    // Config
    private static String INPUT_PATH = "D:\\Users\\Desktop\\[Hi-Res] Reol - 事実上(Special edition)[96kHz／24bit][FLAC]";
    private static final String FLAC_PATH = "D:\\flac-1.3.2-win\\win64\\flac.exe";
    private static final int THREAD_COUNT = 4;

    private static Runtime runtime = Runtime.getRuntime();

    private static int sum = 1;
    private static AtomicInteger count = new AtomicInteger(0);

    private static ArrayList<File> targetList = new ArrayList<>();
    private static ArrayList<String> failList = new ArrayList<>();


    public static void main(String[] args) {
        init();
        File inputFile = new File(INPUT_PATH);
        find(inputFile);
        sum = targetList.size();
        process();

        if (!failList.isEmpty()) {
            System.out.println("Oops! Some of them are failed:");
            for (String f : failList) {
                System.out.println("Fail: " + f);
            }
        }
        System.out.println(String.format("\nProcess Complete! Total: %d - Success: %d - Fail: %d",
                sum, sum - failList.size(), failList.size()));
    }

    public static void init() {
        try {
            if (!new File(INPUT_PATH).exists()) {
                throw new Exception("ERROR: INPUT PATH NOT FOUND");
            }
            if (!new File(INPUT_PATH).isDirectory()) {
                throw new Exception("ERROR: INPUT PATH IS NOT A DIRECTORY");
            }
        } catch (Exception e) {
            e.printStackTrace();
            System.exit(1);
        }
    }

    public static void find(File currentFile) {
        File[] files = currentFile.listFiles();
        ArrayList<File> currentTargetList = new ArrayList<>();
        for (File file : files) {
            if (file.isDirectory()) {
                find(file);
            } else if (file.getName().matches(".*[.](flac)$")) {
                currentTargetList.add(file);
            }
        }
        targetList.addAll(currentTargetList);
    }

    public static void process() {
        System.out.println("======= Multi Thread Mode =======");
        ExecutorService service = Executors.newFixedThreadPool(THREAD_COUNT);
        for (File file : targetList) {
            service.submit(new CompressTask(file.getPath()));
        }
        service.shutdown();
        try {
            service.awaitTermination(72, TimeUnit.HOURS);
        } catch (InterruptedException e) {
            System.out.println("运行超时");
            System.exit(2);
        }
    }

    public static void compress(String taskPath) {
        String[] args = new String[]{
                FLAC_PATH, "-8", "-e", "-p", "-f", "-s", taskPath
        };
        if (!compress(args)) {
            failList.add(taskPath);
        } else {
            System.out.println(String.format("(%d/%d) %s success",
                    count.addAndGet(1), sum, taskPath));
        }
    }

    public static Boolean compress(String[] args) {
        try {
            Process p = runtime.exec(args);
            p.waitFor();
            return true;
        } catch (Exception e) {
            return false;
        }
    }
}

class CompressTask implements Runnable {
    private String taskPath;

    public CompressTask(String taskPath) {
        this.taskPath = taskPath;
    }

    @Override
    public void run() {
        Main.compress(taskPath);
    }
}
