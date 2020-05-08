package hr.yeti.notebook.cli.command;

import static hr.yeti.notebook.Nb.INDEX;
import static hr.yeti.notebook.Nb.STORE;
import hr.yeti.notebook.cli.Color;
import hr.yeti.notebook.cli.Command;
import hr.yeti.notebook.cli.Option;
import hr.yeti.notebook.cli.TerminalCommand;
import hr.yeti.notebook.index.Index;
import hr.yeti.notebook.index.Index.SearchMode;
import hr.yeti.notebook.index.Tokenizer;
import hr.yeti.notebook.store.RAFConnection;
import hr.yeti.notebook.store.StoreReader;
import hr.yeti.notebook.store.StoreRecord;
import java.io.IOException;
import static java.lang.System.err;
import static java.lang.System.exit;
import static java.lang.System.out;
import java.net.URL;
import java.time.Instant;
import java.util.ArrayList;
import java.util.HashSet;
import java.util.List;
import java.util.Map;
import java.util.Set;
import java.util.stream.Collectors;
import java.util.stream.IntStream;

public class Find extends Command {

    public Find(Map<String, String> userOptions) {
        super(userOptions);
    }

    @Override
    public String usage() {
        return "Find records";
    }

    @Override
    public Map<String, Option> options() {
        return Map.of(
            "t", new Option("t", "Search by title", true),
            "s", new Option("s", "Show expanded summary", false),
            "c", new Option("c", "Copy to clipboard", false),
            "o", new Option("o", "Try to open URL in browser (for valid link values only)", false),
            "r", new Option("r", "Enable search using regular expression. Search query must be enclosed in quotes.", false),
            "d", new Option("d", "Delete record", false),
            "l", new Option("l", "Limit record count per page", false)
        );
    }

    @Override
    public void execute() throws Exception {
        try ( RAFConnection storeConn = RAFConnection.getConnection(STORE)) {
            List<Long> recordIds = new ArrayList();

            Set<String> tokens = getSearchTokens();

            Index.SearchMode mode = getSearchMode();

            for (String token : tokens) {
                Set<Long> ids = INDEX.find(token, mode);
                recordIds.addAll(ids);
            }

            if (recordIds.isEmpty()) {
                out.println("\nNo records found\n");
                return;
            }

            int total = recordIds.size();
            int limit = Integer.valueOf(o("l", "5"));
            int pages = (int) Math.ceil((double) total / limit);

            int page = 1;

            if (limit == 0) {
                out.println("\nLimit can't be 0\n");
                return;
            }

            if (recordIds.size() > limit) {
                page = Integer.valueOf(o("p", "1"));
                if (page > pages) {
                    out.println("\nNo such page (max is " + pages + ") \n");
                    return;
                }
                int last = page * limit > total ? total : page * limit;
                recordIds = recordIds.subList((page - 1) * limit, last);
            }

            if (userOptions.containsKey("d")) {
                Long recordId = getSelectedRecordId(recordIds, "d", limit);
                deleteRecord(recordId, mode);
                return;

            }

            if (userOptions.containsKey("o")) {
                Long recordId = getSelectedRecordId(recordIds, "o", limit);
                StoreRecord record = getSelectedRecord(recordId, "o");
                if (record != null) {
                    TerminalCommand open = new TerminalCommand("open", new URL(record.getValue()).toString());
                    open.execute();
                    return;
                }
            }

            if (userOptions.containsKey("s")) {
                Long recordId = getSelectedRecordId(recordIds, "s", limit);
                StoreRecord record = getSelectedRecord(recordId, "s");
                if (record != null) {
                    out.println("\n" + record.getValue() + "\n");
                    return;
                }
            }

            if (userOptions.containsKey("c")) {
                Long recordId = getSelectedRecordId(recordIds, "c", limit);
                StoreRecord record = getSelectedRecord(recordId, "c");
                if (record != null) {
                    new TerminalCommand("sh", "-c", ("echo '" + record.getValue() + "\\c' | pbcopy")).execute();
                    out.println("\n+1 copied to clipboard\n");
                    return;
                }
            }

            printResults(storeConn, recordIds, total, page, pages);
        }
    }

    private void printResults(RAFConnection storeConn, List<Long> recordIds, int total, int page, int pages) throws IOException, InterruptedException {
        new TerminalCommand("clear").execute();

        out.printf("Showing: " + "%d/%d\n", recordIds.size(), total);
        out.println();

        StoreReader storeReader = new StoreReader(storeConn);

        int index = 1;

        for (Long recordId : recordIds) {
            StoreRecord storeRecord = storeReader.read(recordId);
            if (!storeRecord.isDeleted()) {
                out.print(Color.YELLOW);
                out.println("title: " + storeRecord.getTitle());
                out.print(Color.RESET);
                out.println("id: " + index++);
                out.println("date: " + Instant.ofEpochMilli(storeRecord.getTimestamp()).toString());
                out.println("summary: " + getSummary(storeRecord.getValue()));
                out.println("");
            }
        }

        if (pages > 1) {
            out.printf("< %s >", getPagination(page, pages));
            out.println("\n");
        }

    }

    private void deleteRecord(Long recordId, SearchMode mode) throws Exception {
        StoreRecord record = getSelectedRecord(recordId, "d");
        Set<String> tokens = Tokenizer.tokenize(record.getTitle());
        tokens.forEach(token -> INDEX.delete(token, mode, recordId));
        INDEX.save();
        out.println("\n+1 record deleted\n");
    }

    private String getSummary(String text) {
        String summary = text.length() > 20 ? text.substring(0, 20) + "..." : text;
        summary = summary.replaceAll(System.lineSeparator(), " ");
        return summary;
    }

    private SearchMode getSearchMode() {
        return userOptions.containsKey("r") ? SearchMode.REGEX : SearchMode.EXACT_MATCH;
    }

    private Set<String> getSearchTokens() {
        Set<String> tokens = new HashSet<>();
        if (userOptions.containsKey("r")) {
            tokens.add(o("t"));
        } else {
            tokens.addAll(Tokenizer.tokenize(o("t")));
        }
        return tokens;
    }

    private StoreRecord getSelectedRecord(Long recordId, String flag) throws Exception {
        if (userOptions.containsKey(flag)) {
            try ( RAFConnection storeConn = RAFConnection.getConnection(STORE)) {
                StoreReader storeReader = new StoreReader(storeConn);
                return storeReader.read(recordId);
            }
        }
        return null;
    }

    private long getSelectedRecordId(List<Long> records, String flag, int limit) {
        int id = 0;
        if (records.size() == 1) {
            id = 1;
        } else if (o(flag) != null) {
            id = Integer.valueOf(o(flag));
        }
        if (id > records.size() || id > limit) {
            err.println("\nInvalid id\n");
            exit(-1);
        }
        return records.get(id - 1);
    }

    private String getPagination(final int page, final int pages) {
        String pagination = null;
        if (pages > 1) {
            int offset = 4;

            int start = page - offset;
            int end = page + offset;

            if (start < 1) {
                start = 1;
                end = start + 2 * offset;
            }

            if (end > pages) {
                end = pages;
                start = end - 2 * offset;
                if (start < 1) {
                    start = 1;
                }
            }

            pagination = IntStream.rangeClosed(start, end)
                .mapToObj(p -> p == page ? "[" + p + "]" : String.valueOf(p))
                .collect(Collectors.joining(" "));

            if (pages > end) {
                pagination += "..." + pages;
            }
        }
        return pagination;
    }
}
