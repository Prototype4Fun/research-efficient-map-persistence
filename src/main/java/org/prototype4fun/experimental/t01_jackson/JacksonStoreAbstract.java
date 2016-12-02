package org.prototype4fun.experimental.t01_jackson;

import com.fasterxml.jackson.databind.ObjectMapper;
import com.fasterxml.jackson.datatype.guava.GuavaModule;
import com.fasterxml.jackson.datatype.jdk8.Jdk8Module;
import org.prototype4fun.experimental.data.BytePointer;
import org.prototype4fun.experimental.data.Store;
import org.prototype4fun.experimental.data.StringPointer;
import org.prototype4fun.experimental.t04_customBinary.BinaryStoreAbstract;

import java.io.IOException;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.util.*;
import java.util.concurrent.atomic.AtomicReference;

/**
 * Created by SG0218109 on 2016-11-20.
 */
public abstract class JacksonStoreAbstract implements Store<String,StringPointer> {

    final ObjectMapper mapper;
    final Path base;
    final Map<String, StringPointer> map = new HashMap<>(Store.DefaultSize);

    JacksonStoreAbstract(String path) {
        this.base = Paths.get(path).toAbsolutePath();
        mapper = new ObjectMapper();
        mapper.registerModule(new Jdk8Module());
        mapper.registerModule(new GuavaModule());
    }

    @Override
    public StringPointer get(String hash) {
        return map.get(hash);

    }

    @Override
    public JacksonStoreAbstract add(StringPointer pointer) {
        map.computeIfAbsent(pointer.id(), key -> pointer);
        return this;
    }

    @Override
    public JacksonStoreAbstract close() throws IOException {
        synchronize();
        return this;
    }


}
