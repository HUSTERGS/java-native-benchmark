package com.zakgof.jnbenchmark;

import com.zakgof.jnbenchmark.foreign.JdkForeignBenchmark;
import com.zakgof.jnbenchmark.java.JustJava;
import com.zakgof.jnbenchmark.jna.JnaBenchmark;
import com.zakgof.jnbenchmark.jni.JavaCppStock;
import com.zakgof.jnbenchmark.jnr.JnrBenchmark;
import org.openjdk.jmh.annotations.Benchmark;
import org.openjdk.jmh.annotations.BenchmarkMode;
import org.openjdk.jmh.annotations.Measurement;
import org.openjdk.jmh.annotations.Mode;
import org.openjdk.jmh.annotations.OutputTimeUnit;
import org.openjdk.jmh.annotations.Scope;
import org.openjdk.jmh.annotations.State;
import org.openjdk.jmh.annotations.Warmup;

import java.util.concurrent.TimeUnit;

@BenchmarkMode(Mode.AverageTime)
@Warmup(iterations = 5, time = 500, timeUnit = TimeUnit.MILLISECONDS)
@Measurement(iterations = 10, time = 500, timeUnit = TimeUnit.MILLISECONDS)
@State(Scope.Thread)
@OutputTimeUnit(TimeUnit.NANOSECONDS)
public class JmhNanoTime {
	@Benchmark
	public void java_nano_time() throws InterruptedException {
		JustJava.java_nano_time();
	}

	@Benchmark
	public void jni_javacpp() throws InterruptedException {
		JavaCppStock.all();
	}

	@Benchmark
	public void jna() throws InterruptedException {
		JnaBenchmark.all();
	}

	@Benchmark
	public void jnr() throws InterruptedException {
		JnrBenchmark.all();
	}

	@Benchmark
	public void foreign() throws InterruptedException {
		JdkForeignBenchmark.all();
	}
}
