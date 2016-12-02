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
public class StringData extends ArrayList<String>{

    @Setup(Level.Trial)
    public void prepare() throws IOException {
        this.addAll(Files.readAllLines(Paths.get("uuid.txt"), CHARSET));
    }

}
