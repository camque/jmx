package com.zerometal.jmx.ejb;

import javax.ejb.Local;

@Local
public interface IPerformanceRecorder {

	/**
	 * Register an event on jmx
	 * @param clazz
	 * @param component
	 * @param transactionTime
	 * @param isSuccess
	 */
	@SuppressWarnings("rawtypes")
	void registerEvent(Class clazz, String component, long transactionTime, boolean isSuccess);

}
