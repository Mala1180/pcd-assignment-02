package common;

public class Chronometer {

    private boolean running;
    private long startTime;

    public Chronometer() {
        running = false;
    }

    public void start() {
        running = true;
        startTime = System.currentTimeMillis();
    }

    public void stop() {
        startTime = getTime();
        running = false;
    }

    public long getTime() {
        if (running) {
            return System.currentTimeMillis() - startTime;
        } else {
            return startTime;
        }
    }
}
