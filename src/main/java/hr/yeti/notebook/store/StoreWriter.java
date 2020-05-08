package hr.yeti.notebook.store;

import java.io.IOException;

public class StoreWriter extends Writer<StoreRecord> {

    public StoreWriter(RAFConnection conn) {
        super(conn);
    }

    @Override
    public long write(StoreRecord record) throws IOException {
        long id = conn.getRaf().length();
        conn.getRaf().seek(id);
        conn.getRaf().writeBoolean(record.isDeleted());
        conn.getRaf().writeLong(record.getTimestamp());
        conn.getRaf().writeUTF(record.getTitle());
        conn.getRaf().writeUTF(record.getValue());
        return id;
    }

    @Override
    public void delete(long id) throws IOException {
        conn.getRaf().seek(id);
        conn.getRaf().writeBoolean(true);
    }

}
