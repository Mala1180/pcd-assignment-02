package sourceanalyzer.strategy.events.async;

import com.google.gson.Gson;
import com.google.gson.reflect.TypeToken;
import io.vertx.core.Vertx;
import io.vertx.core.eventbus.EventBus;
import sourceanalyzer.common.Pair;
import sourceanalyzer.common.Report;
import sourceanalyzer.strategy.AbstractAnalyzerStrategy;

import java.lang.reflect.Type;
import java.util.function.Function;

public class AsyncEventLoopStrategy extends AbstractAnalyzerStrategy {

    private final Vertx vertx;

    public AsyncEventLoopStrategy(String path, int intervals, int maxLines, int topFilesNumber,
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
        /*EventBus eventBus = vertx.eventBus();
        eventBus.consumer("file-processed", message -> {
            System.out.println("Received: " + message.body());
            Gson gson = new Gson();
            Type pairType = new TypeToken<Pair<String, Integer>>() {}.getType();
            Pair<String, Integer> processedFile = gson.fromJson(message.body().toString(), pairType);
            super.getFileProcessedHandler().apply(processedFile);
        });
        vertx.deployVerticle(new ReadFileVerticle(super.getPath()));
*/
    }

    @Override
    public void stopAnalyzing() {

    }

    @Override
    public void resumeAnalyzing() {

    }
}
