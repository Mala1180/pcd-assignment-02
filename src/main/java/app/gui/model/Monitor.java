package app.gui.model;

import sourceanalyzer.common.ModelData;

import java.util.HashMap;
import java.util.Map;
import java.util.function.Function;

public class Monitor {

    private final ModelData modelData;
    private boolean available = false;

    public Monitor(ModelData modelData) {
        this.modelData = modelData;
    }

    public synchronized void updateDistributions(String fileName, Integer lines, Integer intervals, Integer maxLines) {
        modelData.updateDistributions(fileName, lines, intervals, maxLines);
        available = true;
        notifyAll();
    }

    public synchronized Map<String, Integer> getDistributions() {
        while (!available) {
            try {
                wait();
            } catch (InterruptedException e) {
                throw new RuntimeException(e);
            }
        }
        return new HashMap<>(modelData.getDistributions());
    }

    public synchronized Map<String, Integer> getTopFiles() {
        return new HashMap<>(modelData.getTopFiles());
    }

}
