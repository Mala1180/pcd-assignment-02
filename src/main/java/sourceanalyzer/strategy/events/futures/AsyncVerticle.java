package sourceanalyzer.strategy.events.futures;

import io.vertx.core.*;
import sourceanalyzer.common.Pair;
import sourceanalyzer.common.Report;
import sourceanalyzer.common.Utils;

import java.nio.file.Path;
import java.util.*;
import java.util.concurrent.CompletableFuture;
import java.util.function.Function;
import java.util.stream.Collectors;

public class AsyncVerticle extends AbstractVerticle {

    private boolean incrementally;
    private final String directoryPath;
    private final Function<Pair<String, Integer>, Void> fileProcessedHandler;
    private Function<Set<Pair<String, Integer>>, Report> reportHandler;
    private final Set<CompletableFuture<Pair<String, Integer>>> completableFutures = new HashSet<>();

    public AsyncVerticle(String directoryPath, Function<Pair<String, Integer>, Void> fileProcessedHandler) {
        this.directoryPath = directoryPath;
        this.fileProcessedHandler = fileProcessedHandler;
    }

    public void setReportHandler(Function<Set<Pair<String, Integer>>, Report> reportHandler) {
        this.reportHandler = reportHandler;
    }

    public void setIncrementally(Boolean incrementally) {
        this.incrementally = incrementally;
    }

    @Override
    public void start() {
        if (this.incrementally) {
            this.countLinesIncrementally(this.readFiles());
        } else {
            this.countLines(this.readFiles());
        }
    }

    @Override
    public void stop() {
        for (CompletableFuture<Pair<String, Integer>> future : completableFutures) {
            System.out.println(future.resultNow());
            System.out.println(future.cancel(true));
            future.cancel(true);
        }
    }

    protected Future<Set<Path>> readFiles() {
        return this.getVertx().executeBlocking(promise -> {
            try {
                Set<Path> files = Utils.readFiles(this.directoryPath);
                promise.complete(files);
            } catch (Exception e) {
                promise.fail("Exception while reading files from directory " + this.directoryPath);
                e.printStackTrace();
            }
        });
    }

    protected void countLines(Future<Set<Path>> filesRead) {
        List<Future> futures = new ArrayList<>();
        filesRead.onComplete((AsyncResult<Set<Path>> files) -> {
            files.result().forEach(file -> {
                Future<Pair<String, Integer>> future = this.getVertx().executeBlocking(promise -> {
                    try {
                        Pair<String, Integer> processedFile = Utils.countLines(file);
                        promise.complete(processedFile);
                    } catch (Exception e) {
                        promise.fail("Exception while counting file line of file " + file.getFileName());
                        e.printStackTrace();
                    }
                });
                futures.add(future);
            });
            CompositeFuture.all(futures).onSuccess((CompositeFuture res) -> reportHandler.apply(new HashSet<>(res.result().list())));
        });
    }

    protected void countLinesIncrementally(Future<Set<Path>> filesRead) {
        filesRead.onComplete((AsyncResult<Set<Path>> files) -> {
            files.result().forEach(file -> {
                CompletableFuture<Pair<String, Integer>> completableFuture = new CompletableFuture<>();
                completableFuture.complete(Utils.countLines(file));
                completableFuture.whenComplete((fileComputed, throwable) -> this.fileProcessedHandler.apply(fileComputed));
                completableFutures.add(completableFuture);
            });
        });
    }
}