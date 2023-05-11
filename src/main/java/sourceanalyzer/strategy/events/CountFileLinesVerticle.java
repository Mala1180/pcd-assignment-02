package sourceanalyzer.strategy.events;

import io.vertx.core.AbstractVerticle;

import java.nio.file.Path;

public class CountFileLinesVerticle extends AbstractVerticle {

    private final Path file;

    public CountFileLinesVerticle(Path file) {
        this.file = file;
    }

    @Override
    public void start() {
        System.out.println("Count lines of " + file.getFileName().toString());
    }

}
