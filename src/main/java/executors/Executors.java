package executors;

import common.MyFileTask;

import java.nio.file.Path;
import java.util.Set;
import java.util.concurrent.CompletableFuture;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Future;
import java.util.concurrent.ThreadFactory;

public class Executors {
    public static void main(String[] args) {
        System.out.println("Hello World!");
        //test
        ExecutorService exec = java.util.concurrent.Executors.newSingleThreadExecutor();
        Future<?> cf = exec.submit(new MyFileTask("DIRECTORY"));

    }



}
