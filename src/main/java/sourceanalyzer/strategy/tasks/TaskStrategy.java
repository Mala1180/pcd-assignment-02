package sourceanalyzer.strategy.tasks;

import sourceanalyzer.common.Pair;
import sourceanalyzer.common.Report;
import sourceanalyzer.strategy.AbstractAnalyzerStrategy;

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
        return super.createReport();
    }

    @Override
    public void startAnalyzing() {
        setExecutor(Executors.newSingleThreadExecutor());
        this.readFiles();
        setExecutor(Executors.newCachedThreadPool());
        this.countLinesIncrementally();
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

    protected void countLines() {
        this.countLinesFuture = new HashSet<>();
        for (Path file : getFiles()) {
            countLinesFuture.add(executor.submit(new CountFileLinesTask(file)));
        }
        try {
            shutdownExecutor();
            for (Future<Pair<String, Integer>> future : countLinesFuture) {
                Pair<String, Integer> pair = future.get();
                super.getProcessedFiles().add(pair);
            }
        } catch (InterruptedException | ExecutionException e) {
            throw new RuntimeException(e);
        }
    }

    protected void shutdownExecutor() throws InterruptedException {
        this.executor.shutdown();
        boolean isTerminatedCorrectly = this.executor.awaitTermination(30, TimeUnit.SECONDS);
        if (!isTerminatedCorrectly) {
            throw new RuntimeException("Executor terminated with timeout");
        }
    }

}
