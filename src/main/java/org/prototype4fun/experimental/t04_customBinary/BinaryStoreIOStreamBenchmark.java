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
public class BinaryStoreIOStreamBenchmark {

    public static final Path PATH_io = Paths.get("target/tdata/binary_io_"+ Store.DefaultSize+".bin");
    public static final Path PATH_io_search = Paths.get("target/tdata/binary_io_search_" + Store.DefaultSize+".bin");

    @Benchmark()
    @BenchmarkMode(Mode.Throughput)
    public void loadAndPersist(ByteDataSet set) throws IOException {
        new BinaryStoreIOStream(PATH_io.toString())
                .open()
                .add(set.data())
                .close();
    }

    @State(Scope.Benchmark)
    public static class BinaryStoreMappedHolder  {

        Store store;

        @Setup(Level.Trial)
        public void prepare(ByteDataSet data) throws IOException {
            store = new BinaryStoreIOStream(PATH_io_search.toString())
                    .open()
                    .add(data.data())
                    .synchronize();
        }
    }

    @Benchmark()
    @BenchmarkMode(Mode.Throughput)
    public void search10PercentOfStorage(BinaryStoreMappedHolder holder, ByteDataSet data) throws IOException {
        for (byte[] item : data.search()) {
            holder.store.get(item);
        }
    }

    public static void main(String[] args) throws RunnerException, IOException {
        FileUtils.deleteQuietly(PATH_io.toFile());
        FileUtils.deleteQuietly(PATH_io_search.toFile());
        Options opt = new OptionsBuilder()
                .include(BinaryStoreIOStreamBenchmark.class.getSimpleName())
                .forks(1)
                .warmupIterations(5)
                .measurementIterations(5)
                .build();

        new Runner(opt).run();
    }

}
