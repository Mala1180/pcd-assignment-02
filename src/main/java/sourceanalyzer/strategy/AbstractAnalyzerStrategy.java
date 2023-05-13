package sourceanalyzer.strategy;

import sourceanalyzer.common.Pair;

import java.nio.file.Path;
import java.util.HashSet;
import java.util.Set;
import java.util.function.Function;

public abstract class AbstractAnalyzerStrategy implements AnalyzerStrategy {
    private Set<Path> files = new HashSet<>();
    private Set<Pair<String, Integer>> processedFiles = new HashSet<>();
    private String path;
    private int intervals;
    private int maxLines;
    private int topFilesNumber;
    private Function<Pair<String, Integer>, Void> fileProcessedHandler;


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

    public void setTopFilesNumber(int topFilesNumber) {
        this.topFilesNumber = topFilesNumber;
    }

    public Function<Pair<String, Integer>, Void> getFileProcessedHandler() {
        return fileProcessedHandler;
    }

    public void setFileProcessedHandler(Function<Pair<String, Integer>, Void> fileProcessedHandler) {
        this.fileProcessedHandler = fileProcessedHandler;
    }
}
