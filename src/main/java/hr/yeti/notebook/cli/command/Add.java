package hr.yeti.notebook.cli.command;

import static hr.yeti.notebook.Nb.INDEX;
import static hr.yeti.notebook.Nb.STORE;
import hr.yeti.notebook.cli.Command;
import hr.yeti.notebook.cli.Option;
import hr.yeti.notebook.index.Tokenizer;
import hr.yeti.notebook.store.RAFConnection;
import hr.yeti.notebook.store.StoreRecord;
import hr.yeti.notebook.store.StoreWriter;
import static java.lang.System.out;
import java.time.Instant;
import java.util.Map;
import java.util.Set;

public class Add extends Command {

    public Add(Map<String, String> userOptions) {
        super(userOptions);
    }

    @Override
    public String usage() {
        return "Add new record";
    }

    @Override
    public Map<String, Option> options() {
        return Map.of(
            "t", new Option("t", "title", true),
            "v", new Option("v", "value", true)
        );
    }

    @Override
    public void execute() throws Exception {
        Set<String> tokens = Tokenizer.tokenize(o("t"));
        if (tokens.size() > 0) {
            try ( RAFConnection storeConn = RAFConnection.getConnection(STORE)) {
                StoreRecord valueRecord = new StoreRecord(false, Instant.now().toEpochMilli(), o("t"), o("v"));
                long id = new StoreWriter(storeConn).write(valueRecord);
                tokens.forEach(token -> INDEX.add(token, id));
                INDEX.save();
                out.println("\n+1 record added\n");
            }
        }
    }

}
