package com.zakgof.jnbenchmark.jna;

import com.zakgof.jnbenchmark.Common;

import java.util.List;

import static com.zakgof.jnbenchmark.Common.CLOCK_MONOTONIC;

public class JnaBenchmark {

	public static final class Timespec extends com.sun.jna.Structure {
		public long tv_sec;   // 秒
		public long tv_nsec;  // 纳秒

		@Override
		protected List<String> getFieldOrder() {
			return List.of("tv_sec", "tv_nsec");
		}
	}

	public interface LibC extends com.sun.jna.Library {
		LibC INSTANCE = com.sun.jna.Native.load("c", LibC.class);

		int clock_gettime(int clk_id, Timespec ts);
	}

	private static final Timespec preallocateTimespec = new Timespec();

	public static void callOnly() {
		LibC.INSTANCE.clock_gettime(CLOCK_MONOTONIC, preallocateTimespec);
	}

	public static long all() {
		final Timespec ts = new Timespec();
		if (LibC.INSTANCE.clock_gettime(CLOCK_MONOTONIC, ts) != 0) {
			throw new RuntimeException("clock_gettime failed");
		}
		return Common.convertToNano(ts.tv_sec, ts.tv_nsec);
	}
}
