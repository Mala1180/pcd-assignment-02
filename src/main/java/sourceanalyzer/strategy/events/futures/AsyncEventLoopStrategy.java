package sourceanalyzer.strategy.events.futures;

import io.vertx.core.DeploymentOptions;
import io.vertx.core.Vertx;
import sourceanalyzer.common.Pair;
import sourceanalyzer.common.Report;
import sourceanalyzer.strategy.AbstractAnalyzerStrategy;
import java.util.function.Function;

public class AsyncEventLoopStrategy extends AbstractAnalyzerStrategy {

    private final Vertx vertx;
    private final AsyncVerticle verticle = new AsyncVerticle(super.getPath(), super.getFileProcessedHandler());


    public AsyncEventLoopStrategy(String path, int intervals, int maxLines, int topFilesNumber,
                                  Function<Pair<String, Integer>, Void> fileProcessedHandler) {
        super(path, intervals, maxLines, topFilesNumber, fileProcessedHandler);
        vertx = Vertx.vertx();
    }

    @Override
    public Report makeReport() {
        verticle.setIncrementally(false);
        verticle.setReportHandler((processedFiles) -> {
            super.setProcessedFiles(processedFiles);
            return null;
        });
        vertx.deployVerticle(verticle);
        return super.createReport();
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
