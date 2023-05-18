package app.gui.model;

import app.utils.Approach;
import app.gui.view.ModelObserver;
import sourceanalyzer.common.ModelData;

import java.util.*;

public class Model {

    public static final int TOP_FILES_NUMBER = 5;
    private final List<ModelObserver> observers = new ArrayList<>();
    private Approach approach;
    private String directoryPath;
    private Integer intervals;
    private Integer maxLines;

    private ModelData modelData;
    private Monitor monitor;
    private VirtualMonitor virtualMonitor;


    public void setParameters(Approach approach, String directoryPath, Integer intervals, Integer maxLines) {
        this.modelData = new ModelData(intervals);
        this.approach = approach;
        this.directoryPath = directoryPath;
        this.intervals = intervals;
        this.maxLines = maxLines;
        this.monitor = new Monitor(modelData);
        this.virtualMonitor = new VirtualMonitor(modelData);
    }

    public void updateData(String fileName, Integer lines) {
        switch (approach) {
            case ACTORS, EVENTS, REACTIVE -> this.modelData.updateDistributions(fileName, lines, intervals, maxLines);
            case TASK -> this.monitor.updateDistributions(fileName, lines, intervals, maxLines);
            case VIRTUAL_THREAD -> this.virtualMonitor.updateDistributions(fileName, lines, intervals, maxLines);
            default -> throw new IllegalStateException("Unexpected value: " + approach);
        }
        notifyObservers();
    }

    public Approach getApproach() {
        return approach;
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
        switch (approach) {
            case TASK -> {
                return this.monitor.getDistributions();
            }
            case VIRTUAL_THREAD -> {
                return this.virtualMonitor.getDistributions();
            }
            case ACTORS, EVENTS, REACTIVE -> {
                return this.modelData.getDistributions();
            }
            default -> throw new IllegalStateException("Unexpected value: " + approach);
        }
    }

    public Map<String, Integer> getTopFiles() {
        switch (approach) {
            case TASK -> {
                return this.monitor.getTopFiles();
            }
            case VIRTUAL_THREAD -> {
                return this.virtualMonitor.getTopFiles();
            }
            case ACTORS, EVENTS, REACTIVE -> {
                return this.modelData.getTopFiles();
            }
            default -> throw new IllegalStateException("Unexpected value: " + approach);
        }
    }

}
