package sourceanalyzer.strategy.events.async;

import com.google.gson.Gson;
import com.google.gson.reflect.TypeToken;
import io.vertx.core.AbstractVerticle;
import io.vertx.core.Vertx;
import io.vertx.core.eventbus.EventBus;
import sourceanalyzer.common.Pair;
import sourceanalyzer.common.Report;
import sourceanalyzer.strategy.AbstractAnalyzerStrategy;

import java.lang.reflect.Type;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.function.Function;

public class AsyncEventLoopStrategy extends AbstractAnalyzerStrategy {

    private final Vertx vertx;

    public AsyncEventLoopStrategy(String path, int intervals, int maxLines, int topFilesNumber,
                                  Function<Pair<String, Integer>, Void> fileProcessedHandler) {
        super(path, intervals, maxLines, topFilesNumber, fileProcessedHandler);
        vertx = Vertx.vertx();
    }

    @Override
    public Report makeReport() {
        vertx.deployVerticle(new AsyncVerticle(false, super.getPath(), super.getFileProcessedHandler(), (processedFiles) -> {
            super.setProcessedFiles(processedFiles);
            return null;
        }));
        return this.createReport();
    }

    @Override
    public void startAnalyzing() {
        vertx.deployVerticle(new AsyncVerticle(true, super.getPath(), super.getFileProcessedHandler(), (processedFiles) -> null));
    }

    @Override
    public void stopAnalyzing() {

    }

    @Override
    public void resumeAnalyzing() {

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
}
