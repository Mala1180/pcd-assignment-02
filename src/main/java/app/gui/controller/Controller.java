package app.gui.controller;

import app.gui.model.Model;
import app.utils.Approach;
import app.utils.Event;
import sourceanalyzer.SourceAnalyzerImpl;

public class Controller {

    private final Model model;

    public Controller(Model model) {
        this.model = model;
    }

    public void setParameters(Approach approach, String path, Integer intervals, Integer maxLines) {
        model.setParameters(approach, path, intervals, maxLines);
    }

    public void processEvent(Event event) {
        try {
            new Thread(() -> {
                try {
                    switch (event) {
                        case START -> startCounting();
                        case STOP -> stopCounting();
                    }
                } catch (Exception ex) {
                    ex.printStackTrace();
                }
            }).start();
        } catch (Exception ex) {
            ex.printStackTrace();
        }

    }

    public void startCounting() {
        SourceAnalyzerImpl sourceAnalyser = SourceAnalyzerImpl.getInstance();
        sourceAnalyser.analyzeSources(model.getApproach(), model.getDirectoryPath(), model.getIntervals(), model.getMaxLines(),
                Model.TOP_FILES_NUMBER, (processedFile) -> {
                    model.updateData(processedFile.getFirst(), processedFile.getSecond());
                    return null;
                });
    }

    private void stopCounting() {
        SourceAnalyzerImpl.getInstance().stopAnalyzing();
    }

}