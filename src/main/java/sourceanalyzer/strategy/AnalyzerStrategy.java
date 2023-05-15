package sourceanalyzer.strategy;

import sourceanalyzer.common.Report;

public interface AnalyzerStrategy {

    Report makeReport();

    void startAnalyzing();

    void stopAnalyzing();

}
