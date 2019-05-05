package com.zerometal.jmx;

import java.util.concurrent.atomic.AtomicLong;

import com.zerometal.jmx.ejb.IPerformanceMonitorMXBean;

public class PerformanceMonitor implements IPerformanceMonitorMXBean {

	protected static final long INIT_VALUE = 0L;

	private final AtomicLong processed;

	private final AtomicLong failedProcesses;

	private final AtomicLong lastTransactionTimeInMillis;

	public PerformanceMonitor() {
		this.processed = new AtomicLong(INIT_VALUE);
		this.failedProcesses = new AtomicLong(INIT_VALUE);
		this.lastTransactionTimeInMillis = new AtomicLong(INIT_VALUE);
	}

	@Override
	public long getProcessed() {
		return this.processed.longValue();
	}

	@Override
	public long getFailedProcesses() {
		return this.failedProcesses.longValue();
	}

	@Override
	public long getSucessProcesses() {
		return this.processed.longValue() - this.failedProcesses.longValue();
	}

	@Override
	public long getLastTransactionTimeInMillis() {
		return this.lastTransactionTimeInMillis.longValue();
	}

	@Override
	public void incrementProcessed() {
		this.processed.getAndIncrement();
	}

	@Override
	public void incrementFailedProcesses() {
		this.processed.getAndIncrement();
		this.failedProcesses.getAndIncrement();
	}

	@Override
	public void setLastTransactionTimeInMillis(final long lastTransactionTimeInMillis) {
		this.lastTransactionTimeInMillis.set(lastTransactionTimeInMillis);
	}

	@Override
	public void reset() {
		this.processed.set(INIT_VALUE);
		this.failedProcesses.set(INIT_VALUE);
		this.lastTransactionTimeInMillis.set(INIT_VALUE);
	}

}
