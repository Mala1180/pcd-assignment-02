package sourceanalyzer.strategy.virtualthreads;

import sourceanalyzer.common.Pair;
import sourceanalyzer.common.Report;
import sourceanalyzer.strategy.executors.TaskStrategy;

import java.util.concurrent.*;
import java.util.function.Function;

public class VirtualThreadStrategy extends TaskStrategy {

    public VirtualThreadStrategy(String path, int intervals, int maxLines, int topFilesNumber, Function<Pair<String, Integer>, Void> fileProcessedHandler) {
        super(path, intervals, maxLines, topFilesNumber, fileProcessedHandler);
    }

    @Override
    public void startAnalyzing() {
        setExecutor(Executors.newVirtualThreadPerTaskExecutor());
        this.readFiles();
        setExecutor(Executors.newVirtualThreadPerTaskExecutor());
        this.countLinesIncrementally();
    }

    @Override
    public Report makeReport() {
        this.setExecutor(Executors.newVirtualThreadPerTaskExecutor());
        this.readFiles();
        this.setExecutor(Executors.newVirtualThreadPerTaskExecutor());
        this.countLines();
        return super.createReport();
    }
}
