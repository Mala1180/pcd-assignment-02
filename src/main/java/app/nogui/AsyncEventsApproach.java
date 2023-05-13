package app.nogui;

import app.gui.model.Model;
import app.gui.utils.Approach;
import sourceanalyzer.SourceAnalyzerImpl;
import sourceanalyzer.common.Chronometer;
import sourceanalyzer.common.Report;

public class AsyncEventsApproach {

    public static void main(String[] args) {
        Chronometer chronometer = new Chronometer();
        chronometer.start();
        Report report = SourceAnalyzerImpl.getInstance().getReport(Approach.ASYNC_EVENTS, "src/main/java",
                5, 1000, Model.TOP_FILES_NUMBER);
        chronometer.stop();
        System.out.println("Time elapsed: " + chronometer.getTime() + " ms");
        System.out.println("Longest files: " + report.getLongestFiles());
        System.out.println("Distributions: " + report.getDistributions());
    }

}

