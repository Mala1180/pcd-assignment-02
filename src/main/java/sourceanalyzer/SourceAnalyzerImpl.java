package sourceanalyzer;

import app.gui.utils.Approach;
import sourceanalyzer.common.Pair;
import sourceanalyzer.common.Report;
import sourceanalyzer.strategy.AnalyzerStrategy;
import sourceanalyzer.strategy.events.actors.ActorStrategy;
import sourceanalyzer.strategy.events.futures.EventStrategy;
import sourceanalyzer.strategy.tasks.TaskStrategy;
import sourceanalyzer.strategy.reactive.ReactiveStrategy;
import sourceanalyzer.strategy.virtualthreads.VirtualThreadStrategy;

import java.util.function.Function;

public class SourceAnalyzerImpl implements SourceAnalyzer {

    private static SourceAnalyzerImpl instance;
    private AnalyzerStrategy analyzerStrategy;

    private SourceAnalyzerImpl() {
    }

    public static SourceAnalyzerImpl getInstance() {
        synchronized (SourceAnalyzerImpl.class) {
            if (instance == null) {
                instance = new SourceAnalyzerImpl();
            }
        }
        return instance;
    }

    private void initializeFields(Approach approach, String path, int intervals, int maxLines,
                                  int topFilesNumber, Function<Pair<String, Integer>, Void> fileProcessedHandler) {
        switch (approach) {
            case TASK ->
                    analyzerStrategy = new TaskStrategy(path, intervals, maxLines, topFilesNumber, fileProcessedHandler);
            case VIRTUAL_THREAD -> analyzerStrategy = new VirtualThreadStrategy(path, intervals, maxLines, topFilesNumber, fileProcessedHandler);
            case ACTORS -> analyzerStrategy = new ActorStrategy(path, intervals, maxLines, topFilesNumber, fileProcessedHandler);
            case EVENTS -> analyzerStrategy = new EventStrategy(path, intervals, maxLines, topFilesNumber, fileProcessedHandler);
            case REACTIVE -> analyzerStrategy = new ReactiveStrategy(path, intervals, maxLines, topFilesNumber, fileProcessedHandler);
        }
    }

    @Override
    public Report getReport(Approach approach, String path, int intervals, int maxLines,
                            int topFilesNumber) {
        initializeFields(approach, path, intervals, maxLines, topFilesNumber, (file) -> null);
        return analyzerStrategy.makeReport();
    }

    @Override
    public void analyzeSources(Approach approach, String path, int intervals, int maxLines,
                               int topFilesNumber, Function<Pair<String, Integer>, Void> fileProcessedHandler) {
        initializeFields(approach, path, intervals, maxLines, topFilesNumber, fileProcessedHandler);
        analyzerStrategy.startAnalyzing();
    }

    @Override
    public void stopAnalyzing() {
        analyzerStrategy.stopAnalyzing();
    }

}
