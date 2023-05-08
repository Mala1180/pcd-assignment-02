package executors;

import common.ReadFilesTask;

import java.nio.file.Path;
import java.util.Set;
import java.util.concurrent.*;

public class SourceAnalyser {

    private static SourceAnalyser instance;
    private Set<Path> files;
    private String path;

    private SourceAnalyser() {
    }

    public static SourceAnalyser getInstance() {
        synchronized (SourceAnalyser.class) {
            if (instance == null) {
                instance = new SourceAnalyser();
            }
        }
        return instance;
    }

    public void getReport(String path) {
        this.readFiles();
        this.countLines();
    }

    private void readFiles() {
        try (ExecutorService executor = Executors.newSingleThreadExecutor()) {
            Future<Set<Path>> future = executor.submit(new ReadFilesTask(this.path));
            this.files = future.get();
            executor.shutdown();
            boolean isTerminatedCorrectly = executor.awaitTermination(30, TimeUnit.SECONDS);
            if (!isTerminatedCorrectly) {
                throw new RuntimeException("Executor terminated with timeout");
            }
        } catch (ExecutionException | InterruptedException e) {
            throw new RuntimeException(e);
        }
    }

    private void countLines() {
        for (Path file : this.files) {
            System.out.println(file);
        }
    }

}
