package org.prototype4fun.experimental.t00_general;

import com.google.common.hash.HashFunction;
import com.google.common.hash.Hasher;
import com.google.common.hash.Hashing;
import org.openjdk.jmh.annotations.*;
import org.openjdk.jmh.runner.Runner;
import org.openjdk.jmh.runner.RunnerException;
import org.openjdk.jmh.runner.options.Options;
import org.openjdk.jmh.runner.options.OptionsBuilder;
import org.prototype4fun.experimental.data.ByteData;
import org.prototype4fun.experimental.data.StringData;

import java.io.IOException;
import java.util.*;

import static org.prototype4fun.experimental.data.Generator.CHARSET;

/**
 * Created by SG0218109 on 2016-11-18.
 */
public class Sha1Benchmark {

    static final HashFunction sha1 = Hashing.sha1();
    static final Random random = new Random(System.currentTimeMillis());
    public static final int NumOfTags = 32;

    @Benchmark()
    @BenchmarkMode(Mode.Throughput)
    public void sha1PointersFromStringTags(StringData data) throws IOException {
        Hasher h = sha1.newHasher();
        int idx = random.nextInt(data.size());
        for(int i = 0; i< NumOfTags; i++){
            h.putString(data.get(idx), CHARSET);
        }
        h.hash();
    }

    @Benchmark()
    @BenchmarkMode(Mode.Throughput)
    public void sha1PointersFromStringTagsAsBytes(StringData data) throws IOException {
        Hasher h = sha1.newHasher();
        int idx = random.nextInt(data.size());
        for(int i = 0; i< NumOfTags; i++){
            h.putBytes(data.get(idx).getBytes());
        }
        h.hash();
    }

    @Benchmark()
    @BenchmarkMode(Mode.Throughput)
    public void sha1PointersFromByteTags(ByteData data) throws IOException {
        Hasher h = sha1.newHasher();
        int idx = random.nextInt(data.size());
        for(int i = 0; i< NumOfTags; i++){
            h.putBytes(data.get(idx));
        }
        h.hash().asInt();
    }

    public static void main(String[] args) throws RunnerException, IOException {
        Options opt = new OptionsBuilder()
                .include(Sha1Benchmark.class.getSimpleName())
                .forks(1)
                .warmupIterations(5)
                .measurementIterations(5)
                .build();

        new Runner(opt).run();
    }


}
