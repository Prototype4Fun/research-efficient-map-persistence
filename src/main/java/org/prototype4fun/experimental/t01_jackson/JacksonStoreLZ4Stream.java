package org.prototype4fun.experimental.t01_jackson;

import com.fasterxml.jackson.core.JsonFactory;
import com.google.common.primitives.Ints;
import net.jpountz.lz4.LZ4Compressor;
import net.jpountz.lz4.LZ4Factory;
import net.jpountz.lz4.LZ4FastDecompressor;
import org.prototype4fun.experimental.data.ImmutableStringPointer;
import org.prototype4fun.experimental.data.StringPointer;

import java.io.ByteArrayOutputStream;
import java.io.File;
import java.io.FileOutputStream;
import java.io.IOException;
import java.nio.file.Files;

import static org.iq80.leveldb.impl.Iq80DBFactory.factory;

/**
 * Created by Radek on 17.11.2016.
 */
public class JacksonStoreLZ4Stream extends JacksonStoreAbstract {

    final LZ4Factory lz4 = LZ4Factory.fastestInstance();

    public JacksonStoreLZ4Stream(String name) {
        super(name);
    }

    @Override
    public JacksonStoreLZ4Stream open() throws IOException {
        Files.createDirectories(base.getParent());
        File file = new File(base.toString());
        if (file.exists() && file.length() > 0) {
            int length = Ints.fromByteArray(Files.readAllBytes(base.resolveSibling(base.getFileName().toString()+".meta")));
            byte[] decompress = lz4.fastDecompressor().decompress(Files.readAllBytes(base), length);//??
            ImmutableStringPointer[] pointerArray = mapper.readValue(decompress, ImmutableStringPointer[].class);
            for (StringPointer pointer : pointerArray) {
                map.put(pointer.id(), pointer);
            }
        }
        return this;
    }

    @Override
    public JacksonStoreLZ4Stream synchronize() throws IOException {
        if (map.size() > 0) {
            ByteArrayOutputStream outputStream = new ByteArrayOutputStream();
            mapper.writeValue(outputStream,map.values());
            try (FileOutputStream out = new FileOutputStream(base.toString(), false)) {
                byte[] array = outputStream.toByteArray();
                out.write(lz4.fastCompressor().compress(array));
                Files.write(base.resolveSibling(base.getFileName().toString()+".meta"), Ints.toByteArray(array.length));
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
