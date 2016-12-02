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
public class StringDataSet {


    Set<StringPointer> data;
    Set<String> search;

    public Set<StringPointer> data() {
        return data;
    }

    public Set<String> search() {
        return search;
    }

    @Setup(Level.Trial)
    public void prepare(Generator generator,StringData prepared) throws IOException {
        data = new HashSet<>();
        search = new HashSet<>();
        while (data.size() < Store.DefaultSize) {
            String hash = generator.sha1(prepared,32);
            StringPointer pointer = ImmutableStringPointer.builder().id(hash).build();
            data.add(pointer);
            if (data.size() % 100 == 0) {
                search.add(hash);
            }
        }
    }

}
