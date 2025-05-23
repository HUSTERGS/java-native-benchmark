package com.zakgof.jnbenchmark.foreign;

import java.lang.foreign.Arena;
import java.lang.foreign.FunctionDescriptor;
import java.lang.foreign.Linker;
import java.lang.foreign.MemorySegment;
import java.lang.foreign.SymbolLookup;
import java.lang.invoke.MethodHandle;

import static com.zakgof.jnbenchmark.Common.CLOCK_MONOTONIC;
import static java.lang.foreign.ValueLayout.ADDRESS;
import static java.lang.foreign.ValueLayout.JAVA_INT;
import static java.lang.foreign.ValueLayout.JAVA_LONG;

public class JdkForeignBenchmark {
    private static final MethodHandle mhClockGetTime;
    private static final MemorySegment preallocateSegment = Arena.ofAuto().allocate(JAVA_LONG.byteSize() * 2);

    static {
        final Linker linker = Linker.nativeLinker();
        final SymbolLookup lookup = linker.defaultLookup();
        final MemorySegment clockGettime = lookup.find("clock_gettime").orElseThrow();
        mhClockGetTime = linker.downcallHandle(
                clockGettime,
                FunctionDescriptor.of(
                        JAVA_INT,
                        JAVA_INT,
                        ADDRESS
                )
        );
    }

    public static long all() {
        try (final Arena arena = Arena.ofConfined()) {
            // TODO: try struct layout
            final MemorySegment timeSpec = arena.allocate(JAVA_LONG.byteSize() * 2);
            int result = (int) mhClockGetTime.invokeExact(CLOCK_MONOTONIC, timeSpec);
            if (result != 0) {
                throw new RuntimeException("clock_gettime failed for ret value check");
            }
            long tv_sec = timeSpec.get(JAVA_LONG, 0);
            long tv_nsec = timeSpec.get(JAVA_LONG, JAVA_LONG.byteSize());
            return tv_sec * 1_000_000_000L + tv_nsec;
        } catch (Throwable e) {
            throw new RuntimeException(e);
        }
    }

    public static void callOnly() {
        try {
            int ret = (int) mhClockGetTime.invokeExact(CLOCK_MONOTONIC, preallocateSegment);
        } catch (Throwable e) {
            throw new RuntimeException(e);
        }
    }
}
