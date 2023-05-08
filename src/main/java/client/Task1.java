package client;

import executors.SourceAnalyser;

public class Task1 {

    public static void main(String[] args) {
        SourceAnalyser.getInstance().getReport("src/main/java");
    }

}

