package common;

import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.util.Set;
import java.util.stream.Stream;

import static java.util.stream.Collectors.toSet;

public class MyFileTask implements Runnable {
    String directoryPath;

    public MyFileTask(String directoryPath) {
        this.directoryPath = directoryPath;
    }

    @Override
    public void run() {

    }

    public Set<Path> startCounting() {
        Set<Path> files;
        if (this.directoryPath == null) {
            return null;//throw qualcosa
        }
        try (Stream<Path> stream = Files.find(Paths.get(this.directoryPath), Integer.MAX_VALUE, (filePath, fileAttr) -> fileAttr.isRegularFile())) {
            files = stream.filter(file -> file.toString().endsWith(".java")).collect(toSet());
        } catch (IOException e) {
            throw new RuntimeException(e);
        }

        return files;

/*        int cores = Runtime.getRuntime().availableProcessors() + 1;
        int averageFilesPerThread = files.size() < cores ? 1 : files.size() / cores;

        List<Path> filesPerThread = new ArrayList<>();
        int lastThreadFiles = 0;
        agents = new HashSet<>();
        long startTime = System.currentTimeMillis();

        int counter = 0;

        for (Path file : files) {
            filesPerThread.add(file);

            if ((counter % averageFilesPerThread == 0 && counter < averageFilesPerThread * (cores - 1)) || counter == files.size() - 1) {
                CounterAgent agent = new CounterAgent(model, new HashSet<>(filesPerThread));
                agent.start();
                agents.add(agent);
                lastThreadFiles = filesPerThread.size();
                System.out.println(lastThreadFiles);
                filesPerThread.clear();
            }
            counter++;
        }*/

/*        for (CounterAgent agent : agents) {
            try {
                agent.join();
            } catch (InterruptedException e) {
                throw new RuntimeException(e);
            }
        }

        long stopTime = System.currentTimeMillis();
        System.out.println("Total files: " + files.size());
        System.out.println("Files per core: " + averageFilesPerThread);
        System.out.println("Files last core: " + lastThreadFiles);

        System.out.println("Execution time: " + (stopTime - startTime) + " ms");*/
    }

}
