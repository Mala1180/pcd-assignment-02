package sourceanalyzer.strategy.events.actors;

import io.vertx.core.AbstractVerticle;
import sourceanalyzer.common.Utils;

import java.nio.file.Path;
import java.util.Set;

public class ReadFileVerticle extends AbstractVerticle {

    private final String directoryPath;

    public ReadFileVerticle(String directoryPath) {
        this.directoryPath = directoryPath;
    }

    @Override
    public void start() {
        System.out.println("Read files");
        Set<Path> files = Utils.readFiles(this.directoryPath);
        for (Path file : files) {
            System.out.println(file.toFile().getAbsoluteFile());
            this.getVertx().deployVerticle(new CountFileLinesVerticle(file.toFile().getAbsoluteFile().toString()));
        }

    }

}
