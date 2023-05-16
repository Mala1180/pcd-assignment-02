package sourceanalyzer.strategy.executors;

import sourceanalyzer.common.Pair;
import sourceanalyzer.common.Report;
import sourceanalyzer.strategy.AbstractAnalyzerStrategy;
import sourceanalyzer.strategy.executors.tasks.CountFileLinesTask;
import sourceanalyzer.strategy.executors.tasks.ReadFilesTask;

import java.nio.file.Path;
import java.util.*;
import java.util.concurrent.*;
import java.util.function.Function;

public class TaskStrategy extends AbstractAnalyzerStrategy {

    private ExecutorService executor;
    private Future<Set<Path>> readFilesFuture;
    private Set<Future<Pair<String, Integer>>> countLinesFuture = new HashSet<>();

    public TaskStrategy(String path, int intervals, int maxLines, int topFilesNumber, Function<Pair<String, Integer>, Void> fileProcessedHandler) {
        super(path, intervals, maxLines, topFilesNumber, fileProcessedHandler);
    }

    protected void setExecutor(ExecutorService executor) {
        this.executor = executor;
    }

    @Override
    public Report makeReport() {
        this.setExecutor(Executors.newSingleThreadExecutor());
        this.readFiles();
        this.setExecutor(Executors.newCachedThreadPool());
        this.countLines();
        return createReport();
    }

    @Override
    public void startAnalyzing() {
        setExecutor(Executors.newSingleThreadExecutor());
        this.readFiles();
        setExecutor(Executors.newCachedThreadPool());
        this.countLinesIncrementally();
    }

    protected void countLinesIncrementally() {
        for (Path file : getFiles()) {
            CompletableFuture<Pair<String, Integer>> completableFuture = new CompletableFuture<>();
            completableFuture.whenComplete((fileComputed, throwable) -> getFileProcessedHandler().apply(fileComputed));
            executor.submit(() -> completableFuture.complete(new CountFileLinesTask(file).call()));
        }
        try {
            shutdownExecutor();
        } catch (InterruptedException e) {
            throw new RuntimeException(e);
        }
    }

    @Override
    public void stopAnalyzing() {
        this.executor.shutdownNow();
        this.readFilesFuture.cancel(true);
        for (Future<Pair<String, Integer>> task : this.countLinesFuture) {
            task.cancel(true);
        }
        try {
            this.shutdownExecutor();
        } catch (InterruptedException e) {
            throw new RuntimeException(e);
        }
    }


    protected void readFiles() {
        this.readFilesFuture = executor.submit(new ReadFilesTask(getPath()));
        try {
            setFiles(readFilesFuture.get());
            shutdownExecutor();
        } catch (InterruptedException | ExecutionException e) {
            throw new RuntimeException(e);
        }
    }

    protected void countLines() {
        this.countLinesFuture = new HashSet<>();
        for (Path file : getFiles()) {
            countLinesFuture.add(executor.submit(new CountFileLinesTask(file)));
        }
        try {
            shutdownExecutor();
            for (Future<Pair<String, Integer>> future : countLinesFuture) {
                Pair<String, Integer> pair = future.get();
                getProcessedFiles().add(pair);
            }
        } catch (InterruptedException | ExecutionException e) {
            throw new RuntimeException(e);
        }
    }

    protected Report createReport() {
        List<Pair<String, Integer>> longestFiles = getProcessedFiles().stream()
                .sorted((o1, o2) -> o2.getSecond().compareTo(o1.getSecond()))
                .limit(getTopFilesNumber()).toList();
        Map<String, Integer> distributions = new HashMap<>();
        for (int i = 0; i < getIntervals(); i++) {
            distributions.put("Interval " + (i + 1), 0);
        }
        int linesPerInterval = getMaxLines() / (getIntervals() - 1);
        for (var file : this.getProcessedFiles()) {
            int index = file.getSecond() / linesPerInterval;
            if (index >= getIntervals()) {
                index = getIntervals() - 1;
            }
            String key = "Interval " + (index + 1);
            distributions.put(key, distributions.get(key) + 1);
        }
        return new Report(longestFiles, distributions);
    }

    protected void shutdownExecutor() throws InterruptedException {
        this.executor.shutdown();
        boolean isTerminatedCorrectly = this.executor.awaitTermination(30, TimeUnit.SECONDS);
        if (!isTerminatedCorrectly) {
            throw new RuntimeException("Executor terminated with timeout");
        }
    }

}
