package common;

import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Path;
import java.util.Set;
import java.util.concurrent.Callable;

public class ComputeFileLinesTask implements Callable<Integer> {
    private boolean isStopped;
    private final Path file;

    public ComputeFileLinesTask(Path file) {
        this.file = file;
    }

    public Integer call() {
        int lines = 0;
        try {
            if (isStopped) {
                wait();
            }
            lines += Files.lines(file).count();
            System.out.println("File " + file.getFileName() + " has lines: " + lines);
            //model.updateCounter(file.getFileName().toString(), lines);
        } catch (IOException | InterruptedException e) {
            throw new RuntimeException(e);
        }
        return lines;
    }

    public void setStopped(boolean isStopped) {
        this.isStopped = isStopped;
    }

}
