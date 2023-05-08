package executors;

import common.CountFileLinesTask;
import common.Pair;
import common.ReadFilesTask;

import java.nio.file.Path;
import java.util.HashSet;
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
        this.path = path;
        this.readFiles();
        this.countLines();
    }

    private void readFiles() {
        try (ExecutorService executor = Executors.newSingleThreadExecutor()) {
            Future<Set<Path>> future = executor.submit(new ReadFilesTask(this.path));
            this.files = future.get();
            shutdownExecutor(executor);
        } catch (ExecutionException | InterruptedException e) {
            throw new RuntimeException(e);
        }
    }

    private void countLines() {
        try (ExecutorService executor = Executors.newCachedThreadPool()) {
            Set<Future<Pair<String, Integer>>> futures = new HashSet<>();
            for (Path file : this.files) {
                futures.add(executor.submit(new CountFileLinesTask(file)));
            }
            shutdownExecutor(executor);
            for (Future<Pair<String, Integer>> future : futures) {
                Pair<String, Integer> pair = future.get();
                System.out.println("File " + pair.getFirst() + " has lines: " + pair.getSecond());
            }
        } catch (Exception e) {
            throw new RuntimeException(e);
        }

    }

    private static void shutdownExecutor(ExecutorService executor) throws InterruptedException {
        executor.shutdown();
        boolean isTerminatedCorrectly = executor.awaitTermination(30, TimeUnit.SECONDS);
        if (!isTerminatedCorrectly) {
            throw new RuntimeException("Executor terminated with timeout");
        }
    }

}
