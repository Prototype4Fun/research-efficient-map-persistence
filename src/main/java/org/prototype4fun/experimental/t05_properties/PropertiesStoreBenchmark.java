package org.prototype4fun.experimental.t05_properties;

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
public class PropertiesStoreBenchmark {

    public static final Path PATH_direct = Paths.get("target/tdata/property_"+ Store.DefaultSize+".properties");
    public static final Path PATH_Search = Paths.get("target/tdata/property_" + Store.DefaultSize+"_search.properties");

    @Benchmark()
    @BenchmarkMode(Mode.Throughput)
    public void loadAndPersist(StringDataSet set) throws IOException {
        new PropertiesStore(PATH_direct.toString())
                .open()
                .add(set.data())
                .synchronize()
                .close();
    }


    @State(Scope.Benchmark)
    public static class PropertiesHolder {

        Store store;

        @Setup(Level.Trial)
        public void prepare(StringDataSet data) throws IOException {
            store = new PropertiesStore(PATH_Search.toString())
                    .open()
                    .add(data.data())
                    .synchronize();
        }
    }

    @Benchmark()
    @BenchmarkMode(Mode.Throughput)
    public void search10PercentOfStorage(PropertiesHolder holder, StringDataSet data) throws IOException {
        for (String item : data.search()) {
            holder.store.get(item);
        }
    }

    public static void main(String[] args) throws RunnerException, IOException {
        FileUtils.deleteQuietly(PATH_direct.toFile());
        FileUtils.deleteQuietly(PATH_direct.toFile());
        Options opt = new OptionsBuilder()
                .include(PropertiesStoreBenchmark.class.getSimpleName())
                .forks(1)
                .warmupIterations(5)
                .measurementIterations(5)
                .build();

        new Runner(opt).run();
    }

}
