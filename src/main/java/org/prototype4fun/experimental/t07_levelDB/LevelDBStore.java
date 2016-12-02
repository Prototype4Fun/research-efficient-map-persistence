package org.prototype4fun.experimental.t07_levelDB;

import com.google.common.primitives.Longs;
import org.iq80.leveldb.*;
import org.prototype4fun.experimental.data.*;

import java.io.File;
import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.util.Collection;

import static org.iq80.leveldb.impl.Iq80DBFactory.factory;

/**
 * Created by SG0218109 on 2016-11-19.
 */
public class LevelDBStore implements Store<byte[], BytePointer> {

    private final Path base;
    private DB db;

    public LevelDBStore(String path) {
        this.base = Paths.get(path);
    }

    @Override
    public BytePointer get(byte[] key) {
        long mask = Longs.fromByteArray(db.get(key));
        return ImmutableBytePointer.builder().id(key).mask(mask).build();
    }

    public LevelDBStore add(Collection<BytePointer> pointers) throws IOException {
        WriteBatch batch = db.createWriteBatch();
        pointers.forEach(p -> batch.put(p.id(), Longs.toByteArray(p.mask())));
        db.write(batch);
        batch.close();
        return this;
    }

    @Override
    public LevelDBStore add(BytePointer p) {
        db.put(p.id(), Longs.toByteArray(p.mask()));
        return this;
    }

    public LevelDBStore open() throws IOException {
        Files.createDirectories(base);
        Options options = new Options();
        options.compressionType(CompressionType.NONE);
        options.cacheSize(50 * 1048576);
        options.createIfMissing(true);
        options.logger(System.out::println);
        db = factory.open(new File(base.toString()), options);
        return this;
    }

    public LevelDBStore close() throws IOException {
        db.close();
        return this;
    }

    public static void main(String[] args) throws IOException, InterruptedException {
        Path PATH_direct = Paths.get("target/tdata/leveldb_"+ Store.DefaultSize+".test");
        LevelDBStore store = new LevelDBStore(PATH_direct.toString()).open();
        ByteDataSet dataSet = new ByteDataSet();
        ByteData data = new ByteData();
        data.prepare();
        dataSet.prepare(new Generator(), data);
        dataSet.data().forEach(store::add);
        Thread.sleep(1000);
        store.close();
    }

}
