package shell;

import java.io.IOException;

public class ShellExecutor {
    private static Process getProcess(String run) throws IOException {
        Runtime runtime = Runtime.getRuntime();
        return runtime.exec(run);
    }

    public static Process getRuntime() throws IOException {
        return getProcess("sh");
    }
}
