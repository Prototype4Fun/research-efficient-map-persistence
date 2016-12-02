//package org.prototype4fun.experimental.t08_mapdb;
//
//import com.fasterxml.jackson.databind.ObjectMapper;
//import com.fasterxml.jackson.datatype.guava.GuavaModule;
//import com.fasterxml.jackson.datatype.jdk8.Jdk8Module;
//import org.apache.commons.io.FileUtils;
//import org.mapdb.DB;
//import org.mapdb.DBMaker;
//import org.prototype4fun.experimental.data.Store;
//import org.prototype4fun.experimental.data.StringPointer;
//
//import java.io.IOException;
//import java.nio.file.Files;
//import java.nio.file.Path;
//import java.nio.file.Paths;
//import java.util.HashMap;
//import java.util.Map;
//
///**
// * Created by SG0218109 on 2016-11-20.
// */
//public class MapDBStore implements Store<String,StringPointer> {
//
//    final Path base;
//
//    public MapDBStore(String path) {
//        base = Paths.get(path).toAbsolutePath();
//    }
//
//    public Store open() throws IOException {
//        Files.createDirectories(base.getParent());
//        DB db = DBMaker.fileDB()
//        return this;
//    }
//
//    public Store close() throws IOException {
//        return this;
//    }
//
//    @Override
//    public StringPointer get(String key) {
//
//    }
//
//    @Override
//    public Store add(StringPointer pointer) {
//        return null;
//    }
//}
