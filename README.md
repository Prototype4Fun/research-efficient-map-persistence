# research-efficient-map-persistence

As we required some persistent map for work project (or maybe rather simple index of sha1 hast to file position pointer), so 
I wanted to evaluate how efficient it is acutally to load/store 256K to 1M simple key-valye objects into memory, then search arround 10% of it. 

Test conditions are to have Key->Pointer map, where Key depending on implementation is either String or BitSet/BigInteger sha1 
hash implementation, while pointer is a primitive long value. It would be nice if storage would be ACID, but it is not really required, 
especially given how long it acutally takes to serialize and save the data. Storage size is very important for me, as in our project we 
keep terabytes of serialized and partitioned data. There is no really ANY atomic storage available out there to solve this problem, none 
SQL or NoSQL, within some reasonable cost (we cannot afford to have TB's just in RAM), so key aspects are size and performance, 
while we sacryfice atomity by using data redundancy.

BTW - I'm still wondering if there is much difference in having ACID storage that takes 5 seconds to store gathered data, 
or non-ACID than can do the same 20x times faster, especially if data in my use-case is dynamically recoverable.

All tests are JHM based, so this is also a nice example of JHM usage. 

There are a couple of libraries that are used in the test

- Oracle Berkely DB JE (v 5.84 - quite old - but this is the last one available on public maven)
- Jetbrains Xodus
- LevelDB
- Old good Property file and Properties object (yeh, its actually cool to check it, it ain;t that bad at all even for large numbers!)
- Simple HashMap with couple of backends:
  - JSON Jackson on Stream IO
  - JSON Jackson on Stream LZ4 Compression
  - JSON Jackson on Memory mapped file
  - My custom Binary serialization based on Stream IO
  - My custom Binary serialization based Memory mapped file
    
  It's not a big suprize but of course - My own custom Binary serialization based Memory mapped file wins (just not in search, but this is
  due to fact that hashcode & equals in BigInteger/BitSet seem to work slower than on String - java8 probably has some optimization for that). 

```
Benchmark                                                                      Mode  Cnt     Score     Error  Units
o.p.e.t01_jackson.JacksonStoreIOStreamBenchmark.loadAndPersist                thrpt   10     1,474 ±   0,090  ops/s
o.p.e.t01_jackson.JacksonStoreIOStreamBenchmark.search10PercentOfStorage      thrpt   10  8493,315 ± 398,056  ops/s
o.p.e.t01_jackson.JacksonStoreLZ4StreamBenchmark.loadAndPersist               thrpt   10     1,173 ±   0,203  ops/s
o.p.e.t01_jackson.JacksonStoreLZ4StreamBenchmark.search10PercentOfStorage     thrpt   10  7685,788 ± 263,598  ops/s
o.p.e.t01_jackson.JacksonStoreMMapBenchmark.loadAndPersist                    thrpt   10     1,190 ±   0,052  ops/s
o.p.e.t01_jackson.JacksonStoreMMapBenchmark.search10PercentOfStorage          thrpt   10  7949,114 ± 360,477  ops/s
o.p.e.t04_customBinary.BinaryStoreIOStreamBenchmark.loadAndPersist            thrpt   10     3,371 ±   0,326  ops/s
o.p.e.t04_customBinary.BinaryStoreIOStreamBenchmark.search10PercentOfStorage  thrpt   10  2483,056 ± 149,276  ops/s
o.p.e.t04_customBinary.BinaryStoreMMapBenchmark.loadAndPersist                thrpt   10     2,921 ±   0,211  ops/s
o.p.e.t04_customBinary.BinaryStoreMMapBenchmark.search10PercentOfStorage      thrpt   10  2463,596 ± 183,172  ops/s
o.p.e.t06_bdb7.BdbStoreBenchmark.loadAndPersist                               thrpt   10     0,226 ±   0,017  ops/s
o.p.e.t06_bdb7.BdbStoreBenchmark.search10PercentOfStorage                     thrpt   10   122,316 ±   4,907  ops/s
o.p.e.t07_levelDB.LevelDBStoreBenchmark.loadAndPersist                        thrpt   10     0,521 ±   0,052  ops/s
o.p.e.t07_levelDB.LevelDBStoreBenchmark.search10PercentOfStorage              thrpt   10   171,040 ±  13,042  ops/s
```
