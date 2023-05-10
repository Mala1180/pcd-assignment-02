package app.model;

import java.util.HashMap;
import java.util.Map;

public class Monitor {

    private final Map<String, Integer> distributions = new HashMap<>();
    private final Map<String, Integer> topFiles = new HashMap<>();
    private boolean available = false;

    public Monitor(Integer intervals) {
        for (int i = 1; i < intervals + 1; i++) {
            distributions.put("Interval " + i, 0);
        }
        for (int i = 1; i < Model.TOP_FILES_NUMBER + 1; i++) {
            topFiles.put("File " + i, 0);
        }
    }

    public synchronized void updateDistributions(String fileName, Integer fileLines, Integer intervals, Integer maxLines) {
        int linesPerInterval = maxLines / (intervals - 1);
        int index = fileLines / linesPerInterval;
        if (fileLines > maxLines) {
            index = intervals - 1;
        }
        String key = "Interval " + (index + 1);

        distributions.put(key, distributions.get(key) + 1);
        if (topFiles.size() < Model.TOP_FILES_NUMBER) {
            topFiles.put(fileName, fileLines);
        } else {
            Map.Entry<String, Integer> minEntry = topFiles.entrySet().stream().min(Map.Entry.comparingByValue()).get();
            if (minEntry.getValue() < fileLines) {
                topFiles.put(fileName, fileLines);
                topFiles.remove(minEntry.getKey());
            }
        }
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
        return new HashMap<>(this.distributions);
    }

    public synchronized Map<String, Integer> getTopFiles() {
        return new HashMap<>(this.topFiles);
    }

}
