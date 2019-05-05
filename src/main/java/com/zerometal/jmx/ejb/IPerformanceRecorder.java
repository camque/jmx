package com.zerometal.jmx.ejb;

import com.zerometal.jmx.dto.PerformanceEventDTO;

public interface IPerformanceRecorder {

	String JNDI_CLIENT = "java:global/tpmlm/tpmlm-ejb/PerformanceRecorder!com.zerometal.jmx.ejb.IPerformanceRecorder";

	void receiveEvent(final PerformanceEventDTO event);

}
