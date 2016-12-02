package org.prototype4fun.experimental.t05_properties;

import org.prototype4fun.experimental.data.ImmutableStringPointer;
import org.prototype4fun.experimental.data.Store;
import org.prototype4fun.experimental.data.StringPointer;

import java.io.FileInputStream;
import java.io.FileOutputStream;
import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.util.Properties;

/**
 * Created by SG0218109 on 2016-11-19.
 */
public class PropertiesStore implements Store<String, StringPointer> {

    private final Properties properties = new Properties();
    private final Path base;

    public PropertiesStore(String path) {
        this.base = Paths.get(path);
    }

    public PropertiesStore open() throws IOException {
        Files.createDirectories(base.getParent());
        if (base.toFile().exists()) {
            this.properties.load(new FileInputStream(base.toString()));
        }
        return this;
    }

    public PropertiesStore close() throws IOException {
        return this.synchronize();
    }

    public PropertiesStore synchronize() throws IOException {
        this.properties.store(new FileOutputStream(base.toString()), "None");
        return this;
    }

    @Override
    public StringPointer get(String key) {
        String mask = (String) properties.get(key);
        return null != mask ? ImmutableStringPointer.builder().id(key).mask(Long.parseLong(mask)).build() : null;
    }

    @Override
    public Store add(StringPointer pointer) {
        properties.put(pointer.id(), String.valueOf(pointer.mask()));
        return this;
    }
}
