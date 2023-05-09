package client.view;

public enum Event {

    START("START"),
    STOP("STOP"),
    RESET("RESET"),
    OPEN_FILE_DIALOG("OPEN_FILE_DIALOG"),
    SET_APPROACH("SET_APPROACH");

    private final String command;

    Event(String command) {
        this.command = command;
    }

    public String getCommand() {
        return this.command;
    }
}
