package app.gui.utils;

import java.util.Arrays;

public enum Approach {

    TASK("Task approach with executors"),
    VIRTUAL_THREAD("Virtual thread approach with executors"),
    EVENTS("Actor Events approach"),
    ASYNC_EVENTS("Async Events approach"),
    REACTIVE("Reactive approach");

    private final String message;

    Approach(String message) {
        this.message = message;
    }

    public String getMessage() {
        return this.message;
    }

    public static Approach getByMessage(String message) throws Exception {
        return Arrays.stream(Approach.values())
                .filter(v -> v.getMessage().equals(message))
                .findFirst()
                .orElseThrow(() ->
                new Exception(String.format("Unknown Approach.message: '%s'", message)));
    }
}
