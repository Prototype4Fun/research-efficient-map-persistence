package org.prototype4fun.experimental.t06_bdb7;

import com.sleepycat.je.*;
import com.sleepycat.persist.EntityStore;
import com.sleepycat.persist.PrimaryIndex;
import com.sleepycat.persist.StoreConfig;
import org.prototype4fun.experimental.data.Store;
import org.prototype4fun.experimental.data.StringPointer;

import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.util.Collection;

/**
 * Created by SG0218109 on 2016-11-19.
 */
public class BdbStore implements Store<String,StringPointer> {

    private final Path base;
    private Environment env;
    private EntityStore store;
    private Durability newDurability;
    private TransactionConfig tc;

    public BdbStore(String path) {
        this.base = Paths.get(path);
    }

    public BdbStore open() throws IOException {
//        newDurability =
//                new Durability(Durability.SyncPolicy.NO_SYNC,
//                        null,    // unused by non-HA applications.
//                        null);   // unused by non-HA applications.
        Files.createDirectories(base);
        EnvironmentConfig envConfig = new EnvironmentConfig();
        envConfig.setAllowCreate(true);
//        envConfig.setTxnSerializableIsolation(false);
//        envConfig.setDurability(newDurability);
//        envConfig.setTransactional(false);
//        envConfig.setCachePercent(50);

//        tc = new TransactionConfig();
//        tc.setDurability(newDurability);

        env = new Environment(base.toFile(), envConfig);
        StoreConfig storeConfig = new StoreConfig();
        storeConfig.setAllowCreate(true);
//        storeConfig.setTransactional(false);
        storeConfig.setDeferredWrite(true);
        store = new EntityStore(env, "Pointers", storeConfig);
        return this;
    }

    @Override
    public StringPointer get(String key) {
        return store.getPrimaryIndex(String.class, BdbPointer.class).get(key);
    }

    public BdbStore add(Collection<StringPointer> pointers) throws IOException {
        PrimaryIndex<String, BdbPointer> primaryIndex = store.getPrimaryIndex(String.class, BdbPointer.class);
       // Transaction tx = env.beginTransaction(null, tc);
        pointers.forEach( p -> {
            primaryIndex.put(new BdbPointer(p.id(),p.mask()));
        });
      //  tx.commit();
        return this;
    }


    @Override
    public BdbStore add(StringPointer pointer) {
        store.getPrimaryIndex(String.class, BdbPointer.class).putNoOverwrite(new BdbPointer(pointer.id(),pointer.mask()));
        return this;
    }

    public BdbStore synchronize() throws IOException {
        store.sync();
        return this;
    }

    @Override
    public BdbStore close() throws IOException {
        store.close();
        env.close();
        return this;
    }
}
