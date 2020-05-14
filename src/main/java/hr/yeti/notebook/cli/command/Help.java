package hr.yeti.notebook.cli.command;

import static hr.yeti.notebook.Nb.LOGO;
import hr.yeti.notebook.cli.ArgsToCommandParser;
import hr.yeti.notebook.cli.Color;
import hr.yeti.notebook.cli.Command;
import hr.yeti.notebook.cli.Option;
import java.io.IOException;
import static java.lang.System.out;
import java.util.List;
import java.util.Map;
import java.util.stream.Collectors;

public class Help extends Command {

    private ArgsToCommandParser argsParser = new ArgsToCommandParser();

    public Help(Map<String, String> options) {
        super(options);
    }

    @Override
    public String usage() {
        return "List all available commands and usage";
    }

    @Override
    public Map<String, Option> options() {
        return Map.of();
    }

    @Override
    public void execute() throws Exception {
        out.println(LOGO);
        out.println("\nSimple notebook terminal app with some nice quirks :-)\n");

        List<Command> commands = getCommands();

        commands.forEach(command -> {
            out.println(Color.YELLOW + command.commandName() + Color.RESET + " " + command.usage());
            command.options().entrySet()
                .forEach((e) -> {
                    out.println(" -" + e.getKey() + " " + e.getValue().getDescription() + " " + String.format("(%s)", e.getValue().isMandatory() ? "mandatory" : "optional"));
                });
        });

        out.println("\nUniversal command options");
        out.println(" -" + Option.HELP.getFlag() + " " + Option.HELP.getDescription());
        out.println(" -" + Option.VERBOSE.getFlag() + " " + Option.VERBOSE.getDescription());
        out.println("\nUsage: command [options...]\n");
    }

    public List<Command> getCommands() throws IOException {
        return List.of(Add.class, Find.class, Help.class, Info.class)
            .stream()
            .map(command -> argsParser.parse(command.getSimpleName()))
            .collect(Collectors.toList());
    }
}
