package hr.yeti.notebook.cli.command;

import static hr.yeti.notebook.Nb.NB_DIR;
import static hr.yeti.notebook.Nb.STORE;
import hr.yeti.notebook.cli.Command;
import hr.yeti.notebook.cli.Option;
import static java.lang.System.out;
import java.nio.file.Files;
import java.nio.file.attribute.BasicFileAttributes;
import java.util.Map;

public class Info extends Command {

    public Info(Map<String, String> userOptions) {
        super(userOptions);
    }

    @Override
    public String usage() {
        return "Prints notebook system info";
    }

    @Override
    public Map<String, Option> options() {
        return Map.of();
    }

    @Override
    public void execute() throws Exception {
        out.println();
        out.println("Home directory: " + NB_DIR.toAbsolutePath().toString());
        BasicFileAttributes storeFileAttrs = Files.readAttributes(STORE, BasicFileAttributes.class);
        out.println("Created: " + storeFileAttrs.creationTime().toString());
        out.println("Size: " + storeFileAttrs.size() / 1_024 + "kb");
        out.println();
    }

}
