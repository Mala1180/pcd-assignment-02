package app;

import app.model.Model;
import common.Chronometer;
import common.Report;
import executors.SourceAnalyzer;

public class TaskApproach {

    public static void main(String[] args) {
        Chronometer chronometer = new Chronometer();
        chronometer.start();
        Report report = SourceAnalyzer.getInstance().getReport("src/main/java", 5, 1000, Model.TOP_FILES_NUMBER);
        chronometer.stop();
        System.out.println("Time elapsed: " + chronometer.getTime() + " ms");
        System.out.println("Longest files: " + report.getLongestFiles());
        System.out.println("Distributions: " + report.getDistributions());
    }

}

