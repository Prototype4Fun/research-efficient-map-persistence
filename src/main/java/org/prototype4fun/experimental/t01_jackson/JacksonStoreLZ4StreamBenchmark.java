package org.prototype4fun.experimental.t01_jackson;

import org.apache.commons.io.FileUtils;
import org.openjdk.jmh.annotations.*;
import org.openjdk.jmh.runner.Runner;
import org.openjdk.jmh.runner.RunnerException;
import org.openjdk.jmh.runner.options.Options;
import org.openjdk.jmh.runner.options.OptionsBuilder;
import org.prototype4fun.experimental.data.Store;
import org.prototype4fun.experimental.data.StringDataSet;

import java.io.IOException;
import java.nio.file.Path;
import java.nio.file.Paths;

/**
 * Created by Radek on 17.11.2016.
 */
public class JacksonStoreLZ4StreamBenchmark {

    public static final Path PATH = Paths.get("target/tdata/jackson_compressed_"+ Store.DefaultSize+".lz4");
    public static final Path PATH_search = Paths.get("target/tdata/jackson_compressed_search_" + Store.DefaultSize+".lz4");

    @Benchmark()
    @BenchmarkMode(Mode.Throughput)
    public void loadAndPersist(StringDataSet set) throws IOException {
        new JacksonStoreLZ4Stream(PATH.toString())
                .open()
                .add(set.data())
                .close();
    }

    @State(Scope.Benchmark)
    public static class JacksonStoreHolder {

        Store store;

        @Setup(Level.Trial)
        public void prepare(StringDataSet data) throws IOException {
            store = new JacksonStoreLZ4Stream(PATH_search.toString())
                    .open()
                    .add(data.data())
                    .synchronize();
        }
    }

    @Benchmark()
    @BenchmarkMode(Mode.Throughput)
    public void search10PercentOfStorage(JacksonStoreHolder holder, StringDataSet data) throws IOException {
        for (String item : data.search()) {
            holder.store.get(item);
        }
    }


    public static void main(String[] args) throws RunnerException, IOException {
        FileUtils.deleteQuietly(PATH.toFile());
        FileUtils.deleteQuietly(PATH_search.toFile());
        Options opt = new OptionsBuilder()
                .include(JacksonStoreLZ4StreamBenchmark.class.getSimpleName())
                .forks(1)
                .warmupIterations(5)
                .measurementIterations(5)
                .build();

        new Runner(opt).run();
    }

}
