package app.controller;

import app.model.Model;
import app.utils.Event;
import executors.SourceAnalyser;

import javax.swing.*;
import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.util.ArrayList;
import java.util.HashSet;
import java.util.List;
import java.util.Set;
import java.util.stream.Stream;

import static java.util.stream.Collectors.toSet;

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
        SourceAnalyser sourceAnalyser = SourceAnalyser.getInstance();
        sourceAnalyser.registerFileProcessedHandler((processedFile) -> {
            SwingUtilities.invokeLater(() -> {

            });
            return null;
        });
        sourceAnalyser.analyzeSources(model.getDirectoryPath(), model.getIntervals(), model.getMaxLines());
    }

    private void stopCounting() {
        SourceAnalyser.getInstance().stopAnalyzing();
    }

    public void resumeCounting() {
        SourceAnalyser.getInstance().resumeAnalyzing();
    }

    public void resetCounter() {
        setParameters("", 0, 0);
    }
}