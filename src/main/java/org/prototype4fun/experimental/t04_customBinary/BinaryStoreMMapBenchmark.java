package org.prototype4fun.experimental.t04_customBinary;

import org.apache.commons.io.FileUtils;
import org.openjdk.jmh.annotations.*;
import org.openjdk.jmh.runner.Runner;
import org.openjdk.jmh.runner.RunnerException;
import org.openjdk.jmh.runner.options.Options;
import org.openjdk.jmh.runner.options.OptionsBuilder;
import org.prototype4fun.experimental.data.ByteDataSet;
import org.prototype4fun.experimental.data.Store;

import java.io.IOException;
import java.nio.file.Path;
import java.nio.file.Paths;

/**
 * Created by Radek on 17.11.2016.
 */
public class BinaryStoreMMapBenchmark {

    public static final Path PATH_mmap = Paths.get("target/tdata/binary_mmap_"+ Store.DefaultSize+".bin");
    public static final Path PATH_mmap_search = Paths.get("target/tdata/binary_mmap_search_" + Store.DefaultSize+".bin");

    @Benchmark()
    @BenchmarkMode(Mode.Throughput)
    public void loadAndPersist(ByteDataSet set) throws IOException {
        new BinaryStoreMMap(PATH_mmap.toString())
                .open()
                .add(set.data())
                .synchronize()
                .close();
    }


    @State(Scope.Benchmark)
    public static class BinaryStoreDirectHolder  {

        Store store;

        @Setup(Level.Trial)
        public void prepare(ByteDataSet data) throws IOException {
            store = new BinaryStoreMMap(PATH_mmap_search.toString())
                    .open()
                    .add(data.data())
                    .synchronize();
        }
    }

    @Benchmark()
    @BenchmarkMode(Mode.Throughput)
    public void search10PercentOfStorage(BinaryStoreDirectHolder holder, ByteDataSet data) throws IOException {
        for (byte[] item : data.search()) {
            holder.store.get(item);
        }
    }

    public static void main(String[] args) throws RunnerException, IOException {
        FileUtils.deleteQuietly(PATH_mmap.toFile());
        FileUtils.deleteQuietly(PATH_mmap_search.toFile());
        Options opt = new OptionsBuilder()
                .include(BinaryStoreMMapBenchmark.class.getSimpleName())
                .forks(1)
                .warmupIterations(5)
                .measurementIterations(5)
                .build();

        new Runner(opt).run();
    }

}
