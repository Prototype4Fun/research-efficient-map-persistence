package org.prototype4fun.experimental.data;

import com.fasterxml.jackson.annotation.JsonProperty;
import com.fasterxml.jackson.databind.annotation.JsonSerialize;
import org.immutables.value.Value;

/**
 * Created by Radek on 09.11.2016.
 */
@Value.Immutable
@JsonSerialize
public interface StringPointer {

    @JsonProperty
    @Value
    String id();

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
