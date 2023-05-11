package sourceanalyzer.strategy.events;

import io.vertx.core.AbstractVerticle;
import io.vertx.core.Promise;

import java.nio.file.Path;

public class BrokerVerticle extends AbstractVerticle {

    @Override
    public void start(Promise<Void> startPromise) {
        System.out.println("Broker");
        this.getVertx().eventBus().<Path>consumer("file-read", message -> {
            System.out.println("Count lines of " + message.body().getFileName().toString());
            //this.getVertx().eventBus().publish("file-processed", message.body());
        });
        startPromise.complete();
    }
}
