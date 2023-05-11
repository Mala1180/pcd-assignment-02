package sourceanalyzer.strategy.executors.tasks;

import sourceanalyzer.common.Utils;

import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.util.Set;
import java.util.concurrent.Callable;
import java.util.stream.Stream;

import static java.util.stream.Collectors.toSet;

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
