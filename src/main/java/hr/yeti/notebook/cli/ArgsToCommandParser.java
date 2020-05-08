package hr.yeti.notebook.cli;

import hr.yeti.notebook.cli.command.Help;
import static java.lang.System.err;
import java.lang.reflect.InvocationTargetException;
import java.util.HashMap;
import java.util.Map;

public class ArgsToCommandParser {

    public static final int COMMAND_INDEX = 0;

    public Command parse(String... args) {
        String command;
        Map<String, String> options = new HashMap<>();
        if (args.length == 0 || Option.isOption(args[COMMAND_INDEX])) {
            return new Help(Map.of());
        } else {
            command = args[COMMAND_INDEX];
            for (int index = COMMAND_INDEX + 1; index < args.length; index++) {
                if (Option.isOption(args[index])) {
                    String value = null;
                    if (index + 1 < args.length && !Option.isOption(args[index + 1])) {
                        value = args[index + 1];
                    }
                    options.put(args[index].substring(1), value);
                }
            }
        }
        return createCommandInstance(command, options);
    }

    private Command createCommandInstance(String command, Map<String, String> userOptions) {
        try {
            return (Command) Class.forName(Command.getCanonicalClassName(command))
                .getDeclaredConstructor(Map.class)
                .newInstance(userOptions);
        } catch (ClassNotFoundException
            | IllegalAccessException
            | IllegalArgumentException
            | InstantiationException
            | NoSuchMethodException
            | SecurityException
            | InvocationTargetException e) {
            err.println(String.format("Unknown command: %s", command));
            return null;
        }
    }

}
