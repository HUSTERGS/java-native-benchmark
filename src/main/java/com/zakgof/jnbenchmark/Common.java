package com.zakgof.jnbenchmark;

public class Common {
    public static final int CLOCK_MONOTONIC = 1;

    public static long convertToNano(long sec, long nsec) {
        return sec * 1_000_000_000L + nsec;
    }
}
