package org.prototype4fun.experimental.t01_jackson;

import com.fasterxml.jackson.core.JsonFactory;
import org.jetbrains.annotations.NotNull;
import org.prototype4fun.experimental.data.ImmutableStringPointer;
import org.prototype4fun.experimental.data.StringPointer;

import java.io.*;
import java.nio.MappedByteBuffer;
import java.nio.channels.Channel;
import java.nio.channels.FileChannel;
import java.nio.file.Files;

/**
 * Created by Radek on 17.11.2016.
 */
public class JacksonStoreMMap extends JacksonStoreAbstract {

    private final JsonFactory jasonFactory = new JsonFactory();
    private RandomAccessFile accessFile;
    private FileChannel channel;

    public JacksonStoreMMap(String name) {
        super(name);
    }

    @Override
    public JacksonStoreMMap open() throws IOException {
        Files.createDirectories(base.getParent());
        File file = new File(base.toString());
        accessFile = new RandomAccessFile(base.toString(), "rw");
        channel = accessFile.getChannel();
        if (file.exists() && channel.size() > 0) {
            ImmutableStringPointer[] pointerArray = mapper.readValue(new MMapInputStream(channel), ImmutableStringPointer[].class);
            for (StringPointer pointer : pointerArray) {
                map.put(pointer.id(), pointer);
            }
        }
        return this;
    }

    @Override
    public JacksonStoreMMap synchronize() throws IOException {
        if (map.size() > 0) {
            mapper.writeValue(new MMapOutputStream(channel), map.values());
        }
        return this;
    }

    @Override
    public JacksonStoreAbstract close() throws IOException {
        synchronize();
        channel.close();
        return this;
    }

    private static class MMapOutputStream extends OutputStream{

        final ByteArrayOutputStream byteArrayOutputStream = new ByteArrayOutputStream();
        final FileChannel channel;

        public MMapOutputStream(FileChannel channel){
            this.channel = channel;
        }

        @Override
        public void write(int b) throws IOException {
            byteArrayOutputStream.write(b);
        }

        public void write(byte b[]) throws IOException {
           byteArrayOutputStream.write(b);
        }

        public void close() throws IOException {
            byte[] array = byteArrayOutputStream.toByteArray();
            MappedByteBuffer buffer = channel.map(FileChannel.MapMode.READ_WRITE, 0, array.length);
            buffer.put(array);
//            buffer.force();
        }

    }

    private static class MMapInputStream extends InputStream {

        private final MappedByteBuffer buffer;

        MMapInputStream(FileChannel channel) throws IOException {
            buffer = channel.map(FileChannel.MapMode.READ_ONLY, 0, channel.size());
        }

        @Override
        public int read() throws IOException {
            return buffer.position() < buffer.limit() ? buffer.get() : -1;
        }

        public int available() throws IOException {
            return buffer.remaining();
        }
    }

    public static void main(String[] args) throws IOException {

        new JacksonStoreIOStream("test.json")
                .open()
                .add(ImmutableStringPointer.builder().id("1").mask(1).build())
                .synchronize()
                .add(ImmutableStringPointer.builder().id("2").mask(2).build())
                .close();


    }

}
