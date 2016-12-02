package org.prototype4fun.experimental.data;

import java.io.IOException;
import java.util.Collection;

/**
 * Created by Radek on 17.11.2016.
 */
public interface Store<KeyT, PointerT> {

    int DefaultSize = (int) Math.pow(2, 18);


    PointerT get(KeyT key);

    Store add(PointerT pointer);

    default Store add(Collection<PointerT> pointers) throws IOException {
        pointers.forEach(this::add);
        return this;
    }

    default Store open() throws IOException {
        return this;
    }

    default Store close() throws IOException {
        return this;
    }

    default Store synchronize() throws IOException {
        return this;
    }

}
