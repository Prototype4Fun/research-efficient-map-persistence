package org.prototype4fun.experimental.data;

import org.openjdk.jmh.annotations.Level;
import org.openjdk.jmh.annotations.Scope;
import org.openjdk.jmh.annotations.Setup;
import org.openjdk.jmh.annotations.State;

import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Paths;
import java.util.ArrayList;

import static org.prototype4fun.experimental.data.Generator.CHARSET;

/**
 * Created by SG0218109 on 2016-11-18.
 */
@State(Scope.Benchmark)
public class ByteData extends ArrayList<byte[]> {

    @Setup(Level.Trial)
    public void prepare() throws IOException {
        Files.readAllLines(Paths.get("uuid.txt"), CHARSET).stream()
                .map(String::getBytes)
                .forEach(this::add);
    }

}
