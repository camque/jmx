package com.zerometal.jmx.ejb;

import javax.ejb.Local;

import com.zerometal.jmx.dto.PerformanceEventDTO;

@Local
public interface IPerformanceRecorder {

	void receiveEvent(final PerformanceEventDTO event);

	IPerformanceMonitorMXBean registerInJMX(String beanName);

}
