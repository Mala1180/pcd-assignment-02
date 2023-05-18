package app.utils;

public final class Chronometer {

    private static long startTime;

    private Chronometer() {
    }

    public static void start() {
        startTime = System.currentTimeMillis();
    }

    public static void stop() {
        startTime = System.currentTimeMillis() - startTime;
    }

    public static long getTime() {
        return startTime;
    }
}
