package sourceanalyzer.strategy.virtualthreads;

import sourceanalyzer.common.Pair;
import sourceanalyzer.common.Report;
import sourceanalyzer.strategy.tasks.TaskStrategy;

import java.util.concurrent.*;
import java.util.function.Function;

public class VirtualThreadStrategy extends TaskStrategy {

    public VirtualThreadStrategy(String path, int intervals, int maxLines, int topFilesNumber, Function<Pair<String, Integer>, Void> fileProcessedHandler) {
        super(path, intervals, maxLines, topFilesNumber, fileProcessedHandler);
    }

    @Override
    public void startAnalyzing() {
        setExecutor(Executors.newVirtualThreadPerTaskExecutor());
        super.readFiles();
        setExecutor(Executors.newVirtualThreadPerTaskExecutor());
        super.countLinesIncrementally();
    }

    @Override
    public Report makeReport() {
        super.setExecutor(Executors.newVirtualThreadPerTaskExecutor());
        super.readFiles();
        super.setExecutor(Executors.newVirtualThreadPerTaskExecutor());
        super.countLines();
        return super.createReport();
    }
}
