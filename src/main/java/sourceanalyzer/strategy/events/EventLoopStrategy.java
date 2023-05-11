package sourceanalyzer.strategy.events;

import com.google.gson.Gson;
import io.vertx.core.Vertx;
import io.vertx.core.eventbus.EventBus;
import sourceanalyzer.common.Pair;
import sourceanalyzer.common.Report;
import sourceanalyzer.strategy.AbstractAnalyzerStrategy;
import sourceanalyzer.strategy.events.codec.GenericCodec;

import java.nio.file.Path;
import java.util.function.Function;

public class EventLoopStrategy extends AbstractAnalyzerStrategy {

    private final Vertx vertx;

    public EventLoopStrategy(String path, int intervals, int maxLines, int topFilesNumber,
                             Function<Pair<String, Integer>, Void> fileProcessedHandler) {
        super(path, intervals, maxLines, topFilesNumber, fileProcessedHandler);
        vertx = Vertx.vertx();
    }

    @Override
    public Report makeReport() {
        return null;
    }

    @Override
    public void startAnalyzing() {
        EventBus eventBus = vertx.eventBus();
        //eventBus.registerDefaultCodec(Path.class, new GenericCodec<>(Path.class));
        Gson gson = new Gson();
        eventBus.consumer("file-read", message -> {
            Path file = gson.fromJson((String) message.body(), Path.class);
            System.out.println("Count lines of " + file.getFileName());
            //this.getVertx().eventBus().publish("file-processed", message.body());
        });

        vertx.deployVerticle(new ReadFileVerticle(super.getPath()));

    }

    @Override
    public void stopAnalyzing() {

    }

    @Override
    public void resumeAnalyzing() {

    }
}
