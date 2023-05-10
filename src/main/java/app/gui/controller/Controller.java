package app.gui.controller;

import app.gui.model.Model;
import app.gui.utils.Event;
import executors.SourceAnalyzer;

import javax.swing.*;

public class Controller {

    private final Model model;

    public Controller(Model model) {
        this.model = model;
    }

    public void setParameters(String path, Integer intervals, Integer maxLines) {
        model.setParameters(path, intervals, maxLines);
    }

    public void processEvent(Event event) {
        try {
            new Thread(() -> {
                try {
                    switch (event) {
                        case START -> startCounting();
                        case STOP -> stopCounting();
                        case RESET -> resetCounter();
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
        SourceAnalyzer sourceAnalyser = SourceAnalyzer.getInstance();
        sourceAnalyser.registerFileProcessedHandler((processedFile) -> {
            SwingUtilities.invokeLater(() -> {

            });
            return null;
        });
        sourceAnalyser.analyzeSources(model.getDirectoryPath(), model.getIntervals(), model.getMaxLines(), Model.TOP_FILES_NUMBER);
    }

    private void stopCounting() {
        SourceAnalyzer.getInstance().stopAnalyzing();
    }

    public void resumeCounting() {
        SourceAnalyzer.getInstance().resumeAnalyzing();
    }

    public void resetCounter() {
        setParameters("", 0, 0);
    }
}