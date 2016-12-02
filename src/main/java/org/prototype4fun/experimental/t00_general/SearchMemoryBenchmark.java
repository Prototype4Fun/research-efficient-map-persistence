package org.prototype4fun.experimental.t00_general;

import com.google.common.cache.Cache;
import com.google.common.cache.CacheBuilder;
import com.google.common.collect.Maps;
import org.jetbrains.annotations.NotNull;
import org.openjdk.jmh.annotations.*;
import org.openjdk.jmh.runner.Runner;
import org.openjdk.jmh.runner.RunnerException;
import org.openjdk.jmh.runner.options.Options;
import org.openjdk.jmh.runner.options.OptionsBuilder;
import org.prototype4fun.experimental.data.ByteDataSet;
import org.prototype4fun.experimental.data.Store;
import org.prototype4fun.experimental.data.StringDataSet;

import java.io.IOException;
import java.math.BigDecimal;
import java.math.BigInteger;
import java.util.*;

/**
 * Created by SG0218109 on 2016-11-19.
 */
public class SearchMemoryBenchmark {

    public static class Key {

        private final byte[] bytes;
        private final int hc;

        public Key(byte[] bytes) {
            this.bytes = bytes;
            this.hc = bytes[0] & 255 | (bytes[1] & 255) << 8 | (bytes[2] & 255) << 16 | (bytes[3] & 255) << 24;
        }

        @Override
        public boolean equals(Object o) {
            if (this == o) return true;
            if (o == null || getClass() != o.getClass()) return false;
            Key key = (Key) o;
            for (int i = 0; i < bytes.length; i++)
                if (bytes[i] != key.bytes[i])
                    return false;

            return true;
        }

        @Override
        public int hashCode() {
            return hc;
        }
    }

    @State(Scope.Benchmark)
    public static class SortedKeyData extends ArrayList<BigDecimal> {

        private Cache<Key, Key> guava = CacheBuilder.newBuilder().maximumSize(Store.DefaultSize).build();
        private Map<Key, Key> hashMap;
        private Set<Key> search;

        @Setup(Level.Trial)
        public void prepare(ByteDataSet data) throws IOException {
            search = new HashSet<>();
            data.search().forEach(p -> search.add(new Key(p)));

            hashMap = new HashMap<>(Store.DefaultSize);
            data.data().forEach(p -> {
                Key key = new Key(p.id());
                hashMap.put(key, key);
                guava.put(key,key);
            });


        }
    }

    @State(Scope.Benchmark)
    public static class SortedStringData extends ArrayList<BigDecimal> {

        private Cache<String, String> guava = CacheBuilder.newBuilder().maximumSize(Store.DefaultSize).build();
        private Map<String, String> hashMap;
        private String[] array;
        private ArrayList<String> list;
        private Set<String> search;

        @Setup(Level.Trial)
        public void prepare(StringDataSet data) throws IOException {
            search = new HashSet<>();
            data.search().forEach(p -> search.add(new String(p.getBytes())));

            list = new ArrayList<>();
            data.data().forEach(p -> list.add(p.id()));
            Collections.sort(list);
            array = list.toArray(new String[list.size()]);

            hashMap = new HashMap<>(Store.DefaultSize);

            data.data().forEach(p -> {
                String key = p.id();
                hashMap.put(key, key);
                guava.put(key,key);
            });

        }
    }

    @State(Scope.Benchmark)
    public static class SortedBigIntegerData extends ArrayList<BigDecimal> {

        private Cache<BigInteger, BigInteger> guava = CacheBuilder.newBuilder().maximumSize(Store.DefaultSize).build();
        private Map<BigInteger, BigInteger> hashMap;
        private Map<BigInteger, BigInteger> linkedHashMap;
        private Map<BigInteger, BigInteger> googleHashMap;
        private BigInteger[] array;
        private ArrayList<BigInteger> list;
        private Set<BigInteger> search;

        @Setup(Level.Trial)
        public void prepare(ByteDataSet data) throws IOException {
            search = new HashSet<>();
            data.search().forEach(p -> search.add(new BigInteger(p)));

            list = new ArrayList<>();
            data.data().forEach(p -> list.add(new BigInteger(p.id())));
            Collections.sort(list);
            array = list.toArray(new BigInteger[list.size()]);

            hashMap = new HashMap<>(Store.DefaultSize);
            linkedHashMap = new LinkedHashMap<>();
            googleHashMap = Maps.newHashMap();
            data.data().forEach(p -> {
                BigInteger key = new BigInteger(p.id());
                hashMap.put(key, key);
                linkedHashMap.put(key, key);
                googleHashMap.put(key, key);
                guava.put(key,key);
            });

        }
    }

    @State(Scope.Benchmark)
    public static class SortedBitSetData extends ArrayList<BigDecimal> {

        private Cache<BitSet, BitSet> guava = CacheBuilder.newBuilder().maximumSize(Store.DefaultSize).build();
        private Map<BitSet, BitSet> hashMap;
        private Map<BitSet, BitSet> linkedHashMap;
        private Map<BitSet, BitSet> googleHashMap;
        private BitSet[] array;
        private Set<BitSet> search;

        @Setup(Level.Trial)
        public void prepare(ByteDataSet data) throws IOException {
            search = new HashSet<>();
            data.search().forEach(p -> search.add(BitSet.valueOf(p)));

            ArrayList<BitSet> list = new ArrayList<>(Store.DefaultSize);
            data.data().forEach(p -> list.add(BitSet.valueOf(p.id())));
            Collections.sort(list, comparator());
            array = list.toArray(new BitSet[list.size()]);
            hashMap = new HashMap<>(Store.DefaultSize);
            linkedHashMap = new LinkedHashMap<>();
            googleHashMap = Maps.newHashMap();
            data.data().forEach(p -> {
                BitSet key = BitSet.valueOf(p.id());
                hashMap.put(key, key);
                linkedHashMap.put(key, key);
                googleHashMap.put(key, key);
                guava.put(key,key);
            });

        }

        @NotNull
        static Comparator<BitSet> comparator() {
            return (o1, o2) -> new BigInteger(o1.toByteArray()).compareTo(new BigInteger(o2.toByteArray()));
        }
    }


    @Benchmark()
    @BenchmarkMode(Mode.Throughput)
    public void hashMapSearchBigInteger(SortedBigIntegerData data) throws IOException {
        for (BigInteger bigInteger : data.search) {
            data.hashMap.get(bigInteger);
        }
    }

    @Benchmark()
    @BenchmarkMode(Mode.Throughput)
    public void guavaSearchBigInteger(SortedBigIntegerData data) throws IOException {
        for (BigInteger bigInteger : data.search) {
            data.guava.getIfPresent(bigInteger);
        }
    }
//    @Benchmark()
//    @BenchmarkMode(Mode.Throughput)
//    public void arrayBinarySearchBigInteger(SortedBigIntegerData data) throws IOException {
//        for (BigInteger bigInteger : data.search) {
//            Arrays.binarySearch(data.array, bigInteger);
//        }
//    }

    @Benchmark()
    @BenchmarkMode(Mode.Throughput)
    public void hashMapSearchBitSet(SortedBitSetData data) throws IOException {
        for (BitSet item : data.search) {
            data.hashMap.get(item);
        }
    }

    @Benchmark()
    @BenchmarkMode(Mode.Throughput)
    public void guavaSearchBitSet(SortedBitSetData data) throws IOException {
        for (BitSet item : data.search) {
            data.guava.getIfPresent(item);
        }
    }

//    @Benchmark()
//    @BenchmarkMode(Mode.Throughput)
//    public void arrayBinarySearchBitSet(SortedBitSetData data) throws IOException {
//        for (BitSet item : data.search) {
//            Arrays.binarySearch(data.array, item, comparator());
//        }
//    }

    @Benchmark()
    @BenchmarkMode(Mode.Throughput)
    public void hashMapSearchString(SortedStringData data) throws IOException {
        for (String item : data.search) {
            data.hashMap.get(item);
        }
    }

    @Benchmark()
    @BenchmarkMode(Mode.Throughput)
    public void guavaSearchString(SortedStringData data) throws IOException {
        for (String item : data.search) {
            data.guava.getIfPresent(item);
        }
    }


    @Benchmark()
    @BenchmarkMode(Mode.Throughput)
    public void hashMapSearchKey(SortedKeyData data) throws IOException {
        for (Key item : data.search) {
            data.hashMap.get(item);
        }
    }


    @Benchmark()
    @BenchmarkMode(Mode.Throughput)
    public void guavaSearchKey(SortedKeyData data) throws IOException {
        for (Key item : data.search) {
            data.guava.getIfPresent(item);
        }
    }


//    @Benchmark()
//    @BenchmarkMode(Mode.Throughput)
//    public void arrayBinarySearchString(SortedStringData data) throws IOException {
//        for (String item : data.search) {
//            Arrays.binarySearch(data.array, item);
//        }
//    }

//    // doesn't even make sense actually to measure
//    @Benchmark()
//    @BenchmarkMode(Mode.Throughput)
//    public void arrayListIndexOfSearchBigInteger(SortedBigIntegerData data) throws IOException {
//        for (BigInteger bigInteger : data.search) {
//            data.list.indexOf(bigInteger);
//        }
//    }
//

//    @Benchmark()
//    @BenchmarkMode(Mode.Throughput)
//    public void linkedHashMapSearchBigInteger(SortedBigIntegerData data) throws IOException {
//        for (BigInteger bigInteger : data.search) {
//            data.linkedHashMap.get(bigInteger);
//        }
//    }


//    @Benchmark()
//    @BenchmarkMode(Mode.Throughput)
//    public void googleHashMapSearchBigInteger(SortedBigIntegerData data) throws IOException {
//        for (BigInteger bigInteger : data.search) {
//            data.googleHashMap.get(bigInteger);
//        }
//    }

//
//    @Benchmark()
//    @BenchmarkMode(Mode.Throughput)
//    public void arrayBinarySearchBitSet(SortedBitSetData data) throws IOException {
//        for (BitSet item : data.search) {
//            Arrays.binarySearch(data.array, item, comparator());
//        }
//    }


//    @Benchmark()
//    @BenchmarkMode(Mode.Throughput)
//    public void linkedHashMapSearchBitSet(SortedBitSetData data) throws IOException {
//        for(BitSet item : data.search){
//            data.linkedHashMap.get(item);
//        }
//    }
//
//
//    @Benchmark()
//    @BenchmarkMode(Mode.Throughput)
//    public void googleHashMapSearchBitSet(SortedBitSetData data) throws IOException {
//        for(BitSet item : data.search){
//            data.googleHashMap.get(item);
//        }
//    }

    public static void main(String[] args) throws RunnerException, IOException {
        Options opt = new OptionsBuilder()
                .include(SearchMemoryBenchmark.class.getSimpleName())
                .forks(1)
                .warmupIterations(5)
                .measurementIterations(5)
                .build();

        new Runner(opt).run();
    }

}
