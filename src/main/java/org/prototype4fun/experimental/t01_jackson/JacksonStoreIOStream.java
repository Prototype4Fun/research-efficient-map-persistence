package org.prototype4fun.experimental.t01_jackson;

import com.fasterxml.jackson.core.JsonFactory;
import org.prototype4fun.experimental.data.ImmutableStringPointer;
import org.prototype4fun.experimental.data.StringPointer;

import java.io.*;
import java.nio.file.Files;

/**
 * Created by Radek on 17.11.2016.
 */
public class JacksonStoreIOStream extends JacksonStoreAbstract {

    private final JsonFactory jasonFactory = new JsonFactory();

    public JacksonStoreIOStream(String name) {
        super(name);
    }

    @Override
    public JacksonStoreIOStream open() throws IOException {
        Files.createDirectories(base.getParent());
        File file = new File(base.toString());
        if (file.exists() && file.length() > 0) {
            ImmutableStringPointer[] pointerArray = mapper.readValue(file, ImmutableStringPointer[].class);
            for (StringPointer pointer : pointerArray) {
                map.put(pointer.id(), pointer);
            }
        }
        return this;
    }

    @Override
    public JacksonStoreIOStream synchronize() throws IOException {
        if (map.size() > 0) {
            ByteArrayOutputStream outputStream = new ByteArrayOutputStream();
            mapper.writeValue(outputStream,map.values());
            try (FileOutputStream out = new FileOutputStream(base.toString(), false)) {
                out.write(outputStream.toByteArray());
            }
        }
        return this;
    }

//    public static void main(String[] args) throws IOException {
//
//        new JacksonStoreIOStream("test.json")
//                .open()
//                .add(ImmutableStringPointer.builder().id("1").mask(1).build())
//                .synchronize()
//                .add(ImmutableStringPointer.builder().id("2").mask(2).build())
//                .close();
//
//
//    }

}
