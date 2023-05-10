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

    public TaskStrategy(String path, int intervals, int maxLines, int topFilesNumber, Function<Pair<String, Integer>, Void> fileProcessedHandler) {
        super(path,  intervals,  maxLines,  topFilesNumber, fileProcessedHandler);
    }

    @Override
    public Report makeReport() {
        this.readFiles();
        this.countLines();
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

    @Override
    public void startAnalyzing() {
        this.readFiles();
        try (ExecutorService executor = Executors.newCachedThreadPool()) {
            for (Path file : getFiles()) {
                CompletableFuture<Pair<String, Integer>> completableFuture = new CompletableFuture<>();
                completableFuture.whenComplete((fileComputed, throwable) -> getFileProcessedHandler().apply(fileComputed));
                executor.submit(() -> completableFuture.complete(new CountFileLinesTask(file).call()));
            }
            shutdownExecutor(executor);
        } catch (Exception e) {
            throw new RuntimeException(e);
        }
    }

    @Override
    public void stopAnalyzing() {
    }

    @Override
    public void resumeAnalyzing() {

    }

    private void readFiles() {
        try (ExecutorService executor = Executors.newSingleThreadExecutor()) {
            Future<Set<Path>> future = executor.submit(new ReadFilesTask(getPath()));
            setFiles(future.get());
            shutdownExecutor(executor);
        } catch (ExecutionException | InterruptedException e) {
            throw new RuntimeException(e);
        }
    }

    private void countLines() {
        try (ExecutorService executor = Executors.newCachedThreadPool()) {
            Set<Future<Pair<String, Integer>>> futures = new HashSet<>();
            for (Path file : getFiles()) {
                futures.add(executor.submit(new CountFileLinesTask(file)));
            }

            ThreadPoolExecutor threadPoolExecutor = (ThreadPoolExecutor) executor;
            threadPoolExecutor.getActiveCount();
            shutdownExecutor(executor);
            for (Future<Pair<String, Integer>> future : futures) {
                Pair<String, Integer> pair = future.get();
                getProcessedFiles().add(pair);
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
