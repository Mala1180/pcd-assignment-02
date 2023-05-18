package app.nogui;

import app.gui.model.Model;
import app.utils.Approach;
import app.utils.Chronometer;
import app.utils.PathTest;
import sourceanalyzer.common.Report;
import sourceanalyzer.SourceAnalyzerImpl;

public class TaskApproach {

    public static void main(String[] args) {
        Chronometer.start();
        Report report = SourceAnalyzerImpl.getInstance().getReport(Approach.TASK, PathTest.TEST1300.getPath(),
                5, 1000, Model.TOP_FILES_NUMBER);
        Chronometer.stop();
        System.out.println("Time elapsed: " + Chronometer.getTime() + " ms");
        System.out.println("Longest files: " + report.getLongestFiles());
        System.out.println("Distributions: " + report.getDistributions());
    }

}

