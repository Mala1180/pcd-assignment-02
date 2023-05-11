package sourceanalyzer.strategy.events;

import io.vertx.core.AbstractVerticle;
import io.vertx.core.Vertx;
import sourceanalyzer.common.Utils;

import java.nio.file.Path;

public class CountFileLinesVerticle extends AbstractVerticle {

    private final Path file;

    public CountFileLinesVerticle(Path file) {
        this.file = file;
    }

    @Override
    public void start() {
        System.out.println("Count lines of " + file.getFileName().toString());
        this.getVertx().eventBus().publish("file-processed", Utils.countLines(file));
    }

}
