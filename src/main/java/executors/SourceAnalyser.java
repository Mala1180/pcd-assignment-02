package executors;

import common.Report;
import executors.tasks.CountFileLinesTask;
import common.Pair;
import executors.tasks.ReadFilesTask;

import java.nio.file.Path;
import java.util.*;
import java.util.concurrent.*;

public class SourceAnalyser {

    private static SourceAnalyser instance;
    private Set<Path> files;
    private final Set<Pair<String, Integer>> computedFiles = new HashSet<>();
    private String path;
    private int intervals;
    private int maxLines;

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

    public Report getReport(String path, int intervals, int maxLines) {
        this.path = path;
        this.intervals = intervals;
        this.maxLines = maxLines;
        this.readFiles();
        this.countLines();
        return this.makeReport();
    }


    private void readFiles() {
        try {
            ExecutorService executor = Executors.newSingleThreadExecutor();
            Future<Set<Path>> future = executor.submit(new ReadFilesTask(this.path));
            this.files = future.get();
            shutdownExecutor(executor);
        } catch (ExecutionException | InterruptedException e) {
            throw new RuntimeException(e);
        }
    }

    private void countLines() {
        try {
            ExecutorService executor = Executors.newCachedThreadPool();
            Set<Future<Pair<String, Integer>>> futures = new HashSet<>();
            for (Path file : this.files) {
                futures.add(executor.submit(new CountFileLinesTask(file)));
            }
            shutdownExecutor(executor);
            for (Future<Pair<String, Integer>> future : futures) {
                Pair<String, Integer> pair = future.get();
                computedFiles.add(pair);
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

    private Report makeReport() {
        List<Pair<String, Integer>> longestFiles = this.computedFiles.stream()
                .sorted((o1, o2) -> o2.getSecond().compareTo(o1.getSecond()))
                .limit(5).toList();
        Map<String, Integer> distributions = new HashMap<>();
        for (int i = 0; i < this.intervals; i++) {
            distributions.put("Interval " + (i + 1), 0);
        }
        int linesPerInterval = maxLines / (intervals - 1);
        for (var file : this.computedFiles) {
            int index = file.getSecond() / linesPerInterval;
            if (index >= intervals) {
                index = intervals - 1;
            }
            String key = "Interval " + (index + 1);
            distributions.put(key, distributions.get(key) + 1);
        }
        return new Report(longestFiles, distributions);
    }

}
