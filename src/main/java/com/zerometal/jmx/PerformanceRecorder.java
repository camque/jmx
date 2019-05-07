package com.zerometal.jmx;

import static javax.ejb.ConcurrencyManagementType.BEAN;

import java.lang.management.ManagementFactory;
import java.text.MessageFormat;
import java.util.HashMap;
import java.util.Map;
import java.util.Set;
import java.util.concurrent.locks.Lock;
import java.util.concurrent.locks.ReentrantLock;

import javax.annotation.PostConstruct;
import javax.annotation.PreDestroy;
import javax.ejb.Asynchronous;
import javax.ejb.ConcurrencyManagement;
import javax.ejb.LocalBean;
import javax.ejb.Singleton;
import javax.ejb.Startup;
import javax.management.InstanceAlreadyExistsException;
import javax.management.InstanceNotFoundException;
import javax.management.MBeanRegistrationException;
import javax.management.MBeanServer;
import javax.management.MalformedObjectNameException;
import javax.management.NotCompliantMBeanException;
import javax.management.ObjectName;

import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;

import com.zerometal.jmx.ejb.IPerformanceMonitorMXBean;
import com.zerometal.jmx.ejb.IPerformanceRecorder;

/**
 * Performance beans
 * @author zerometal
 */
@Singleton
@Startup
@ConcurrencyManagement(BEAN)
@LocalBean
public class PerformanceRecorder implements IPerformanceRecorder {

	/** System log */
	private static final Logger LOG = LogManager.getLogger(PerformanceRecorder.class);

	private static final String MBEAN_NAME = "{0}:type={1}";
	private Map<String, IPerformanceMonitorMXBean> metrics;
	private Map<String, Lock> locks;
	private MBeanServer platformMBeanServer;

	public PerformanceRecorder() {}

	@SuppressWarnings("rawtypes")
	@Override
	@Asynchronous
	public void registerEvent(Class clazz, String component, long transactionTime, boolean isSuccess) {
		final String key = MessageFormat.format(MBEAN_NAME, clazz.getName(), component);
		final Lock lock = this.getLock(key);

		IPerformanceMonitorMXBean bean = null;

		lock.lock();
		try {
			bean = this.metrics.get(key);
			if (bean == null) {
				bean = this.registerInJMX(key);
			}

		} finally {
			lock.unlock();
		}

		bean.setLastTransactionTimeInMillis(transactionTime);
		if (isSuccess) {
			bean.incrementProcessed();
		}
		else {
			bean.incrementFailedProcesses();
		}
	}

	private synchronized Lock getLock(final String key) {
		Lock lock = this.locks.get(key);
		if (lock == null) {
			lock = new ReentrantLock();
			this.locks.put(key, lock);
		}

		return lock;
	}

	private IPerformanceMonitorMXBean registerInJMX(final String beanName) {

		try {
			IPerformanceMonitorMXBean bean;
			final ObjectName objectName = new ObjectName(beanName);

			if (this.platformMBeanServer.isRegistered(objectName)) {
				this.platformMBeanServer.unregisterMBean(objectName);
			}

			bean = new PerformanceMonitor();

			this.metrics.put(beanName, bean);
			this.platformMBeanServer.registerMBean(bean, objectName);

			return bean;

		} catch (MalformedObjectNameException | InstanceAlreadyExistsException | MBeanRegistrationException | NotCompliantMBeanException | InstanceNotFoundException e) {
			LOG.error("Problem during registration of Monitoring into JMX:", e);
			throw new IllegalStateException("Problem during registration of Monitoring into JMX:" + e, e);
		}

	}

	@PostConstruct
	public void registerInServer() {
		this.metrics = new HashMap<>();
		this.platformMBeanServer = ManagementFactory.getPlatformMBeanServer();
		this.locks = new HashMap<>();

	}

	@PreDestroy
	public void unregisterFromJMX() {
		try {
			final Set<String> keys = this.metrics.keySet();

			ObjectName objectName;
			for (final String key : keys) {
				objectName = new ObjectName(key);
				this.platformMBeanServer.unregisterMBean(objectName);
			}

		} catch (final Exception e) {
			throw new IllegalStateException("Problem during unregistration of Monitoring into JMX:" + e, e);
		}
	}

}