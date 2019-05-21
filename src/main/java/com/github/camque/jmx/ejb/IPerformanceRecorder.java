package com.github.camque.jmx.ejb;

import javax.ejb.Local;

@Local
public interface IPerformanceRecorder {

	/**
	 * Register an event on jmx
	 * @param clazz Component class
	 * @param component Component name
	 * @param transactionTime Duration
	 * @param isSuccess Result execution
	 */
	@SuppressWarnings("rawtypes")
	void registerEvent(Class clazz, String component, long transactionTime, boolean isSuccess);

}
