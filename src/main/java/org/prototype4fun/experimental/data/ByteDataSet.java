package org.prototype4fun.experimental.data;

import org.openjdk.jmh.annotations.Level;
import org.openjdk.jmh.annotations.Scope;
import org.openjdk.jmh.annotations.Setup;
import org.openjdk.jmh.annotations.State;

import java.io.IOException;
import java.util.HashSet;
import java.util.Set;

/**
 * Created by SG0218109 on 2016-11-18.
 */
@State(Scope.Benchmark)
public class ByteDataSet {


    Set<BytePointer> data;
    Set<byte[]> search;

    public Set<BytePointer> data() {
        return data;
    }

    public Set<byte[]> search() {
        return search;
    }

    @Setup(Level.Trial)
    public void prepare(Generator generator,ByteData prepared) throws IOException {
        data = new HashSet<>();
        search = new HashSet<>();
        while (data.size() < Store.DefaultSize) {
            byte[] hash = generator.sha1B(prepared,32);
            ImmutableBytePointer pointer = ImmutableBytePointer.builder().id(hash).build();
            data.add(pointer);
            if (data.size() % 100 == 0) {
                search.add(hash);
            }
        }
    }

}
