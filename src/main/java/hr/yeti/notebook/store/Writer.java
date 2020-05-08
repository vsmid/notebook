package hr.yeti.notebook.store;

import java.io.IOException;

public abstract class Writer<I extends Record> {

    protected RAFConnection conn;

    private Writer() {
    }

    public Writer(RAFConnection conn) {
        this.conn = conn;
    }

    public abstract long write(I record) throws IOException;

    public abstract void delete(long id) throws IOException;
}
