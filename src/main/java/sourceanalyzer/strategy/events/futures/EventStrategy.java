package sourceanalyzer.strategy.events.futures;

import io.vertx.core.DeploymentOptions;
import io.vertx.core.Vertx;
import sourceanalyzer.common.Pair;
import sourceanalyzer.common.Report;
import sourceanalyzer.strategy.AbstractAnalyzerStrategy;
import java.util.concurrent.CompletableFuture;
import java.util.concurrent.ExecutionException;
import java.util.function.Function;

public class EventStrategy extends AbstractAnalyzerStrategy {

    private final Vertx vertx;
    private final AsyncVerticle verticle = new AsyncVerticle(super.getPath(), super.getFileProcessedHandler());


    public EventStrategy(String path, int intervals, int maxLines, int topFilesNumber,
                         Function<Pair<String, Integer>, Void> fileProcessedHandler) {
        super(path, intervals, maxLines, topFilesNumber, fileProcessedHandler);
        vertx = Vertx.vertx();
    }

    @Override
    public Report makeReport() {
        CompletableFuture<Report> future = new CompletableFuture<>();
        verticle.setIncrementally(false);
        verticle.setReportHandler((processedFiles) -> {
            super.setProcessedFiles(processedFiles);
            future.complete(super.createReport());
            return null;
        });
        vertx.deployVerticle(verticle);
        try {
            return future.get();
        } catch (InterruptedException | ExecutionException e) {
            throw new RuntimeException(e);
        }
    }

    @Override
    public void startAnalyzing() {
        verticle.setIncrementally(true);
        vertx.deployVerticle(verticle, new DeploymentOptions().setWorkerPoolName("worker-pool"));
    }

    @Override
    public void stopAnalyzing() {
        vertx.undeploy(verticle.deploymentID());
    }

}
