package hr.yeti.notebook.cli;

import static java.lang.System.err;
import static java.lang.System.exit;
import static java.lang.System.out;
import java.nio.file.Path;
import java.util.ArrayList;
import java.util.List;
import java.util.Map;

public abstract class Command {

    protected Map<String, String> userOptions;

    private Command() {
    }

    public Command(Map<String, String> userOptions) {
        this.userOptions = userOptions;
    }

    public abstract String usage();

    public abstract Map<String, Option> options();

    public abstract void execute() throws Exception;

    public void run() {
        if (userOptions.containsKey(Option.HELP.getFlag())) {
            out.println("\n" + usage() + "\n");
            out.println("Options");
            options().entrySet().forEach(e -> out.println(
                " -"
                + e.getKey()
                + " "
                + e.getValue().getDescription()
                + " "
                + String.format("(%s)", e.getValue().isMandatory() ? "mandatory" : "optional"))
            );
            out.println("\nUsage: " + commandName() + " [options...]\n");
        } else {
            validatedMandatoryOptions();
            try {
                execute();
            } catch (Exception e) {
                err.println("\nFailed to execute command\n");
                if (userOptions.containsKey(Option.VERBOSE.getFlag())) {
                    err.println(e.getMessage() + "\n");
                }
            }
        }
    }

    protected String o(String key, String def) {
        return userOptions.getOrDefault(key, def);
    }

    protected String o(String key) {
        return Command.this.o(key, null);
    }

    private void validatedMandatoryOptions() {
        List<String> missing = new ArrayList<>();
        options().values().forEach((option) -> {
            if (!userOptions.containsKey(option.getFlag()) || userOptions.get(option.getFlag()) == null) {
                if (option.isMandatory()) {
                    missing.add("-" + option.getFlag());
                }
            }
        });
        if (!missing.isEmpty()) {
            err.println("\nPlease provide the following options: " + String.join(", ", missing) + "\n");
            exit(-1);
        }
    }

    public static String getCanonicalClassName(String command) {
        return "hr.yeti.notebook.cli.command." + (Character.toTitleCase(command.charAt(0)) + command.substring(1));
    }

    public String commandName() {
        return getCommandName(getClass().getSimpleName());
    }

    protected String getCommandName(String clazz) {
        return clazz.toLowerCase().split("\\.")[0];
    }

    protected String getCommandName(Path clazzPath) {
        return getCommandName(clazzPath.getFileName().toString());
    }
}
