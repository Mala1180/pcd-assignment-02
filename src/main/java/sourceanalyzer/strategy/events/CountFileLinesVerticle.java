package sourceanalyzer.strategy.events;

import com.google.gson.Gson;
import io.vertx.core.AbstractVerticle;
import io.vertx.core.Vertx;
import sourceanalyzer.common.Utils;

import java.nio.file.Path;
import java.nio.file.Paths;

public class CountFileLinesVerticle extends AbstractVerticle {

    private final String file;

    public CountFileLinesVerticle(String file) {
        this.file = file;
    }

    @Override
    public void start() {
        System.out.println("Count lines of " + file);
        Gson gson = new Gson();
        this.getVertx().eventBus().publish("file-processed", gson.toJson(Utils.countLines(Paths.get(file))));
    }

}
