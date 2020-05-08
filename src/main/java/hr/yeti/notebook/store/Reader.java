package hr.yeti.notebook.store;

import java.io.IOException;
import java.util.List;

public abstract class Reader<I extends Record> {

    protected RAFConnection conn;

    private Reader() {
    }

    public Reader(RAFConnection conn) {
        this.conn = conn;
    }

    public abstract List<I> read(String query, char mode) throws IOException;

    public abstract I read(long id) throws IOException;
}
