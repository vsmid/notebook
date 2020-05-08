package hr.yeti.notebook.store;

import java.io.IOException;
import java.util.List;

public class StoreReader extends Reader<StoreRecord> {

    public StoreReader(RAFConnection conn) {
        super(conn);
    }

    @Override
    public List<StoreRecord> read(String query, char mode) throws IOException {
        throw new UnsupportedOperationException("Not supported yet.");
    }

    @Override
    public StoreRecord read(long id) throws IOException {
        conn.getRaf().seek(id);
        boolean deleted = conn.getRaf().readBoolean();
        long timestamp = conn.getRaf().readLong();
        String title = conn.getRaf().readUTF();
        String value = conn.getRaf().readUTF();
        return new StoreRecord(id, deleted, timestamp, title, value);
    }

}
