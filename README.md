# java-native-benchmark
JMH performance benchmark for Java's native call APIs: [JNI](https://docs.oracle.com/en/java/javase/12/docs/specs/jni/index.html) (via [JavaCpp](https://github.com/bytedeco/javacpp) ), [JNA](https://github.com/java-native-access/jna), [JNR](https://github.com/jnr/jnr-ffi), [Bridj](https://github.com/nativelibs4java/BridJ) and [JDK JEP-424](https://openjdk.org/jeps/424) Foreign Function/Memory APIs (Preview).

## Updated May 23, 2025
The original [repo](https://github.com/zakgof/java-native-benchmark) (which this repo fork from) haven't updated for a while, and it was base on window api. So basically
this repo replace former Windows `GetSystemTime` api to Linux `clock_gettime` api, and compare the result with Java `System.nanoTime` as baseline and use the latest environment including 
JDK and related dependencies
## Benchmark operation ##
Get nano sec from linux api [clock_gettime](https://linux.die.net/man/3/clock_gettime)

````cpp
int clock_gettime(clockid_t clk_id, struct timespec *tp);
````
with the timespec structure defined as
````cpp
struct timespec {
    time_t   tv_sec;        /* seconds */
    long     tv_nsec;       /* nanoseconds */
};
````
Each implementation will
1. allocate memory for the `timespec` struct
2. call native method `clock_gettime` passing the allocated memory
3. collect the final nano time by multiply tv_sec with `1_000_000_000L` then plus tv_nsec, hopefully get the equivalent result as Java `System.nanoTime`

In a separate benchmark I measured performance of the native call only  (item 2).

## How to run ##

Make sure that gradle is configured with a JDK 24 and run
````
gradlew clean jmh
````

## Results ##

**System**:

- Intel(R) Xeon(R) Platinum 8336C CPU @ 2.30GHz
- Debian GNU/Linux 10 (buster)
- openjdk-24.0.1
- gradle-8.14.0
```
Full benchmark (average time, smaller is better)

Benchmark                   Mode  Cnt     Score       Error  Units
JmhNanoTime.java_nano_time  avgt   10    23.649 ±     0.151  ns/op
JmhNanoTime.foreign         avgt   10    98.892 ±    32.292  ns/op
JmhNanoTime.jnr             avgt   10   185.246 ±    34.042  ns/op
JmhNanoTime.jna             avgt   10  2167.672 ±  1376.484  ns/op
JmhNanoTime.jni_javacpp     avgt   10  4926.206 ± 11352.754  ns/op
```

Now let's look into performance of the native call only, stripping out the struct allocation and field access:

````
Native call only (average time, smaller is better)

Benchmark                   Mode  Cnt     Score       Error  Units
JmhCallOnly.java_nano_time  avgt   10    23.789 ±     0.997  ns/op
JmhCallOnly.foreign         avgt   10    31.533 ±     1.878  ns/op
JmhCallOnly.jnr             avgt   10   110.965 ±     0.656  ns/op
JmhCallOnly.jna             avgt   10   664.699 ±    29.632  ns/op
JmhCallOnly.jni_javacpp     avgt   10  2026.679 ±  2297.273  ns/op
````
The order is nearly the same, and Panama is a leader.

**JNI**     
JNI is a Java's standard way to call native code present in JDK since its early versions. JNI requires building a native stub as an adapter between Java and native library, so is considered low-level. Helper tools have been developed in order to automate and simplify native stub generation. Here I used [JavaCpp](https://github.com/bytedeco/javacpp), the project is known for prebaking Java wrappers around high-performant C/C++ libraries such as OpenCV and ffmpeg.
JavaCpp comes with ready-to-use wrappers for widely used system libraries, including Windows API lib, so I used them in this benchmark.

**JNA**     
JNA resolves the burden of writing native wrapper by using a native stub that calls the target function dynamically. It only requires writing Java code and provides mapping to C structs and unions, however, for complex libraries writing Java API that matched a native lib's C API still might be a big task. JNA also provides prebaked Java classes for Windows API. Wrapping the calls dynamically results in high performance overhead comparing to JNI.

**JNA Direct**    
JNA's direct mode claims to "improve performance substantially, approaching that of custom JNI". That should be well seen then calls are using mostly primitive types for arguments and return values.   

~~**BriJ**~~     
Bridj is an attempt to provide a Java to Cpp interop solution similar to JNA (without a need of writing and compiling native code), it claims to provide better performance using dyncall and hand-optimized assembly tweaks. A tool named JNAerator helps to generate java classed from the native library headers. The Bridj projects seems to be abandoned now.
> ***`Bridj` is not under maintain anymore, after thousands failure, I give it up to make it run properly, so the result and related code is removed. but PR is welcomed in case someone success.***

**JNR**     
JNR is a comparingly young project that target the same problem. Similarly as JNA or Bridj it does not require native programming. There's not much documentation or reviews at the moment, but JNR is often called promising.

**JDK Foreign Function/Memory API Preview (JEP-424)**
API by which Java programs can interoperate with code and data outside of the Java runtime.

**Pure Java**    
just return `System.nanoTime()` as baseline
