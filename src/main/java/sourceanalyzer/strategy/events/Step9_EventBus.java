package sourceanalyzer.strategy.events;

import io.vertx.core.AbstractVerticle;
import io.vertx.core.Promise;
import io.vertx.core.Vertx;
import io.vertx.core.eventbus.EventBus;
import io.vertx.core.eventbus.MessageConsumer;
import sourceanalyzer.common.Utils;
import sourceanalyzer.strategy.events.codec.GenericCodec;

import java.nio.file.Path;
import java.util.Set;

class MyAgent1 extends AbstractVerticle {

    public void start(Promise<Void> startPromise) {
        System.out.println("Read files");

        EventBus eventBus = this.getVertx().eventBus();
        MessageConsumer<Path> consumer = eventBus.consumer("file-read");
        consumer.handler(message -> {
            System.out.println("Count lines of " + message.body());
            //this.getVertx().eventBus().publish("file-processed", message.body());
        });
        log("Ready.");
        startPromise.complete();
    }

    private void log(String msg) {
        System.out.println("[REACTIVE AGENT #1][" + Thread.currentThread() + "] " + msg);
    }
}

class MyAgent2 extends AbstractVerticle {

    public void start() {
        log("started.");
        Set<Path> files = Utils.readFiles(System.getProperty("user.dir") + "/src/main/java/sourceanalyzer/strategy/events");
        System.out.println(files);
        for (Path file : files) {
            EventBus eb = this.getVertx().eventBus();
            eb.send("file-read",  file);
        }

    }

    private void log(String msg) {
        System.out.println("[REACTIVE AGENT #2][" + Thread.currentThread() + "] " + msg);
    }
}

public class Step9_EventBus {

    public static void main(String[] args) {
        Vertx vertx = Vertx.vertx();
        vertx.eventBus().registerDefaultCodec(Path.class, new GenericCodec<>(Path.class));
        vertx.deployVerticle(new MyAgent1(), res -> {
            /* deploy the second verticle only when the first has completed */
            vertx.deployVerticle(new MyAgent2());
        });
    }
}

