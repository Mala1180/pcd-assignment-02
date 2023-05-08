package executors;

import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;

public class SourceAnalyser {
    public static void main(String[] args) {
        try (ExecutorService executors = Executors.newSingleThreadExecutor()) {
            executors.execute(() -> {
                System.out.println("Hello World!");
            });
        }

    }
}
