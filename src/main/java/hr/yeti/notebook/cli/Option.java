package hr.yeti.notebook.cli;

public class Option {

    public static final String FORMAT = "\\-[a-zA-Z]+";
    public static final Option HELP = new Option("h", "Help", false);
    public static final Option VERBOSE = new Option("V", "Prints more verbose error message", false);

    private String flag;
    private String value;
    private String description;
    private boolean mandatory;

    public Option(String flag, String description, boolean mandatory) {
        this.flag = flag;
        this.description = description;
        this.mandatory = mandatory;
    }

    static boolean isOption(String arg) {
        return arg.matches(FORMAT);
    }

    public String getFlag() {
        return flag;
    }

    public String getValue() {
        return value;
    }

    public String getDescription() {
        return description;
    }

    public boolean isMandatory() {
        return mandatory;
    }

    public void setValue(String value) {
        this.value = value;
    }

    @Override
    public String toString() {
        return description + "(" + mandatory + ")";
    }

}
