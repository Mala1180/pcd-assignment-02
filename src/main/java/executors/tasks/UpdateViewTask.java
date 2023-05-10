package executors.tasks;

import app.model.Model;
import common.Pair;

import java.util.concurrent.Callable;
import java.util.concurrent.ExecutionException;
import java.util.concurrent.Future;

public class UpdateViewTask implements Callable<Future<Pair<String, Integer>>> {

    //    private final Model model;
    private final Future<Pair<String, Integer>> fileFuture;
    private final Callable<Pair<String, Integer>> callback;

    public UpdateViewTask(Future<Pair<String, Integer>> file, Callable<Pair<String, Integer>> callback) {
        this.fileFuture = file;
        this.callback = callback;
//        this.model = model;
    }

    public Future<Pair<String, Integer>> call() {
//        try {
//            Pair<String, Integer> file = this.fileFuture.get();
//            callback.call(file);
//        } catch (Exception e) {
//            throw new RuntimeException(e);
//        }
        return null;
    }

}
