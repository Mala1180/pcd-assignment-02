package executors.tasks;

import common.Pair;

import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Path;
import java.util.concurrent.Callable;
import java.util.stream.Stream;

public class CountFileLinesTask implements Callable<Pair<String, Integer>> {
    private final Path file;

    public CountFileLinesTask(Path file) {
        this.file = file;
    }

    public Pair<String, Integer> call() {
        try {
            try (Stream<String> lines = Files.lines(file)) {
                int countedLines = (int) lines.count();
                return new Pair<>(file.getFileName().toString(), countedLines);
            }
        } catch (IOException e) {
            throw new RuntimeException(e);
        }
    }

}
