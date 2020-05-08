package hr.yeti.notebook.store;

import java.io.FileNotFoundException;
import java.io.RandomAccessFile;
import static java.lang.System.err;
import java.nio.file.Path;

public class RAFConnection implements AutoCloseable {

    private RandomAccessFile raf;

    private RAFConnection(Path path) {
        try {
            raf = new RandomAccessFile(path.toFile(), "rw");
        } catch (FileNotFoundException ex) {
            err.println("Could not connect to the store: " + path.toAbsolutePath().toString());
        }
    }

    public static RAFConnection getConnection(Path path) {
        return new RAFConnection(path);
    }

    public RandomAccessFile getRaf() {
        return raf;
    }

    @Override
    public void close() throws Exception {
        raf.close();
    }

}
