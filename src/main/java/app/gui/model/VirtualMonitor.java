package app.gui.model;

import sourceanalyzer.common.ModelData;

import java.util.HashMap;
import java.util.Map;
import java.util.concurrent.locks.Condition;
import java.util.concurrent.locks.Lock;
import java.util.concurrent.locks.ReentrantLock;

public class VirtualMonitor {

    private final ModelData modelData;
    private final Condition condition;
    private boolean available = false;
    private final Lock lock;

    public VirtualMonitor(ModelData modelData) {
        this.modelData = modelData;
        this.lock = new ReentrantLock();
        this.condition = lock.newCondition();
    }

    public void updateDistributions(String fileName, Integer lines, Integer intervals, Integer maxLines) {
        this.lock.lock();
        modelData.updateDistributions(fileName, lines, intervals, maxLines);
        available = true;
        this.condition.signalAll();
        this.lock.unlock();
    }

    public Map<String, Integer> getDistributions() {
        this.lock.lock();
        while (!available) {
            try {
                this.condition.await();
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
