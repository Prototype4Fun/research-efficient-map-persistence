package org.prototype4fun.experimental.data;

import com.fasterxml.jackson.annotation.JsonProperty;
import com.fasterxml.jackson.databind.annotation.JsonSerialize;
import org.immutables.value.Value;

import java.nio.ByteBuffer;

/**
 * Created by Radek on 09.11.2016.
 */
@Value.Immutable
@JsonSerialize
@Value.Style(allParameters = true)
public interface BytePointer {

    @JsonProperty
    @Value
    byte[] id();

    @JsonProperty
    @Value.Auxiliary
    @Value.Default
    default long mask(){return Long.MAX_VALUE;}
//    default long[] mask() {
//        return new long[]{Long.MAX_VALUE,Long.MAX_VALUE,Long.MAX_VALUE,Long.MAX_VALUE,Long.MAX_VALUE,Long.MAX_VALUE,Long.MAX_VALUE,
//                Long.MAX_VALUE,Long.MAX_VALUE,Long.MAX_VALUE,Long.MAX_VALUE,Long.MAX_VALUE,Long.MAX_VALUE,Long.MAX_VALUE,Long.MAX_VALUE,Long.MAX_VALUE,
//                Long.MAX_VALUE,Long.MAX_VALUE,Long.MAX_VALUE,Long.MAX_VALUE,Long.MAX_VALUE,Long.MAX_VALUE,Long.MAX_VALUE,Long.MAX_VALUE};
//    }



}
