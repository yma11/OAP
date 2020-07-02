package com.intel.oap.common.storage.stream;

import java.io.FileInputStream;
import java.io.FileNotFoundException;
import java.io.IOException;

public class ChunkInputStream extends FileInputStream {
   protected ChunkReader chunkReader;

    public ChunkInputStream(String name, DataStore dataStore) throws FileNotFoundException {
        super(name);
        this.chunkReader = dataStore.getChunkReader(name.getBytes());
    }

    /**
     * Reads the next byte of data from the input stream. The value byte is
     * returned as an <code>int</code> in the range <code>0</code> to
     * <code>255</code>. If no byte is available because the end of the stream
     * has been reached, the value <code>-1</code> is returned. This method
     * blocks until input data is available, the end of the stream is detected,
     * or an exception is thrown.
     * @return
     * @throws IOException
     */
    public int read() throws IOException {
        return chunkReader.read();
    }

    public int read(byte b[]) throws IOException {
        assert(b.length > 0);
        return chunkReader.read(b);
    }

    public int read(byte b[], int off, int len) throws IOException {
        throw new UnsupportedOperationException("Unsupported operation");
    }

    public long skip(long n) throws IOException {
        return chunkReader.skip(n);
    }

    public int available() throws IOException {
        throw new UnsupportedOperationException("Unsupported operation");
    }

    public void free() throws IOException {
        chunkReader.freeFromPMem();
    }
}
