package sourceanalyzer.strategy.events;

import io.vertx.core.Vertx;
import sourceanalyzer.common.Pair;
import sourceanalyzer.common.Report;
import sourceanalyzer.strategy.AbstractAnalyzerStrategy;
import sourceanalyzer.strategy.AnalyzerStrategy;

import java.util.function.Function;

public class EventLoopStrategy extends AbstractAnalyzerStrategy {

    private final Vertx vertx;

    public EventLoopStrategy(String path, int intervals, int maxLines, int topFilesNumber,
                             Function<Pair<String, Integer>, Void> fileProcessedHandler) {
        super(path, intervals, maxLines, topFilesNumber, fileProcessedHandler);
        vertx = Vertx.vertx();
    }

    @Override
    public Report makeReport() {
        return null;
    }

    @Override
    public void startAnalyzing() {
        vertx.deployVerticle(new ReadFileVerticle(super.getPath()));
        // quando ho letto i file, approccio il conteggio delle linee
        // per ogni file, faccio partire un verticle, il quale dentro
    }

    @Override
    public void stopAnalyzing() {

    }

    @Override
    public void resumeAnalyzing() {

    }
}
