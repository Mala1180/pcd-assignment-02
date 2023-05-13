package sourceanalyzer.strategy.events.async;

import com.google.gson.Gson;
import io.vertx.core.AbstractVerticle;
import sourceanalyzer.common.Utils;

import java.nio.file.Paths;

public class AsyncVerticle extends AbstractVerticle {

    private final String file;

    public AsyncVerticle(String file) {
        this.file = file;
    }

    @Override
    public void start() {
        System.out.println("Count lines of " + file);
        Gson gson = new Gson();
        this.getVertx().eventBus().publish("file-processed", gson.toJson(Utils.countLines(Paths.get(file))));
    }

}
