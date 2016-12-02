package org.prototype4fun.experimental.t07_levelDB;

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
public class LevelDBStoreBenchmark {

    public static final Path PATH_direct = Paths.get("target/tdata/leveldb_"+ Store.DefaultSize+".db");
    public static final Path PATH_Search = Paths.get("target/tdata/leveldb_" + Store.DefaultSize+"_search.db");

    @Benchmark()
    @BenchmarkMode(Mode.Throughput)
    public void loadAndPersist(ByteDataSet set) throws IOException {
        new LevelDBStore(PATH_direct.toString())
                .open()
                .add(set.data())
                .synchronize()
                .close();
    }


    @State(Scope.Benchmark)
    public static class LevelDBStoreHolder  {

        Store store;

        @Setup(Level.Trial)
        public void prepare(ByteDataSet data) throws IOException {
            store = new LevelDBStore(PATH_Search.toString())
                    .open()
                    .add(data.data())
                    .synchronize();
        }
    }

    @Benchmark()
    @BenchmarkMode(Mode.Throughput)
    public void search10PercentOfStorage(LevelDBStoreHolder holder, ByteDataSet data) throws IOException {
        for (byte[] item : data.search()) {
            holder.store.get(item);
        }
    }

    public static void main(String[] args) throws RunnerException, IOException {
        FileUtils.deleteQuietly(PATH_direct.toFile());
        FileUtils.deleteQuietly(PATH_direct.toFile());
        Options opt = new OptionsBuilder()
                .include(LevelDBStoreBenchmark.class.getSimpleName())
                .forks(1)
                .warmupIterations(5)
                .measurementIterations(5)
                .build();

        new Runner(opt).run();
    }

}
