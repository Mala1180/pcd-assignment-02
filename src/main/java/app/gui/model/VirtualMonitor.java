package app.gui.model;

import sourceanalyzer.common.ModelData;

import java.util.HashMap;
import java.util.Map;
import java.util.concurrent.locks.Lock;
import java.util.concurrent.locks.ReentrantLock;

import static java.util.concurrent.TimeUnit.SECONDS;

public class VirtualMonitor {

    private final ModelData modelData;
    private boolean available = false;
    private final Lock lock;

    public VirtualMonitor(ModelData modelData) {
        this.modelData = modelData;
        this.lock = new ReentrantLock();
    }

    public void updateDistributions(String fileName, Integer lines, Integer intervals, Integer maxLines) {
        this.lock.lock();
        modelData.updateDistributions(fileName, lines, intervals, maxLines);
        available = true;
        this.lock.notifyAll();
        this.lock.unlock();
    }

    public Map<String, Integer> getDistributions() {
        this.lock.lock();
        while (!available) {
            try {
                this.lock.wait();
            } catch (InterruptedException e) {
                throw new RuntimeException(e);
            }
        }
        var distributions = new HashMap<>(modelData.getDistributions());
        this.lock.unlock();
        return distributions;
    }

    public Map<String, Integer> getTopFiles() {
        this.lock.lock();
        var topFiles = new HashMap<>(modelData.getTopFiles());
        this.lock.unlock();
        return topFiles;
    }

}
