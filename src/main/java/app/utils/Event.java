package app.utils;

public enum Event {

    START("START"),
    STOP("STOP"),
    RESET("RESET"),
    OPEN_FILE_DIALOG("OPEN_FILE_DIALOG");

    private final String command;

    Event(String command) {
        this.command = command;
    }

    public String getCommand() {
        return this.command;
    }
}
