package org.prototype4fun.experimental.t04_customBinary;

import org.prototype4fun.experimental.data.BytePointer;
import org.prototype4fun.experimental.data.ImmutableBytePointer;
import org.prototype4fun.experimental.data.Store;

import java.io.IOException;
import java.io.RandomAccessFile;
import java.nio.ByteBuffer;
import java.nio.MappedByteBuffer;
import java.nio.channels.FileChannel;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.util.*;
import java.util.concurrent.atomic.AtomicReference;

/**
 * Created by SG0218109 on 2016-11-18.
 */
public class BinaryStoreMMap extends BinaryStoreAbstract{

    private FileChannel channel;
    private MappedByteBuffer buffer;

    public BinaryStoreMMap(String path) {
        super(path);
    }

    public BinaryStoreMMap synchronize() throws IOException {
        Queue<BytePointer> pointers = log.getAndSet(new LinkedList<>());
        if (pointers.size() > 0) {
            buffer = channel.map(FileChannel.MapMode.READ_WRITE, channel.size(), channel.size() + (HashSize + MaskElementSize) * pointers.size());
            pointers.forEach(p -> pToBB(p, buffer));
        }
        return this;
    }

    @Override
    public BinaryStoreMMap close() throws IOException {
        channel.close();
        return this;

    }

    public Store open() throws IOException {
        Files.createDirectories(base.getParent());
        RandomAccessFile accessFile = new RandomAccessFile(base.toAbsolutePath().toString(), "rw");
        channel = accessFile.getChannel();
        if (channel.size() > 0) {
            buffer = channel.map(FileChannel.MapMode.READ_ONLY, 0, channel.size());
            while (buffer.remaining() > 0) {
                BytePointer p = bbToP(buffer);
                map.put(BitSet.valueOf(p.id()), p);
            }
        }
        return this;
    }

    private static BytePointer bbToP(ByteBuffer byteBuffer) {
        byte[] hash = new byte[HashSize];
        byteBuffer.get(hash, 0, HashSize);
        long mask = byteBuffer.getLong();
        return ImmutableBytePointer.builder().id(hash).mask(mask).build();
    }

    private static ByteBuffer pToBB(BytePointer p, ByteBuffer buffer) {
        buffer.put(p.id());
        buffer.putLong(p.mask());
        return buffer;
    }

}
