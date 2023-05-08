package common;

import java.util.List;
import java.util.Map;

public class Report {

    private final List<Pair<String, Integer>> longestFiles;
    private final Map<String, Integer> distributions;

    public Report(List<Pair<String, Integer>> longestFiles, Map<String, Integer> distributions) {
        this.longestFiles = longestFiles;
        this.distributions = distributions;
    }

    public List<Pair<String, Integer>> getLongestFiles() {
        return longestFiles;
    }

    public Map<String, Integer> getDistributions() {
        return distributions;
    }
}
