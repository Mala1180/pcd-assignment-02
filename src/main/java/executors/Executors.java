package executors;

import common.MyFileTask;

import java.util.concurrent.ExecutorService;
import java.util.concurrent.ThreadFactory;

public class Executors {
    public static void main(String[] args) {
        System.out.println("Hello World!");
        //test
        ExecutorService exec = java.util.concurrent.Executors.newSingleThreadExecutor();
        exec.execute(new MyFileTask("DIRECTORY"));
    }



}
