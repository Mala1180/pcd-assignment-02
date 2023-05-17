package sourceanalyzer.strategy;

import sourceanalyzer.common.Pair;
import sourceanalyzer.common.Report;

import java.nio.file.Path;
import java.util.*;
import java.util.function.Function;

public abstract class AbstractAnalyzerStrategy implements AnalyzerStrategy {
    private Set<Path> files = new HashSet<>();
    private Set<Pair<String, Integer>> processedFiles = new HashSet<>();
    private String path;
    private int intervals;
    private int maxLines;
    private final int topFilesNumber;
    private final Function<Pair<String, Integer>, Void> fileProcessedHandler;


    public AbstractAnalyzerStrategy(String path, int intervals, int maxLines, int topFilesNumber, Function<Pair<String, Integer>, Void> fileProcessedHandler) {
        this.path = path;
        this.intervals = intervals;
        this.maxLines = maxLines;
        this.topFilesNumber = topFilesNumber;
        this.fileProcessedHandler = fileProcessedHandler;
    }

    public Set<Path> getFiles() {
        return files;
    }

    public void setFiles(Set<Path> files) {
        this.files = files;
    }

    public Set<Pair<String, Integer>> getProcessedFiles() {
        return processedFiles;
    }

    public void setProcessedFiles(Set<Pair<String, Integer>> processedFiles) {
        this.processedFiles = processedFiles;
    }

    public String getPath() {
        return path;
    }

    public void setPath(String path) {
        this.path = path;
    }

    public int getIntervals() {
        return intervals;
    }

    public void setIntervals(int intervals) {
        this.intervals = intervals;
    }

    public int getMaxLines() {
        return maxLines;
    }

    public void setMaxLines(int maxLines) {
        this.maxLines = maxLines;
    }

    public int getTopFilesNumber() {
        return topFilesNumber;
    }

    public Function<Pair<String, Integer>, Void> getFileProcessedHandler() {
        return fileProcessedHandler;
    }


    protected Report createReport() {
        List<Pair<String, Integer>> longestFiles = getProcessedFiles().stream()
                .sorted((o1, o2) -> o2.getSecond().compareTo(o1.getSecond()))
                .limit(getTopFilesNumber()).toList();
        Map<String, Integer> distributions = new HashMap<>();
        for (int i = 0; i < getIntervals(); i++) {
            distributions.put("Interval " + (i + 1), 0);
        }
        int linesPerInterval = getMaxLines() / (getIntervals() - 1);
        for (var file : this.getProcessedFiles()) {
            int index = file.getSecond() / linesPerInterval;
            if (index >= getIntervals()) {
                index = getIntervals() - 1;
            }
            String key = "Interval " + (index + 1);
            distributions.put(key, distributions.get(key) + 1);
        }
        return new Report(longestFiles, distributions);
    }
}
