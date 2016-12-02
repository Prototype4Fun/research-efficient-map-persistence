package org.prototype4fun.experimental.t06_bdb7;

import com.sleepycat.persist.model.Entity;
import com.sleepycat.persist.model.PrimaryKey;
import org.prototype4fun.experimental.data.StringPointer;

/**
 * Created by SG0218109 on 2016-11-19.
 */
@Entity
public class BdbPointer implements StringPointer {

    public BdbPointer(String id,long mask) {
        this.mask = mask;
        this.id = id;
    }

    long mask;

    @PrimaryKey
    String id;

    @Override
    public String id() {
        return id;
    }

    @Override
    public long mask() {
        return mask;
    }

    public BdbPointer() {
    }
}
