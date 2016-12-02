package org.prototype4fun.experimental.t04_customBinary;

import org.prototype4fun.experimental.data.BytePointer;
import org.prototype4fun.experimental.data.Store;

import java.math.BigInteger;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.util.*;
import java.util.concurrent.atomic.AtomicReference;

/**
 * Created by SG0218109 on 2016-11-20.
 */
public abstract class BinaryStoreAbstract implements Store<byte[], BytePointer> {

    static final int HashSize = 20;
    //public static final int MaskLength = 24;
    static final int MaskElementSize = 8;

    protected final Path base;
    final Map<BitSet, BytePointer> map = new HashMap<>(Store.DefaultSize);
    final AtomicReference<Queue<BytePointer>> log = new AtomicReference<>(new LinkedList<BytePointer>());

    BinaryStoreAbstract(String path) {
        this.base = Paths.get(path);
    }


    @Override
    public BytePointer get(byte[] hash) {
        return map.get(BitSet.valueOf(hash));

    }

    @Override
    public BinaryStoreAbstract add(BytePointer pointer) {
        map.computeIfAbsent(BitSet.valueOf(pointer.id()), key -> commit(pointer));
        return this;
    }

    private BytePointer commit(BytePointer pointer) {
        log.get().offer(pointer);
        return pointer;
    }
}
