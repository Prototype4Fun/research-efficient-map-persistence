package org.prototype4fun.experimental.data;

import com.google.common.hash.HashFunction;
import com.google.common.hash.Hasher;
import com.google.common.hash.Hashing;
import org.apache.commons.lang3.RandomStringUtils;
import org.jetbrains.annotations.NotNull;
import org.openjdk.jmh.annotations.Level;
import org.openjdk.jmh.annotations.Scope;
import org.openjdk.jmh.annotations.Setup;
import org.openjdk.jmh.annotations.State;

import java.io.IOException;
import java.nio.charset.Charset;
import java.nio.file.Files;
import java.nio.file.Paths;
import java.util.*;

/**
 * Created by Radek on 17.11.2016.
 */
@State(Scope.Benchmark)
public class Generator {

    public static final HashFunction sha1 = Hashing.sha1();
    public static final HashFunction sha256 = Hashing.sha256();

    public static final String CAPS = "ABCDEFGHIJKLMNOPQRSTUVWXYZ";
    public static final String LOWS = CAPS.toLowerCase();
    public static final String LETT = CAPS + LOWS;
    public static final String NUMS = "1234567890";
    public static final String ALLS = CAPS + LOWS + NUMS;
    public static final Charset CHARSET = Charset.defaultCharset();
    public static final Random RANDOM = new Random(System.currentTimeMillis());

    public String sha1(List<String> predicates, int num) {
        return sha(predicates, num, sha1.newHasher());
    }

    public byte[] sha1B(List<byte[]> predicates, int num) {
        return shaB(predicates, num, sha1.newHasher());
    }

    public String sha256(List<String> predicates, int num) {
        return sha(predicates, num, sha256.newHasher());
    }

    @NotNull
    private byte[] shaB(List<byte[]> predicates, int num, Hasher h) {
        for(int i=0;i<num;i++){
            h.putBytes(predicates.get(RANDOM.nextInt(predicates.size())));
        }
        return h.hash().asBytes();
    }

    @NotNull
    private String sha(List<String> predicates, int num, Hasher h) {
        for(int i=0;i<num;i++){
            h.putString(predicates.get(RANDOM.nextInt(predicates.size())),CHARSET);
        }
        return h.hash().toString();
    }

}
