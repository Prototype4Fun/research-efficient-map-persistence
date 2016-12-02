package org.prototype4fun.experimental;

import org.openjdk.jmh.runner.Runner;
import org.openjdk.jmh.runner.RunnerException;
import org.openjdk.jmh.runner.options.Options;
import org.openjdk.jmh.runner.options.OptionsBuilder;
import org.prototype4fun.experimental.t01_jackson.JacksonStoreIOStreamBenchmark;
import org.prototype4fun.experimental.t01_jackson.JacksonStoreLZ4StreamBenchmark;
import org.prototype4fun.experimental.t01_jackson.JacksonStoreMMap;
import org.prototype4fun.experimental.t01_jackson.JacksonStoreMMapBenchmark;
import org.prototype4fun.experimental.t04_customBinary.BinaryStoreIOStreamBenchmark;
import org.prototype4fun.experimental.t04_customBinary.BinaryStoreMMapBenchmark;
import org.prototype4fun.experimental.t06_bdb7.BdbStoreBenchmark;
import org.prototype4fun.experimental.t07_levelDB.LevelDBStoreBenchmark;

/**
 * Created by Radek on 17.11.2016.
 */
public class AllBenchmarks {

    public static void main(String[] args) throws RunnerException {
        Options opt = new OptionsBuilder()
                .include(JacksonStoreIOStreamBenchmark.class.getSimpleName())
                .include(JacksonStoreMMapBenchmark.class.getSimpleName())
                .include(JacksonStoreLZ4StreamBenchmark.class.getSimpleName())
                .include(BinaryStoreIOStreamBenchmark.class.getSimpleName())
                .include(BinaryStoreMMapBenchmark.class.getSimpleName())
                //.include(XodusStoreBenchmark.class.getSimpleName())
                .include(BdbStoreBenchmark.class.getSimpleName())
                .include(LevelDBStoreBenchmark.class.getSimpleName())
                .forks(1)
                .warmupIterations(10)
                .measurementIterations(10)
                .build();
        new Runner(opt).run();
    }
}
