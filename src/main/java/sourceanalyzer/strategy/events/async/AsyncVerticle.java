package sourceanalyzer.strategy.events.async;

import io.vertx.core.*;
import sourceanalyzer.common.Pair;
import sourceanalyzer.common.Report;
import sourceanalyzer.common.Utils;

import java.nio.file.Path;
import java.util.*;
import java.util.concurrent.CompletableFuture;
import java.util.function.Function;

public class AsyncVerticle extends AbstractVerticle {

    private final boolean incrementally;
    private final String directoryPath;
    private final Function<Pair<String, Integer>, Void> fileProcessedHandler;
    private final Function<Set<Pair<String, Integer>>, Report> reportHandler;

    public AsyncVerticle(Boolean incrementally, String directoryPath, Function<Pair<String, Integer>, Void> fileProcessedHandler, Function<Set<Pair<String, Integer>>, Report> reportHandler) {
        this.incrementally = incrementally;
        this.directoryPath = directoryPath;
        this.fileProcessedHandler = fileProcessedHandler;
        this.reportHandler = reportHandler;
    }

    @Override
    public void start() {
        if (this.incrementally) {
            this.countLinesIncrementally(this.readFiles());
        } else {
            this.countLines(this.readFiles());
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

    protected void countLines(Future<Set<Path>> fileReaded) {
        List<Future> futures = new ArrayList<>();
        fileReaded.onComplete((AsyncResult<Set<Path>> files) -> {
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

    protected void countLinesIncrementally(Future<Set<Path>> fileReaded) {
        fileReaded.onComplete((AsyncResult<Set<Path>> files) -> {
            files.result().forEach(file -> {
                CompletableFuture<Pair<String, Integer>> completableFuture = new CompletableFuture<>();
                completableFuture.complete(Utils.countLines(file));
                completableFuture.whenComplete((fileComputed, throwable) -> this.fileProcessedHandler.apply(fileComputed));
            });
        });
    }
}