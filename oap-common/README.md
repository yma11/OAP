# OAP Common

OAP commoan package includes native libraries and JNI interface for Intel Optane PMem.

## Prerequisites
Below libraries need to be installed in the machine

- [Memkind](http://memkind.github.io/memkind/)

- [Vmemcache](https://github.com/pmem/vmemcache)

## Building

```
mvn clean package -Ppersistent-memory,vmemcache
```

## Run benchmark

```
mvn clean install -Ppersistent-memory
java -jar target/benchmarks.jar PMemStreamWriteTest -f 1
java -jar target/benchmarks.jar PMemDirectWriteTest -f 1
java -jar target/benchmarks.jar PMemStreamReadTest -f 1
java -jar target/benchmarks.jar PMemDirectReadTest -f 1
```
