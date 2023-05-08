package executors.tasks;

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
        if (this.directoryPath == null) {
            throw new IllegalArgumentException("Directory path cannot be null");
        }
        Set<Path> files;
        try (Stream<Path> stream = Files.find(Paths.get(this.directoryPath), Integer.MAX_VALUE, (filePath, fileAttr) -> fileAttr.isRegularFile())) {
            files = stream.filter(file -> file.toString().endsWith(".java")).collect(toSet());
        } catch (IOException e) {
            throw new RuntimeException(e);
        }
        return files;
    }

}
