package org.prototype4fun.experimental.t03_xodus;

import jetbrains.exodus.ByteIterable;
import jetbrains.exodus.env.*;
import org.prototype4fun.experimental.data.ImmutableStringPointer;
import org.prototype4fun.experimental.data.StringPointer;

import java.io.IOException;

import static jetbrains.exodus.bindings.LongBinding.entryToLong;
import static jetbrains.exodus.bindings.LongBinding.longToEntry;
import static jetbrains.exodus.bindings.StringBinding.stringToEntry;

/**
 * Created by Radek on 17.11.2016.
 */
public class XodusStore implements org.prototype4fun.experimental.data.Store<String,StringPointer> {
    private final String fileName;
    private ContextualEnvironment  env;
    private jetbrains.exodus.env.Store store;

    public XodusStore(String name) {
        fileName = name;
    }

    @Override
    public StringPointer get(final String hash) {
        return env.computeInReadonlyTransaction((TransactionalComputable<StringPointer>) txn -> {
            final ByteIterable entry = store.get(txn, stringToEntry(hash));
            long mask = entryToLong(entry);
            return ImmutableStringPointer.builder().id(hash).mask(mask).build();
        });
    }

    public XodusStore add(StringPointer pointer) {
        env.executeInTransaction(txn -> store.put(txn, stringToEntry(pointer.id()), longToEntry(pointer.mask())));
        return this;
    }


    @Override
    public XodusStore open() throws IOException {
        env = Environments.newContextualInstance(fileName);
        store = env.computeInTransaction(txn -> env.openStore("MyStore", StoreConfig.WITHOUT_DUPLICATES, txn));
        return this;
    }

    @Override
    public XodusStore close() throws IOException {
        env.close();
        return this;
    }
}
