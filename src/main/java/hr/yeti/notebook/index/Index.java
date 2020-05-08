package hr.yeti.notebook.index;

import static hr.yeti.notebook.Nb.NB_DIR;
import java.io.FileInputStream;
import java.io.FileOutputStream;
import java.io.IOException;
import static java.lang.System.err;
import static java.lang.System.exit;
import java.nio.file.Files;
import java.nio.file.Path;
import java.util.HashSet;
import java.util.Properties;
import java.util.Set;
import java.util.stream.Collectors;
import java.util.stream.Stream;

public class Index {

    public static enum SearchMode {
        EXACT_MATCH('e'), REGEX('r');

        private final char mode;

        private SearchMode(char mode) {
            this.mode = mode;
        }

        public char getMode() {
            return mode;
        }

        @Override
        public String toString() {
            return Character.toString(mode);
        }
    }

    private Properties props;
    private Path indexPath;

    public Index(Path indexPath) {
        this.indexPath = indexPath;
        this.props = new Properties();
        try {
            if (!Files.exists(NB_DIR)) {
                Files.createDirectories(NB_DIR);
                if (!Files.exists(indexPath)) {
                    Files.createFile(indexPath);
                }
            }
            this.props.load(new FileInputStream(indexPath.toFile()));
        } catch (IOException e) {
            err.println("Failed to load index");
            exit(-1);
        }
    }

    public void save() {
        try {
            this.props.store(new FileOutputStream(indexPath.toFile()), "");
        } catch (IOException ex) {
            err.println("Failed to update index");
            exit(-1);
        }

    }

    public void delete(String token, SearchMode mode, Long recordId) {
        if (mode == SearchMode.EXACT_MATCH) {
            if (props.containsKey(token)) {
                removeRecordId(token, recordId);
            }
        } else if (mode == SearchMode.REGEX) {
            for (Object key : props.keySet()) {
                if (key.toString().matches(token)) {
                    removeRecordId(token, recordId);
                }
            }
        }
    }

    public Set<Long> find(String token, SearchMode mode) {
        Set<Long> results = new HashSet<>();
        if (mode == SearchMode.EXACT_MATCH) {
            if (props.containsKey(token)) {
                results.addAll(extractRecordIds(token));
            }
        } else if (mode == SearchMode.REGEX) {
            for (Object key : props.keySet()) {
                if (key.toString().matches(token)) {
                    results.addAll(extractRecordIds(key.toString()));
                }
            }
        }
        return results;
    }

    public void add(String token, Long recordId) {
        if (props.containsKey(token)) {
            String recordIds = (String) props.get(token);
            recordIds += "," + recordId.toString();
            props.replace(token, recordIds);
        } else {
            props.put(token, recordId.toString());
        }
    }

    private void removeRecordId(String token, Long recordId) {
        String ids = Stream.of(props.get(token).toString().split(","))
            .filter(id -> !id.equals(String.valueOf(recordId)))
            .collect(Collectors.joining(","));
        if (ids.length() > 0) {
            props.put(token, String.join(",", ids));
        } else {
            props.remove(token);
        }
    }

    private Set<Long> extractRecordIds(String token) {
        Set<Long> recordIds = new HashSet<>();
        for (String id : props.get(token).toString().split(",")) {
            try {
                recordIds.add(Long.valueOf(id));
            } catch (NumberFormatException e) {
            }
        }
        return recordIds;
    }
}
