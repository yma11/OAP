package com.intel.oap.common.storage.benchmark;

import com.intel.oap.common.storage.stream.ChunkOutputStream;
import com.intel.oap.common.storage.stream.DataStore;
import com.intel.oap.common.storage.stream.PMemManager;
import com.intel.oap.common.unsafe.PersistentMemoryPlatform;
import org.openjdk.jmh.annotations.*;
import org.openjdk.jmh.runner.Runner;
import org.openjdk.jmh.runner.RunnerException;
import org.openjdk.jmh.runner.options.Options;
import org.openjdk.jmh.runner.options.OptionsBuilder;

import java.io.File;
import java.io.IOException;
import java.util.Properties;
import java.util.concurrent.TimeUnit;

@Warmup(iterations = 2, time = 1, timeUnit = TimeUnit.SECONDS)
@Measurement(iterations = 5, time = 1, timeUnit = TimeUnit.SECONDS)
@State(Scope.Benchmark)
public class PMemStreamWriteTest {
    // refering http://hg.openjdk.java.net/code-tools/jmh/file/b6f87aa2a687/jmh-samples/src/main/java/org/openjdk/jmh/samples/JMHSample_06_FixtureLevel.java
    ChunkOutputStream chunkOutputStream;
    byte[] data;
    DataStore dataStore;
    String fileName = "target/test.file";
    @Setup(Level.Trial)
    public void intializeChunkOutputStream() throws IOException{
        Properties p = new Properties();
        p.setProperty("totalSize", "400000000000");
        p.setProperty("chunkSize", "1048576");
        PMemManager pMemManager = new PMemManager(p);
        dataStore = new DataStore(pMemManager);
        File fd = new File("target/tmp");
        if (!fd.exists()) {
            fd.mkdirs();
        }
        PersistentMemoryPlatform.initialize("target/tmp", (long) 400 * 1024 * 1024 * 1024, 0);
        data = new byte[1024*1024];
        int index = 0;
        while(index < data.length) {
            data[index] = 'a';
            index ++;
        }

    }

    @Benchmark
    @BenchmarkMode(Mode.AverageTime)
    @OutputTimeUnit(TimeUnit.NANOSECONDS)
    public void measurePMemStreamWrite() throws IOException {
        chunkOutputStream = new ChunkOutputStream(fileName, dataStore);
        chunkOutputStream.write(data);
        chunkOutputStream.close();
    }

    public static void main(String[] args) throws RunnerException {
        Options opt = new OptionsBuilder()
                .include(PMemStreamWriteTest.class.getSimpleName())
                .build();

        new Runner(opt).run();
    }
}