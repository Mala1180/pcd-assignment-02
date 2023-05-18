package sourceanalyzer;

import app.utils.Approach;
import sourceanalyzer.common.Pair;
import sourceanalyzer.common.Report;

import java.util.function.Function;

public interface SourceAnalyzer {

    Report getReport(Approach approach, String path, int intervals, int maxLines, int topFilesNumber);

    void analyzeSources(Approach approach, String path, int intervals, int maxLines,
                        int topFilesNumber, Function<Pair<String, Integer>, Void> fileProcessedHandler);

    void stopAnalyzing();

}
