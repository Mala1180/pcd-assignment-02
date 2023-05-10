package app.gui.utils;

import java.util.Arrays;

public enum Approach {

    TASK("Task approach with executors"),
    VIRTUAL_THREAD("Virtual thread approach with executors"),
    ASYNC("Asynchronous approach"),
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
