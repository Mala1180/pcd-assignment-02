package app.utils;

public enum PathTest {

    TEST2000(System.getProperty("user.home") + "/Downloads/sources2000"),
    TEST8000(System.getProperty("user.home") + "/Downloads/sources8000");

    private final String path;

    PathTest(String path) {
        this.path = path;
    }

    public String getPath() {
        return path;
    }
}
