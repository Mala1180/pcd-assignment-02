package sourceanalyzer.strategy.events;

import com.google.gson.Gson;
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
        Gson gson = new Gson();
        for (Path file : files) {
            System.out.println(gson.toJson(file));
            this.getVertx().eventBus().publish("file-read", gson.toJson(file));
        }

    }

}
