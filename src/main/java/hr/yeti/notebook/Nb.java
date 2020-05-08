package hr.yeti.notebook;

import hr.yeti.notebook.cli.ArgsToCommandParser;
import hr.yeti.notebook.cli.Command;
import hr.yeti.notebook.index.Index;
import java.nio.file.Path;
import java.nio.file.Paths;

public class Nb {

    public static final Path USER_HOME = Paths.get(System.getProperty("user.home"));
    public static final Path NB_DIR = USER_HOME.resolve(".notebook");
    public static final Path INDEX_FILE = NB_DIR.resolve("index");
    public static final Path STORE = NB_DIR.resolve("store");
    public static final Index INDEX = new Index(INDEX_FILE);

    public static String LOGO = " __ _   __  ____  ____  ____   __    __  __ _ \n"
        + "(  ( \\ /  \\(_  _)(  __)(  _ \\ /  \\  /  \\(  / )\n"
        + "/    /(  O ) )(   ) _)  ) _ ((  O )(  O ))  ( \n"
        + "\\_)__) \\__/ (__) (____)(____/ \\__/  \\__/(__\\_)";

    public static void main(String[] args) {
        Command command = new ArgsToCommandParser().parse(args);
        if (command != null) {
            command.run();
        }
    }

//    static boolean isAnsiSupported() {
//        return System.getenv().get("TERM") != null;
//    }
//    static String os() {
//        return System.getProperty("os.name");
//    }
}
