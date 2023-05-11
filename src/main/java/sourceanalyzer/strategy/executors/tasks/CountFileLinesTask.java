package sourceanalyzer.strategy.executors.tasks;

import sourceanalyzer.common.Pair;
import sourceanalyzer.common.Utils;

import java.nio.file.Path;
import java.util.concurrent.Callable;

public class CountFileLinesTask implements Callable<Pair<String, Integer>> {
    private final Path file;

    public CountFileLinesTask(Path file) {
        this.file = file;
    }

    @Override
    public Pair<String, Integer> call() {
        return Utils.countLines(file);
    }

}
