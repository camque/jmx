package com.github.camque.jmx.ejb;

public interface IPerformanceMonitorMXBean {

	long getProcessed();

	long getFailedProcesses();

	long getSucessProcesses();

	long getLastTransactionTimeInMillis();

	void incrementProcessed();

	void incrementFailedProcesses();

	void setLastTransactionTimeInMillis(long lastTransactionTimeInMillis);

	void reset();

}
