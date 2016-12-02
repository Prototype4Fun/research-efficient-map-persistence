package org.prototype4fun.experimental.t04_customBinary;

import com.google.common.primitives.Longs;
import org.prototype4fun.experimental.data.BytePointer;
import org.prototype4fun.experimental.data.ImmutableBytePointer;
import org.prototype4fun.experimental.data.Store;

import java.io.*;
import java.math.BigInteger;
import java.nio.ByteBuffer;
import java.nio.MappedByteBuffer;
import java.nio.channels.FileChannel;
import java.nio.file.*;
import java.util.BitSet;
import java.util.HashMap;
import java.util.Map;
import java.util.Queue;
import java.util.concurrent.atomic.AtomicReference;

/**
 * Created by SG0218109 on 2016-11-18.
 */
public class BinaryStoreIOStream extends BinaryStoreAbstract {


    public BinaryStoreIOStream(String path) {
        super(path);
    }

    @Override
    public BinaryStoreIOStream close() throws IOException {
        synchronize();
        return this;
    }

    public BinaryStoreIOStream synchronize() throws IOException {

        Queue<BytePointer> bytePointers = log.get();
        int size = bytePointers.size();
        ByteBuffer buffer = ByteBuffer.allocate((HashSize + MaskElementSize) * size);
        for (BytePointer pointer : bytePointers) {
            pToStream(pointer, buffer);
        }
        buffer.flip();
        Files.write(base,buffer.array(), StandardOpenOption.APPEND,StandardOpenOption.CREATE);
        return this;
    }

    public Store open() throws IOException {
        Files.createDirectories(base.getParent());
        File file = base.toFile();
        if (file.exists()) {
            byte[] hash = new byte[HashSize]; //some reuse
            byte[] longValue = new byte[MaskElementSize];
            byte[] data = Files.readAllBytes(base);
            ByteArrayInputStream arrayInputStream = new ByteArrayInputStream(data);
            while (arrayInputStream.available() > 0) {
                ImmutableBytePointer bytePointer = streamToP(arrayInputStream, hash, longValue);
                map.put(BitSet.valueOf(bytePointer.id()), bytePointer);
            }

        }
        return this;
    }

    private static ImmutableBytePointer streamToP(InputStream stream, byte[] hash, byte[] longValue) throws IOException {
        stream.read(hash);
        stream.read(longValue);
        long mask = Longs.fromByteArray(longValue);
        return ImmutableBytePointer.builder().id(hash).mask(mask).build();
    }

    private static void pToStream(BytePointer p, ByteBuffer buffer) throws IOException {
        buffer.put(p.id());
        buffer.putLong(p.mask());
    }
}
