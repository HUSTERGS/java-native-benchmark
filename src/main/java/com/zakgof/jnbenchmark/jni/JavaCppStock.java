package com.zakgof.jnbenchmark.jni;

import org.bytedeco.javacpp.chrono.SteadyTime;

public class JavaCppStock {
	private static final SteadyTime preallocatedSteadyTime = new SteadyTime();

	public static void callOnly() {
		preallocatedSteadyTime.time_since_epoch();
	}

	public static long all() {
        try (final SteadyTime st = new SteadyTime()) {
			return st.time_since_epoch().count();
        }
	}
}
