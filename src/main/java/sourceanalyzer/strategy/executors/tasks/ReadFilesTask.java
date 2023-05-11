package sourceanalyzer.strategy.executors.tasks;

import sourceanalyzer.common.Utils;

import java.nio.file.Path;
import java.util.Set;
import java.util.concurrent.Callable;

public class ReadFilesTask implements Callable<Set<Path>> {
    private final String directoryPath;

    public ReadFilesTask(String directoryPath) {
        this.directoryPath = directoryPath;
    }

    @Override
    public Set<Path> call() {
        return Utils.readFiles(this.directoryPath);
    }

}
