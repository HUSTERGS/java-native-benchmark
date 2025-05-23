package com.zakgof.jnbenchmark.jnr;

import com.zakgof.jnbenchmark.Common;
import jnr.ffi.LibraryLoader;
import jnr.ffi.Runtime;
import jnr.ffi.Struct;

import static com.zakgof.jnbenchmark.Common.CLOCK_MONOTONIC;

public class JnrBenchmark {
	private static final Timespec preallocateTimespec;

	static {
		final Runtime runtime = Runtime.getSystemRuntime();
		preallocateTimespec = new Timespec(runtime);
	}

	public static class Timespec extends Struct {
		public final time_t tv_sec;
		public final Signed64 tv_nsec;

		protected Timespec(Runtime runtime) {
			super(runtime);
			this.tv_sec = new time_t();
			this.tv_nsec = new Signed64();
		}
	}

	public interface LibC {
		LibC INSTANCE = LibraryLoader.create(LibC.class).load("c");

		int clock_gettime(int clock_id, Timespec tp);
	}
	
	public static void callOnly() {
		LibC.INSTANCE.clock_gettime(CLOCK_MONOTONIC, preallocateTimespec);
	}
	
	public static long all() {
		final Runtime runtime = Runtime.getSystemRuntime();
		final Timespec ts = new Timespec(runtime);
		if (LibC.INSTANCE.clock_gettime(CLOCK_MONOTONIC, ts) != 0) {
			throw new RuntimeException("clock_gettime failed");
		}
		return Common.convertToNano(ts.tv_sec.longValue(), ts.tv_nsec.longValue());
	}
}
