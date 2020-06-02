package com.intel.oap.common.storage;

import com.intel.oap.common.storage.stream.*;
import com.intel.oap.common.unsafe.PersistentMemoryPlatform;
import org.junit.Assert;
import org.junit.Before;
import org.junit.Test;

import java.io.File;
import java.io.IOException;
import java.util.Properties;

public class ChunkStreamAPITest {

    DataStore dataStore;

    @Before
    public void prepare() {
        File fd = new File("target/tmp/");
        if (!fd.exists()) {
            fd.mkdirs();
        }
    }

    @Test
    public void testFileStreamReadWrite() throws IOException {
        Properties p = new Properties();
        p.put("totalSize", "0");
        p.put("chunkSize", "1024");
        PMemManager pMemManager = new PMemManager(p);
        dataStore = new DataStore(pMemManager);
        String fileName = "target/test.file";
        byte[] data = new byte[]{'a', 'b', 'c'};
        ChunkOutputStream chunkoutputStream = new ChunkOutputStream(fileName, dataStore);
        chunkoutputStream.write(data);
        chunkoutputStream.close();

        ChunkInputStream chunkInputStream = new ChunkInputStream(fileName, dataStore);
        byte[] readData = new byte[3];
        chunkInputStream.read(readData);
        chunkInputStream.close();
        Assert.assertArrayEquals(data, readData);
    }

    @Test
    public void testPMemStreamEmpty() throws IOException {
        Properties p = new Properties();
        p.put("totalSize", "1024");
        p.put("chunkSize", "10");
        PMemManager pMemManager = new PMemManager(p);
        dataStore = new DataStore(pMemManager);
        String fileName = "target/pmem.file";
        ChunkOutputStream chunkOutputStream = new ChunkOutputStream(fileName, dataStore);
        chunkOutputStream.close();
        MetaData metaData = pMemManager.getpMemMetaStore().getMetaFooter(fileName.getBytes());
        assert(metaData.getTotalChunk() == 0);
        assert(metaData.isHasDiskData() == false);
        // delete generated pmem.file
        File file = new File(fileName);
        if (file != null && file.exists()) {
            assert(file.delete());
        }
    }

    @Test
    public void testPMemStreamSingleChunk() throws IOException {
        Properties p = new Properties();
        p.put("totalSize", "1024");
        p.put("chunkSize", "6");
        PMemManager pMemManager = new PMemManager(p);
        dataStore = new DataStore(pMemManager);
        String initialPath = "target/tmp/";
        String fileName = "target/pmem.file";
        byte[] data = new byte[]{'a', 'b', 'c'};
        PersistentMemoryPlatform.initialize(initialPath, 16 * 1024 *1024, 0);
        ChunkOutputStream chunkOutputStream = new ChunkOutputStream(fileName, dataStore);
        chunkOutputStream.write(data);
        chunkOutputStream.close();

        ChunkInputStream chunkInputStream = new ChunkInputStream(fileName, dataStore);
        byte[] readData = new byte[3];
        chunkInputStream.read(readData);
        chunkInputStream.close();
        Assert.assertArrayEquals(data, readData);
        MetaData metaData = pMemManager.getpMemMetaStore().getMetaFooter(fileName.getBytes());
        assert(metaData.getTotalChunk() == 1);
        assert(metaData.isHasDiskData() == false);
        chunkInputStream.free();
    }

    @Test
    public void testPMemStreamSingleChunkNearBoundary() throws IOException {
        Properties p = new Properties();
        p.put("totalSize", "1024");
        p.put("chunkSize", "6");
        PMemManager pMemManager = new PMemManager(p);
        dataStore = new DataStore(pMemManager);
        String initialPath = "target/tmp/";
        String fileName = "target/pmem.file";
        byte[] data = new byte[]{'a', 'b', 'c', 'd', 'e', 'f'};
        PersistentMemoryPlatform.initialize(initialPath, 16 * 1024 *1024, 0);
        ChunkOutputStream chunkOutputStream = new ChunkOutputStream(fileName, dataStore);
        chunkOutputStream.write(data);
        chunkOutputStream.close();

        ChunkInputStream chunkInputStream = new ChunkInputStream(fileName, dataStore);
        byte[] readData = new byte[6];
        chunkInputStream.read(readData);
        chunkInputStream.close();
        Assert.assertArrayEquals(data, readData);
        MetaData metaData = pMemManager.getpMemMetaStore().getMetaFooter(fileName.getBytes());
        assert(metaData.getTotalChunk() == 1);
        assert(metaData.isHasDiskData() == false);
        chunkInputStream.free();
    }

    @Test
    public void testPMemStreamMultiChunks() throws IOException {
        Properties p = new Properties();
        p.put("totalSize", "1024");
        p.put("chunkSize", "6");
        PMemManager pMemManager = new PMemManager(p);
        dataStore = new DataStore(pMemManager);
        String initialPath = "target/tmp/";
        String fileName = "target/pmem.file";
        byte[] data = new byte[]{'a', 'b', 'c', 'd', 'e', 'f', 'g'};
        PersistentMemoryPlatform.initialize(initialPath, 16 * 1024 *1024, 0);
        ChunkOutputStream chunkOutputStream = new ChunkOutputStream(fileName, dataStore);
        chunkOutputStream.write(data);
        chunkOutputStream.close();

        ChunkInputStream chunkInputStream = new ChunkInputStream(fileName, dataStore);
        byte[] readData = new byte[7];
        chunkInputStream.read(readData);
        chunkInputStream.close();
        Assert.assertArrayEquals(data, readData);
        MetaData metaData = pMemManager.getpMemMetaStore().getMetaFooter(fileName.getBytes());
        assert(metaData.getTotalChunk() == 2);
        assert(metaData.isHasDiskData() == false);
        chunkInputStream.free();
    }

    @Test
    public void testPMemStreamMultiChunksNearBoundary() throws IOException {
        Properties p = new Properties();
        p.put("totalSize", "1024");
        p.put("chunkSize", "3");
        PMemManager pMemManager = new PMemManager(p);
        dataStore = new DataStore(pMemManager);
        String initialPath = "target/tmp/";
        String fileName = "target/pmem.file";
        byte[] data = new byte[]{'a', 'b', 'c', 'd', 'e', 'f'};
        PersistentMemoryPlatform.initialize(initialPath, 16 * 1024 *1024, 0);
        ChunkOutputStream chunkOutputStream = new ChunkOutputStream(fileName, dataStore);
        chunkOutputStream.write(data);
        chunkOutputStream.close();

        ChunkInputStream chunkInputStream = new ChunkInputStream(fileName, dataStore);
        byte[] readData = new byte[6];
        chunkInputStream.read(readData);
        chunkInputStream.close();
        Assert.assertArrayEquals(data, readData);
        MetaData metaData = pMemManager.getpMemMetaStore().getMetaFooter(fileName.getBytes());
        assert(metaData.getTotalChunk() == 2);
        assert(metaData.isHasDiskData() == false);
        chunkInputStream.free();
    }
    @Test
    public void testPMemStreamSingleChunkWithFileTriggered() throws IOException {
        Properties p = new Properties();
        p.put("totalSize", "4");
        p.put("chunkSize", "3");
        PMemManager pMemManager = new PMemManager(p);
        dataStore = new DataStore(pMemManager);
        String initialPath = "target/tmp/";
        String fileName = "target/pmem.file";
        byte[] data = new byte[]{'a', 'b', 'c', 'd', 'e', 'f'};
        PersistentMemoryPlatform.initialize(initialPath, 16 * 1024 *1024, 0);
        ChunkOutputStream chunkOutputStream = new ChunkOutputStream(fileName, dataStore);
        chunkOutputStream.write(data);
        chunkOutputStream.close();

        ChunkInputStream chunkInputStream = new ChunkInputStream(fileName, dataStore);
        byte[] readData = new byte[6];
        chunkInputStream.read(readData);
        chunkInputStream.close();
        Assert.assertArrayEquals(data, readData);
        MetaData metaData = pMemManager.getpMemMetaStore().getMetaFooter(fileName.getBytes());
        assert(metaData.getTotalChunk() == 1);
        assert(metaData.isHasDiskData() == true);
        chunkInputStream.free();
    }

    @Test
    public void testPMemStreamMultiChunksWithFileTriggered() throws IOException {
        Properties p = new Properties();
        p.put("totalSize", "4");
        p.put("chunkSize", "2");
        PMemManager pMemManager = new PMemManager(p);
        dataStore = new DataStore(pMemManager);
        String initialPath = "target/tmp/";
        String fileName = "target/pmem.file";
        byte[] data = new byte[]{'a', 'b', 'c', 'd', 'e', 'f'};
        PersistentMemoryPlatform.initialize(initialPath, 16 * 1024 *1024, 0);
        ChunkOutputStream chunkOutputStream = new ChunkOutputStream(fileName, dataStore);
        chunkOutputStream.write(data);
        chunkOutputStream.close();

        ChunkInputStream chunkInputStream = new ChunkInputStream(fileName, dataStore);
        byte[] readData = new byte[6];
        chunkInputStream.read(readData);
        chunkInputStream.close();
        Assert.assertArrayEquals(data, readData);
        MetaData metaData = pMemManager.getpMemMetaStore().getMetaFooter(fileName.getBytes());
        assert(metaData.getTotalChunk() == 2);
        assert(metaData.isHasDiskData() == true);
        chunkInputStream.free();
    }
}