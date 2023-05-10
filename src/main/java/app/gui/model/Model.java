package app.gui.model;

import app.gui.view.ModelObserver;

import java.util.*;

public class Model {

    public static final int TOP_FILES_NUMBER = 5;
    private final List<ModelObserver> observers = new ArrayList<>();
    private String directoryPath;
    private Integer intervals;
    private Integer maxLines;
    private Monitor monitor;

    public void setParameters(String directoryPath, Integer intervals, Integer maxLines) {
        this.directoryPath = directoryPath;
        this.intervals = intervals;
        this.maxLines = maxLines;
        this.monitor = new Monitor(intervals);
    }

    public void updateCounter(String fileName, Integer lines) {
        this.monitor.updateDistributions(fileName, lines, intervals, maxLines);
        notifyObservers();
    }

    public String getDirectoryPath() {
        return this.directoryPath;
    }

    public Integer getIntervals() {
        return intervals;
    }

    public Integer getMaxLines() {
        return maxLines;
    }

    public void addObserver(ModelObserver obs) {
        this.observers.add(obs);
    }

    private void notifyObservers() {
        for (ModelObserver obs : this.observers) {
            obs.modelUpdated(this);
        }
    }

    public Map<String, Integer> getDistributions() {
        return this.monitor.getDistributions();
    }

    public Map<String, Integer> getTopFiles() {
        return this.monitor.getTopFiles();
    }
}
