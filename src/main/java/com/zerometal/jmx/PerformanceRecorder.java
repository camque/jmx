package com.zerometal.jmx;

import static javax.ejb.ConcurrencyManagementType.BEAN;

import java.lang.management.ManagementFactory;
import java.util.HashMap;
import java.util.Map;
import java.util.Set;
import java.util.concurrent.locks.Lock;
import java.util.concurrent.locks.ReentrantLock;

import javax.annotation.PostConstruct;
import javax.ejb.Asynchronous;
import javax.ejb.ConcurrencyManagement;
import javax.ejb.EJB;
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

import com.zerometal.jmx.dto.PerformanceEventDTO;
import com.zerometal.jmx.ejb.IPerformanceMonitorMXBean;
import com.zerometal.jmx.ejb.IPerformanceRecorder;


/**
 * Session Bean implementation class PerformanceRecorder
 */
@Singleton
@Startup
@ConcurrencyManagement(BEAN)
@EJB(name="PerformanceRecorder", beanInterface=IPerformanceRecorder.class, beanName="PerformanceRecorder")
public class PerformanceRecorder implements IPerformanceRecorder {

	/** System log */
	private static final Logger LOG = LogManager.getLogger(PerformanceRecorder.class);

	private static final String MBEAN_NAME = "com.mobiera.tpmlm.%s:type=%s";

	private Map<String, IPerformanceMonitorMXBean> metrics;

	private Map<String, Lock> locks;

	private MBeanServer platformMBeanServer;

	/**
	 * Default constructor.
	 */
	public PerformanceRecorder() {
		super();
	}

	@Override
	@Asynchronous
	public void receiveEvent(final PerformanceEventDTO event) {

		final String key = String.format(MBEAN_NAME, event.getRoot(), event.getComponent());
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

		bean.setLastTransactionTimeInMillis(event.getTransactionInMillis());
		if (event.isSucess()) {
			bean.incrementProcessed();
		} else {
			bean.incrementFailedProcesses();
		}

	}

	/**
	 * Gets the lock.
	 *
	 * @param key
	 *            the key
	 * @return the lock
	 */
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

		} catch (MalformedObjectNameException | InstanceAlreadyExistsException | MBeanRegistrationException
				| NotCompliantMBeanException | InstanceNotFoundException e) {
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