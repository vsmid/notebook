package hr.yeti.notebook.cli;

import java.io.IOException;
import static java.lang.System.err;
import java.nio.charset.StandardCharsets;

public class TerminalCommand {

    public String[] args;

    public TerminalCommand(String... args) {
        this.args = args;
    }

    public String execute() throws IOException, InterruptedException {
        ProcessBuilder processBuilder = new ProcessBuilder(args);
        processBuilder.inheritIO();
        Process process = processBuilder.start();
        process.waitFor();
        String error = new String(process.getErrorStream().readAllBytes(), StandardCharsets.UTF_8);
        if (error.length() > 0) {
            err.println();
        }
        byte[] input = process.getInputStream().readAllBytes();
        return new String(input, StandardCharsets.UTF_8);
    }
}
