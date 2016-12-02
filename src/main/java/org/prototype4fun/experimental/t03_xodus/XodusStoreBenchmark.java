package org.prototype4fun.experimental.t03_xodus;

import org.apache.commons.io.FileUtils;
import org.openjdk.jmh.annotations.*;
import org.openjdk.jmh.runner.Runner;
import org.openjdk.jmh.runner.RunnerException;
import org.openjdk.jmh.runner.options.Options;
import org.openjdk.jmh.runner.options.OptionsBuilder;
import org.prototype4fun.experimental.data.Store;
import org.prototype4fun.experimental.data.StringDataSet;

import java.io.IOException;
import java.math.BigDecimal;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.util.ArrayList;

/**
 * Created by Radek on 17.11.2016.
 */
public class XodusStoreBenchmark {
    //its too damn slow
    public static final Path PATH_Persist = Paths.get("target/tdata/xodus_persist" + Store.DefaultSize);
    public static final Path PATH_Search = Paths.get("target/tdata/xodus_search" + Store.DefaultSize);

    @Benchmark()
    @BenchmarkMode(Mode.Throughput)
    public void loadAndPersist(StringDataSet set) throws IOException {
        new XodusStore(PATH_Persist.toString())
                .open()
                .add(set.data())
                .close();
    }


    @State(Scope.Benchmark)
    public static class XodusStoreHolder extends ArrayList<BigDecimal> {

        Store store;

        @Setup(Level.Trial)
        public void prepare(StringDataSet data) throws IOException {
            store = new XodusStore(PATH_Search.toString())
                    .open()
                    .add(data.data());
        }
    }

    @Benchmark()
    @BenchmarkMode(Mode.Throughput)
    public void search10PercentOfStorage(XodusStoreHolder holder,StringDataSet data) throws IOException {
        for (String item : data.search()) {
            holder.store.get(item);
        }
    }


    public static void main(String[] args) throws RunnerException, IOException {
        FileUtils.deleteQuietly(PATH_Persist.toFile());
        FileUtils.deleteQuietly(PATH_Search.toFile());
        Options opt = new OptionsBuilder()
                .include(XodusStoreBenchmark.class.getSimpleName())
                .forks(1)
                .warmupIterations(5)
                .measurementIterations(5)
                .build();

        new Runner(opt).run();
    }

}
